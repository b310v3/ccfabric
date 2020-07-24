/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.function.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
import java.io.File;
import java.io.FileWriter;
import java.lang.*;
import java.util.concurrent.TimeUnit;

import javax.crypto.KeyAgreement;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractEvent;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import com.rabbitmq.client.*;

public class ClientApp {

	private static java.util.function.Consumer<ContractEvent> contractListener;
	private static final BlockingQueue<ContractEvent> contractEvents = new LinkedBlockingQueue<>();
	private final static String EXCHANGE_NAME = "getupandwork";
    private final static String CHAIN_NAME = "A-fabric-chain";
    private final static String PEER_IP = "localhost:9051";
    private final static String QUORUM_ADDRESS = "0x7e2e058ea0717d63dA6C7791ACEE54fd8eb0e0c2";
    private final static String QUORUM_ENODE = "enode://f0940e74c7c7cdf5937118c36a6007b8cb9b920c7c39b554b509804d92ac2695f69c9185439d3b6c1d4861734b6e308d8557e3d5f354cb45481f31ad7ba2bfd9@140.118.109.132:21000?discport=0&raftport=50000";
    private final static String MQ_HOST = "140.118.109.132";
    private final static String USERNAME = "belove";
	private final static String PASSWORD = "oc886191";
	
	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	public static void main(final String[] args) throws Exception {
		
		// Load a file system based wallet for managing identities.
		final Path walletPath = Paths.get("wallet");
		final Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		final Path networkConfigPath = Paths.get("..", "..", "test-network", "organizations", "peerOrganizations",
				"org2.example.com", "connection-org2.yaml");

		final Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);

		// create a gateway connection
		try (Gateway gateway = builder.connect()) {

			// get the network and contract
			final Network network = gateway.getNetwork("mychannel");
			final Contract contract = network.getContract("fabcar");

			// send crosschain request to mqtt server
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(MQ_HOST);
			factory.setPort(5672);
			factory.setUsername(USERNAME);
			factory.setPassword(PASSWORD);
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.exchangeDeclare("getupandwork", "direct", true);
			String replyQueueName = channel.queueDeclare().getQueue();

			contractListener = contract.addContractListener(contractEvents::add, "crosschain");
			System.out.println("====== Start Moinitoring ======");

			while (true) {
				ContractEvent event = getContractEvent();
				System.out.println(new String(event.getPayload().get()));
				JSONObject jevent = new JSONObject(new String(event.getPayload().get()));

				// setting for RPC contractEvents.peek() != nullcall
				final String corrId = UUID.randomUUID().toString();
				AMQP.BasicProperties props = new AMQP.BasicProperties
					.Builder()
					.correlationId(corrId)
					.replyTo(replyQueueName)
					.build();
				// Create discovery info json
				JSONObject jobj = new JSONObject();
				jobj.put("targetchain", jevent.getString("targetchain"));
				jobj.put("sourcechain", jevent.getString("ownerchain"));
				jobj.put("sourceadd", QUORUM_ADDRESS);
				jobj.put("sourceenode", QUORUM_ENODE);

				channel.basicPublish(EXCHANGE_NAME, "Discovery_Service", props, jobj.toString().getBytes("UTF-8"));
			

				// handle the peerlist from mqtt server
				System.out.println("... Waiting for server reply request ...");

				DeliverCallback deliverCallback = (consumerTag, delivery) -> {
					String message = new String(delivery.getBody(), "UTF-8");
					System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
					
					JSONObject peerlist = new JSONObject(message);
					
					// create the file to generate quorum
					JSONObject genisisfile = CreateGenisis(peerlist);
					JSONArray staticnodesfile = CreateStatic(peerlist);
					JSONObject files = new JSONObject();
					files.put("genisis", genisisfile);
					files.put("static-nodes", staticnodesfile);

					String[] ip  = new String[4];
					JSONObject peer1 = new JSONObject();
					JSONObject peer2 = new JSONObject();
					JSONObject peer3 = new JSONObject();
					JSONObject peer4 = new JSONObject();
					peer1 = peerlist.getJSONObject("peer1");
					peer2 = peerlist.getJSONObject("peer2");
					peer3 = peerlist.getJSONObject("peer3");
					peer4 = peerlist.getJSONObject("peer4");
					ip[0] = peer1.getString("peerip");
					ip[1] = peer2.getString("peerip");
					ip[2] = peer3.getString("peerip");
					ip[3] = peer4.getString("peerip");


					// Save the genisis and static-nodes files
					JsonWriter(genisisfile);
					JsonWriter(staticnodesfile);
					
					// start up quorum first and get the smart contract address
					long startTime = System.nanoTime();
					try {
						RunQuorum quorum = new RunQuorum();
						quorum.startQuorum();
						System.out.println("quorum start");

						// sending two config files to other node and start up the network
						for (int i = 1; i < 4; i++) {
							channel.basicPublish(EXCHANGE_NAME, ip[i], null, files.toString().getBytes("UTF-8"));
						}
						
						// Deploy the smart contract and wait for all peer to consensus it
						String contractAddress = quorum.Deploy(); // get the contract address, NEED TO CHECK IS IT EMPTY!!!!!
						System.out.println("quorum contract : " + contractAddress);
						quorum.PushCrossChain(jevent.getString("ownerpeer"), ip[3], jevent.getString("info"), jevent.getString("ownerchain"), jevent.getString("targetchain")); //insert crosschain data into quorum
						System.out.println("quorum push succdess?");
						JSONObject address = new JSONObject();
						address.put("contractaddr", contractAddress);
						//files.put("contractaddr", contractAddress); // save the address into the json
						
						// call RunQuorun.java and send the eventdata, save the files.
						for (int i = 1; i < 4; i++) {
							channel.basicPublish(EXCHANGE_NAME, ip[i], null, address.toString().getBytes("UTF-8"));
						}
						System.out.println("quorum file send");
						// now need to wait end event and save the transaction and shut down quorum
						boolean close = false;
						while(close == false) {
							close = quorum.CheckClose();
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
					long endTime = System.nanoTime();
					long totalTime = endTime - startTime;
					System.out.println("all process done!, time = " + totalTime / 1000000);
					
				};
				channel.basicConsume(replyQueueName, true, deliverCallback, consumerTag -> { });
				// create the file to generate quorum
				// send the file to all peer
				// boot up quorum
				
			}

			// contract.submitTransaction("validate"/* work on progess for validation */);

			/*
			 * contract.submitTransaction("createCar", "CAR10", "VW", "Polo", "Grey",
			 * "Mary");
			 * 
			 * result = contract.evaluateTransaction("queryCar", "CAR10");
			 * System.out.println(new String(result));
			 * 
			 * contract.submitTransaction("changeCarOwner", "CAR10", "Archie");
			 * 
			 * result = contract.evaluateTransaction("queryCar", "CAR10");
			 * System.out.println(new String(result));
			 */
		}
	}

	private static ContractEvent getContractEvent() throws InterruptedException { //String expectedPayload
		//final List<String> payloads = new ArrayList<>();
		/*final ContractEvent matchingEvent = removeFirstMatch(contractEvents, event -> {
			String eventPayload = event.getPayload().toString();
			return expectedPayload.equals(eventPayload);
		});*/
		ContractEvent matchingEvent = removeFirstMatch(contractEvents);
		
		return matchingEvent;
	}

	/**
	 * Remove and return the first element matching the given predicate. All other
	 * elements remain on the queue.
	 * 
	 * @param queue A queue.
	 * @param match Filter used to match queue elements.
	 * @return The first matching element or null if no matches are found.
	 * @throws InterruptedException If waiting for queue elements is interrupted.
	 */
	private static <T> T removeFirstMatch(final BlockingQueue<T> queue) //, final Predicate<? super T> match
			throws InterruptedException {
		//final List<T> unmatchedElements = new ArrayList<>();
        //T element;

        //while ((element = queue.take()) != null) {
			//System.out.println(element);;
            //if (match.test(element)) {
            //    break;
            //}
            //unmatchedElements.add(element);
        //}

        //queue.addAll(unmatchedElements); // Re-queue elements that didn't match
        return queue.take();
	}
	
	// create genisis.json file
	private static JSONObject CreateGenisis(JSONObject obj) {
		JSONObject peer1 = new JSONObject();
		JSONObject peer2 = new JSONObject();
		JSONObject peer3 = new JSONObject();
		JSONObject peer4 = new JSONObject();
		peer1 = obj.getJSONObject("peer1");
		peer2 = obj.getJSONObject("peer2");
		peer3 = obj.getJSONObject("peer3");
		peer4 = obj.getJSONObject("peer4");
		JSONObject balance = new JSONObject();
		balance.put("balance", "1000000000000000000000000000");
		JSONObject peerlist = new JSONObject();
		peerlist.put(peer1.getString("peeraddress"), balance);
		peerlist.put(peer2.getString("peeraddress"), balance);
		peerlist.put(peer3.getString("peeraddress"), balance);
		peerlist.put(peer4.getString("peeraddress"), balance);
		JSONObject genisis = new JSONObject();
		genisis.put("alloc", peerlist);
		genisis.put("coinbase", "0x0000000000000000000000000000000000000000");
		JSONObject config = new JSONObject();
		config.put("homesteadBlock", 0);
		config.put("byzantiumBlock", 0);
		config.put("constantinopleBlock", 0);
		config.put("chainId", 10);
		config.put("eip150Block", 0);
		config.put("eip155Block", 0);
		config.put("eip150Hash", "0x0000000000000000000000000000000000000000000000000000000000000000");
		config.put("eip158Block", 0);
		JSONObject maxCodeSizeConfig = new JSONObject();
		JSONArray configarr = new JSONArray();
		maxCodeSizeConfig.put("block", 0);
		maxCodeSizeConfig.put("size", 64);
		configarr.put(maxCodeSizeConfig);
		config.put("maxCodeSizeConfig", configarr);
		config.put("isQuorum", true);
		genisis.put("config", config);
		genisis.put("difficulty", "0x0");
		genisis.put("extraData", "0x0000000000000000000000000000000000000000000000000000000000000000");
		genisis.put("gasLimit", "0xE0000000");
		genisis.put("mixhash", "0x00000000000000000000000000000000000000647572616c65787365646c6578");
		genisis.put("nonce", "0x0");
		genisis.put("parentHash", "0x0000000000000000000000000000000000000000000000000000000000000000");
		genisis.put("timestamp", "0x00");
		return genisis;
	}

	// Create static-node.json file
	private static JSONArray CreateStatic(JSONObject obj) {
		JSONObject peer1 = new JSONObject();
		JSONObject peer2 = new JSONObject();
		JSONObject peer3 = new JSONObject();
		JSONObject peer4 = new JSONObject();
		peer1 = obj.getJSONObject("peer1");
		peer2 = obj.getJSONObject("peer2");
		peer3 = obj.getJSONObject("peer3");
		peer4 = obj.getJSONObject("peer4");
		//String enode = peer1.getString("peerenode") + ", " + peer2.getString("peerenode") + ", " + peer3.getString("peerenode") + ", " + peer4.getString("peerenode");
		JSONArray enodearr = new JSONArray();
		enodearr.put(peer1.getString("peerenode"));
		enodearr.put(peer2.getString("peerenode"));
		enodearr.put(peer3.getString("peerenode"));
		enodearr.put(peer4.getString("peerenode"));
		System.out.println("enodes : " + enodearr.toString());
		return enodearr;
	}

	private static void JsonWriter(JSONObject obj) {
        File jsonFile = new File("/home/belove/quorum/fromscratch/genesis.json");
        
        try (FileWriter file = new FileWriter(jsonFile)) {
 
            file.write(obj.toString());
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private static void JsonWriter(JSONArray obj) {
        File jsonFile = new File("/home/belove/quorum/fromscratch/new-node-1/static-nodes.json");
        
        try (FileWriter file = new FileWriter(jsonFile)) {
 
            file.write(obj.toString());
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}