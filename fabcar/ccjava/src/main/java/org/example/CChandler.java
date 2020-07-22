package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.lang.*;

import com.rabbitmq.client.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class CChandler {
    private final static String EXCHANGE_NAME = "getupandwork";
    private final static String CHAIN_NAME = "A";
    private final static String PEER_IP = "localhost:9051";
    private final static String QUORUM_ADDRESS = "0x01E4300aEc7188d7108880De4fBf2f0691ec797C";
    private final static String QUORUM_ENODE = "8be33cd80714e0c967d9f6c4281c315a9b3879a8ac06626f4a359c49b3280997508b16b555a8083b6ecb53130548e32db38fdb11bcc8381ecfc0615329ea113c";
    private final static String MQ_HOST = "140.118.109.132";
    private final static String USERNAME = "belove";
    private final static String PASSWORD = "oc886191";
    private final static int PEER = 4;
    private final static String PEER_ROUTING_KEY = "peer0.org2.example.com";
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
        channel.queueBind(queueName, EXCHANGE_NAME, PEER_ROUTING_KEY);

        System.out.println("=== Start to waiting crosschain mqtt request ===");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" +
                delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");

            // handle the json files and save to quorum folder and start the quorum up.
            JSONObject files = new JSONObject(message);
            JSONObject genisis = files.getJSONObject("genisis");
            JSONObject staticNodes = files.getJSONObject("static-nodes");
            String contractAddr = files.getString("contractaddr");

            JsonWriter(genisis);
            JsonWriter(staticNodes);

            // start up quorum
            RunQuorum quorum = new RunQuorum();
            try {
                String[] ccdata = quorum.Deploy(contractAddr, PEER);
                System.out.println("Received crosschain data: " + ccdata[0] + ccdata[1] + ccdata[2] + ccdata[3] + ccdata[4] + ccdata[5]);
            }catch (Exception e) {
                System.out.println("Quorum error!");
            }

            // send the transaction back to the fabric

        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }

    private static void JsonWriter(JSONObject obj) {
        File jsonFile = new File("/home/belove/quorum/fromscratch/genisis.json");
       
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