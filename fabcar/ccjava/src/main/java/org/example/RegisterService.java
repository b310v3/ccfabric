package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import com.rabbitmq.client.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractEvent;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

public class RegisterService {
    private final static String EXCHANGE_NAME = "getupandwork";
    private final static String CHAIN_NAME = "A-fabric-chain";
    private final static String PEER_IP = "peer0.org2.example.com";//"140.118.109.132:9051";
    private final static String QUORUM_ADDRESS = "0x01E4300aEc7188d7108880De4fBf2f0691ec797C";
    private final static String QUORUM_ENODE = "enode://8be33cd80714e0c967d9f6c4281c315a9b3879a8ac06626f4a359c49b3280997508b16b555a8083b6ecb53130548e32db38fdb11bcc8381ecfc0615329ea113c@127.0.0.1:21000?discport=0&raftport=50000";
    private final static String MQ_HOST = "140.118.109.132";
    private final static String USERNAME = "belove";
    private final static String PASSWORD = "oc886191";

    public static void main(final String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(MQ_HOST);
        factory.setPort(5672);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
        String queueName = channel.queueDeclare().getQueue();
        //channel.queueBind(queueName , EXCHANGE_NAME, "peer0.org2.example.com");

        final String corrId = UUID.randomUUID().toString();
        AMQP.BasicProperties props = new AMQP.BasicProperties
            .Builder()
            .correlationId(corrId)
            .replyTo(queueName)
            .build();

        // Let user input the peer quorum address and enode
        //Scanner scanObj = new Scanner(System.in);
        //System.out.println("Address : ");
        //String address = scanObj.nextLine(); 
        //System.out.println("Enode : ");
        //String enode = scanObj.nextLine();

        // Combine all info into json format
        JSONObject jsoninfo = new JSONObject();
        jsoninfo.put("peerchain", CHAIN_NAME);
        jsoninfo.put("peerip", PEER_IP);
        jsoninfo.put("peeraddress", QUORUM_ADDRESS);
        jsoninfo.put("peerenode", QUORUM_ENODE);

        // Send to mqtt register request
        channel.basicPublish(EXCHANGE_NAME, "Register_Service", props, jsoninfo.toString().getBytes("UTF-8"));
        System.out.println(" [x] Sent :'" + jsoninfo.toString() + "'");
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");


        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            if(message.substring(0,3).equals("200")) {
                System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");

                // Load a file system based wallet for managing identities.
                final Path walletPath = Paths.get("wallet");
                final Wallet wallet = Wallets.newFileSystemWallet(walletPath);
                // load a CCP
                final Path networkConfigPath = Paths.get("..", "..", "test-network", "organizations", "peerOrganizations",
                        "org2.example.com", "connection-org2.yaml");

                final Gateway.Builder builder = Gateway.createBuilder();
                builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);

                // create a gateway connection
                try(Gateway gateway = builder.connect()) {
                    // get the network and contract
                    final Network network = gateway.getNetwork("mychannel");
                    final Contract contract = network.getContract("fabcar");

                    contract.submitTransaction("Insertccpeer", "peer0.org2.example.com");
                    System.out.println("Add cc peer into fabric!");
                } catch (Exception e){
                    System.out.println("Error");
                }
            }
            else {
                System.out.println("Register fail!");
                System.exit(0);
            }
        };
        
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        
    }
}