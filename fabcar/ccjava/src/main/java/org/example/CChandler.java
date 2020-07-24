package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.lang.*;
import java.util.concurrent.TimeUnit;

import com.rabbitmq.client.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class CChandler {
    

    private final static String EXCHANGE_NAME = "getupandwork";
    private final static String CHAIN_NAME = "A-fabric-chain";
    private final static String PEER_IP = "localhost:9051";
    private final static String QUORUM_ADDRESS = "0x7e2e058ea0717d63dA6C7791ACEE54fd8eb0e0c2";
    private final static String QUORUM_ENODE = "enode://f0940e74c7c7cdf5937118c36a6007b8cb9b920c7c39b554b509804d92ac2695f69c9185439d3b6c1d4861734b6e308d8557e3d5f354cb45481f31ad7ba2bfd9@140.118.109.132:21000?discport=0&raftport=50000";
    private final static String MQ_HOST = "140.118.109.132";
    private final static String USERNAME = "belove";
    private final static String PASSWORD = "oc886191";
    private final static String PEER_ROUTING_KEY = "peer0.org2.example.com";

    public static void main(final String[] args) throws Exception {
        try {
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

        while(true) {

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
                
                // handle the json files and save to quorum folder and start the quorum up.
                RunQuorum quorum = new RunQuorum();
                JSONObject files = new JSONObject(message);

                try {
                    // check is files or address
                    if (files.has("contractaddr")) {
                        String[] ccdata = quorum.Deploy(files.getString("contractaddr"));
                        System.out.println("Received crosschain data: " + ccdata[0] + ccdata[1] + ccdata[2] + ccdata[3] + ccdata[4]);
                    } else {
                        JSONObject genisis = files.getJSONObject("genisis");
                        JSONArray staticNodes = files.getJSONArray("static-nodes");

                        JsonWriter(genisis);
                        JsonWriter(staticNodes);

                        // Start up quorum
                        quorum.startQuorum();
                        System.out.println("Run quorum Run!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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