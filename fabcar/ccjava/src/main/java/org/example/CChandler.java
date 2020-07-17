package main.java.org.example;

import java.io.FileWriter;
import java.io.IOException;

import com.rabbitmq.client.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class CChandler {
    private final static String QUORUM_ADDRESS = "0x01E4300aEc7188d7108880De4fBf2f0691ec797C";
    private final static String QUORUM_ENODE = "8be33cd80714e0c967d9f6c4281c315a9b3879a8ac06626f4a359c49b3280997508b16b555a8083b6ecb53130548e32db38fdb11bcc8381ecfc0615329ea113c";
    private final static String EXCHANGE_NAME = "getupandwork";
    private final static String PEER_ROUTING_KEY = "peer0.Org2";

    public static void main(final String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("140.118.109.132:5672");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangDeclare(EXCHANGE_NAME, "direct");
        String queueName = channel.queueDeclare.getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, PEER_ROUTING_KEY);

        Syetem.out.println("=== Start to waiting crosschain mqtt request ===");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" +
                delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");

            // handle the json files and save to quorum folder and start the quorum up.
            JSONObject files = new JSONObject(message);
            JSONObject genisis = files.getJSONObject("genisis");
            JSONObject staticNodes = files.getJSONObject("static-nodes");

            JsonWriter(genisis, true);
            JsonWriter(staticNodes, false);

            // start up quorum

        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }

    private void JsonWriter(JSONObject obj, boolean isGenisis) {
        if (isGenisis) {
            File jsonFile = new File("/home/belove/quorum/fromscratch/genisis.json");
        }
        else {
            File jsonFile = new File("/home/belove/quorum/fromscratch/new-node-1/static-nodes.json");
        }
        try (FileWriter file = new FileWriter(jsonFile)) {
 
            file.write(obj.toJSONString());
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}