package es.uji.al259348.sliwserver;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Main {

    private static final String BROKER_HOST = "tcp://0.0.0.0:61613";
    private static final String BROKER_USER = "admin";
    private static final String BROKER_PASSWORD = "password";
    private static final String BROKER_CLIENT_ID = "SliwServer";

    private static final int MSG_QOS = 2;

    private static class MessageToPublish {
        String topic;
        MqttMessage message;
    }

    private static class MqttClientThread extends Thread implements MqttCallback {

        private String brokerHost;
        private String brokerUser;
        private String brokerPass;

        private MqttClient mqttClient;
        private MqttConnectOptions mqttConnectOptions;

        private boolean running = true;
        private BlockingQueue<MessageToPublish> messageToPublishQueue;

        public MqttClientThread(String brokerHost, String brokerUser, String brokerPass) {
            this.brokerHost = brokerHost;
            this.brokerUser = brokerUser;
            this.brokerPass = brokerPass;

            mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(true);
            mqttConnectOptions.setUserName(brokerUser);
            mqttConnectOptions.setPassword(brokerPass.toCharArray());

            messageToPublishQueue = new LinkedBlockingDeque<>();
        }

        public void finish() {
            running = false;
            interrupt();
        }

        @Override
        public void run() {

            try {
                mqttClient = new MqttClient(brokerHost, BROKER_CLIENT_ID, new MemoryPersistence());
                mqttClient.setCallback(this);

                System.out.println("Connecting to " + mqttClient.getServerURI() + " ...");
                mqttClient.connect(mqttConnectOptions);
                System.out.println("Connected!");

                System.out.println("Subscribing to topic: user/linkedTo/a:b:c:d/request");
                mqttClient.subscribe("user/linkedTo/a:b:c:d:e/request");

                while(running) {
                    try {
                        MessageToPublish messageToPublish = messageToPublishQueue.take();
                        String topic = messageToPublish.topic;
                        MqttMessage message = messageToPublish.message;
                        mqttClient.publish(topic, message);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }

                System.out.println("Disconnecting from " + BROKER_HOST + " ...");
                mqttClient.disconnect();
                System.out.println("Disconnected");

            } catch (MqttException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void connectionLost(Throwable cause) {
            System.out.println("Connection lost!!!!");
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            System.out.println("Message delivered to topics: " + Arrays.toString(token.getTopics()));
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            System.out.println("Message arrived for topic (" + topic + "): ");

            String msg = new String(message.getPayload());
            System.out.println(msg);

            if (topic.equals("user/linkedTo/a:b:c:d:e/request")) {
                String responseTopic = "user/linkedTo/a:b:c:d:e/response";

                // Obtener usuario de la base de datos...

                MqttMessage responseMessage = new MqttMessage();
                responseMessage.setPayload(new Date().toString().getBytes());
                responseMessage.setQos(MSG_QOS);
                responseMessage.setRetained(false);

                MessageToPublish response = new MessageToPublish();
                response.topic = responseTopic;
                response.message = responseMessage;

                messageToPublishQueue.put(response);
            }

//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            Sample sample = objectMapper.readValue(msg, Sample.class);
//            System.out.println("sample = " + sample.toString());
//        } catch (Exception e) {
//            //e.printStackTrace();
//            System.out.println(msg);
//        }

        }

    }

    public static void main(String args[]) {

        System.out.println("SliwServer");
        System.out.println("Press <intro> at any moment to stop the server.");
        System.out.println("-----------------------------------------------");

        MqttClientThread t = new MqttClientThread(BROKER_HOST, BROKER_USER, BROKER_PASSWORD);
        t.start();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        t.finish();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

}
