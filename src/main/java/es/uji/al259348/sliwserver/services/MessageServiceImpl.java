package es.uji.al259348.sliwserver.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.uji.al259348.sliwserver.config.MqttConfig;
import es.uji.al259348.sliwserver.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    MqttConfig.MqttGateway messageGateway;

    @Autowired
    private UserService userService;

    private static final String SMARTWATCH_MAC_ADDRESS = "44:d4:e0:fe:f5:3f";
    private static final int MSG_QOS = 2;

    @Override
    public void handleMessage(String topic, String payload, int qos, boolean retained) {
        logger.info("Message received from topic (" + topic + "):");
        logger.info(payload);

        String[] topicFields = topic.split("/");

        if (topicFields[0].equals("user")) {

            if (topicFields[1].equals("linkedTo")) {
                String deviceId = topicFields[2];
                String responseTopic = "user/linkedTo/" + SMARTWATCH_MAC_ADDRESS + "/response";

                // Obtener usuario de la base de datos...
                User user = userService.getUserLinkedTo(SMARTWATCH_MAC_ADDRESS);
                logger.info("User: " + user);

                try {
                    String json = (new ObjectMapper()).writeValueAsString(user);
                    publish(responseTopic, json, MSG_QOS, true);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            } else {
                String userId = topicFields[1];

                // configurar...
            }

        }
    }

    @Override
    public void publish(String topic, String payload, int qos, boolean retained) {
        messageGateway.publish(topic, payload, qos, retained);
    }
}
