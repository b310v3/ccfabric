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
import java.util.concurrent.TimeUnit;
import java.util.UUID;

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

	private static Consumer<ContractEvent> contractListener;
	private static final BlockingQueue<ContractEvent> contractEvents = new LinkedBlockingQueue<>();
	private final static String QUORUM_ADDRESS = "0x01E4300aEc7188d7108880De4fBf2f0691ec797C";
    private final static String QUORUM_ENODE = "8be33cd80714e0c967d9f6c4281c315a9b3879a8ac06626f4a359c49b3280997508b16b555a8083b6ecb53130548e32db38fdb11bcc8381ecfc0615329ea113c";

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

			contractListener = contract.addContractListener(contractEvents::add, "crosschain");
			System.out.println("====== Start Moinitoring ======");
			ClientApp app = new ClientApp();

			while (true) {
				ContractEvent event = app.getContractEvent();
				System.out.println(new String(event.getPayload().get()));
				JSONObject jevent = new JSONObject(new String(event));

				// send crosschain request to mqtt server
				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost("140.118.109.132:5672");
				try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
					channel.exchangeDeclare("getupandwork", "direct");
					String replyQueueName = channel.queueDeclare().getQueue();
					//channel.queueBind(queueName , "getupandwork", "peer0.org2.example.com");

					// setting for RPC call
					final String corrId = UUID.randomUUID().toString();
					AMQP.BasicProperties props = new AMQP.BasicProperties
						.Builder()
						.correlationId(corrId)
						.replyTo(replyQueueName)
						.build();
					// Create discovery info json
					JSONObject jobj = new JSONobject();
					jobj.put("targetchain", "B");
					jobj.put("sourcechain", "A");
					jobj.put("sourceadd", QUORUM_ADDRESS);
					jobj.put("sourceenode", QUORUM_ENODE);

					channel.basicPublish(EXCHANGE_NAME, "Discocery_Service", props, jobj.toString().getBytes("UTF-8"));
				}

				// handle the peerlist from mqtt server (need to think about the pipeline of the requester)
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
					peer1 = obj.getJSONObject("peer1");
					peer2 = obj.getJSONObject("peer2");
					peer3 = obj.getJSONObject("peer3");
					peer4 = obj.getJSONObject("peer4");
					ip[0] = peer1.getString("peerip");
					ip[1] = peer2.getString("peerip");
					ip[2] = peer3.getString("peerip");
					ip[3] = peer4.getString("peerip");

					for (int i = 0; i < 4; i++) {
						channel.basicPublish(EXCHANGE_NAME, ip[i], null, files.toString().getBytes("UTF-8"));
					}
														
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

	private ContractEvent getContractEvent() throws InterruptedException { //String expectedPayload
		final List<String> payloads = new ArrayList<>();
		/*final ContractEvent matchingEvent = removeFirstMatch(contractEvents, event -> {
			String eventPayload = event.getPayload().toString();
			return expectedPayload.equals(eventPayload);
		});*/
		final ContractEvent matchingEvent = removeFirstMatch(contractEvents);
		
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
	private <T> T removeFirstMatch(final BlockingQueue<T> queue) //, final Predicate<? super T> match
			throws InterruptedException {
		final List<T> unmatchedElements = new ArrayList<>();
        T element;

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
	private JSONObject CreateGenisis(JSONObject obj) {
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
		peerlist.put(peer1.getString(peeraddress), balance);
		peerlist.put(peer2.getString(peeraddress), balance);
		peerlist.put(peer3.getString(peeraddress), balance);
		peerlist.put(peer4.getString(peeraddress), balance);
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
		maxCodeSizeConfig.put("size", 35);
		configarr.put(maxCodeSizeConfig);
		config.put("maxCodeSizeConfig", configarr);
		config.put("isQuorum", true);
		genisis.put("config", config);
		genisis.put("difficulty", "0x0");
		genisis.put("extraData", "0x0000000000000000000000000000000000000000000000000000000000000000");
		genisis.put("configgasLimit", "0xE0000000");
		genisis.put("mixhash", "0x00000000000000000000000000000000000000647572616c65787365646c6578");
		genisis.put("nonce", "0x0");
		genisis.put("parentHash", "0x0000000000000000000000000000000000000000000000000000000000000000");
		genisis.put("timestamp", "0x00");
		return genisis;
	}

	// Create static-node.json file
	private JSONObject CreateStatic(JSONObject obj) {
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
		System.out.println("enodes : " + enodearr.getString());
		return enodearr;
	}
}