package es.uji.al259348.sliwserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.uji.al259348.sliwserver.model.Location;
import es.uji.al259348.sliwserver.model.User;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Main {

    private static final String BROKER_HOST = "tcp://0.0.0.0:61613";
    private static final String BROKER_USER = "admin";
    private static final String BROKER_PASSWORD = "password";
    private static final String BROKER_CLIENT_ID = "SliwServer";

    private static final String SMARTWATCH_MAC_ADDRESS = "44:d4:e0:fe:f5:3f";

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

                System.out.println("Subscribing to topic: user/linkedTo/+/request");
                mqttClient.subscribe("user/linkedTo/+/request");

                System.out.println("Subscribing to topic: user/+/configure");
                mqttClient.subscribe("user/+/configure");

                System.out.println("Subscribing to topic: user/+/sample");
                mqttClient.subscribe("user/+/sample");

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
            System.out.println("Message arrived from topic: " + topic);

            String msg = new String(message.getPayload());
            System.out.println(msg);

            String[] topicFields = topic.split("/");

            if (topicFields[0].equals("user")) {

                if (topicFields[1].equals("linkedTo")) {
                    String deviceId = topicFields[2];
                    String responseTopic = "user/linkedTo/" + SMARTWATCH_MAC_ADDRESS + "/response";

                    // Obtener usuario de la base de datos...
                    User user = new User();
                    user.setId("1");
                    user.setName("adrian");

                    Location loc1 = new Location();
                    loc1.setName("Cocina");
                    loc1.setConfigMsg("Vete pa la cocina!");

//                Location loc2 = new Location();
//                loc2.setName("Baño");
//                loc2.setConfigMsg("Vete pal baño");

                    user.setLocations(Arrays.asList(loc1));

                    String json = (new ObjectMapper()).writeValueAsString(user);

                    MqttMessage responseMessage = new MqttMessage();
                    responseMessage.setPayload(json.getBytes());
                    responseMessage.setQos(MSG_QOS);
                    responseMessage.setRetained(false);

                    MessageToPublish response = new MessageToPublish();
                    response.topic = responseTopic;
                    response.message = responseMessage;

                    messageToPublishQueue.put(response);

                } else {
                    String userId = topicFields[1];

                    // configurar...
                }

            }

        }

    }

    public static void main(String args[]) {

        System.out.println("SliwServer");
        System.out.println("Press <intro> at any moment to stop the server.");
        System.out.println("-----------------------------------------------");

        //request().subscribeOn(Schedulers.newThread()).observeOn(Schedulers.newThread()).subscribe();

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

    private static Observable<Void> connect() {
        System.out.println("connect | Thread: " + Thread.currentThread().getName());
        return Observable.create(subscriber -> {
            System.out.println("connect | create | Thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.onCompleted();
        });
    }

    private static Observable<Void> subscribe() {
        System.out.println("subscribe | Thread: " + Thread.currentThread().getName());
        return Observable.create(subscriber -> {
            System.out.println("subscribe | create | Thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.onCompleted();
        });
    }

    private static Observable<Void> publish() {
        System.out.println("publish | Thread: " + Thread.currentThread().getName());
        return Observable.create(subscriber -> {
            System.out.println("publish | create | Thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.onCompleted();
        });
    }

    private static Observable<String> request() {
        System.out.println("request | Thread: " + Thread.currentThread().getName());
        return Observable.create(subscriber -> {
            System.out.println("request | create | Thread: " + Thread.currentThread().getName());
            subscriber.onNext("all");
            subscriber.onCompleted();
            Observable.concat(connect(), subscribe(), publish()).subscribeOn(Schedulers.newThread()).observeOn(Schedulers.newThread()).subscribe();
        });
    }

}
