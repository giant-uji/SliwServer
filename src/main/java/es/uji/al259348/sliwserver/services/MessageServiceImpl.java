package es.uji.al259348.sliwserver.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.uji.al259348.sliwserver.model.Location;
import es.uji.al259348.sliwserver.model.User;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class MessageServiceImpl implements MessageService {

    @Override
    public void handleMessage(String topic, String msg) {
        System.out.println("Message received from topic: " + topic);
        System.out.println(msg);

        String[] topicFields = topic.split("/");

        if (topicFields[0].equals("user")) {

            if (topicFields[1].equals("linkedTo")) {
//                String deviceId = topicFields[2];
//                String responseTopic = "user/linkedTo/" + SMARTWATCH_MAC_ADDRESS + "/response";
//
//                // Obtener usuario de la base de datos...
//                User user = new User();
//                user.setId("1");
//                user.setName("adrian");
//
//                Location loc1 = new Location();
//                loc1.setName("Cocina");
//                loc1.setConfigMsg("Vete pa la cocina!");
//
////                Location loc2 = new Location();
////                loc2.setName("Baño");
////                loc2.setConfigMsg("Vete pal baño");
//
//                user.setLocations(Arrays.asList(loc1));
//
//                String json = (new ObjectMapper()).writeValueAsString(user);
//
//                MqttMessage responseMessage = new MqttMessage();
//                responseMessage.setPayload(json.getBytes());
//                responseMessage.setQos(MSG_QOS);
//                responseMessage.setRetained(false);
//
//                MessageToPublish response = new MessageToPublish();
//                response.topic = responseTopic;
//                response.message = responseMessage;
//
//                messageToPublishQueue.put(response);

            } else {
                String userId = topicFields[1];

                // configurar...
            }

        }

    }

}
