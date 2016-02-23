package es.uji.al259348.sliwserver.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.uji.al259348.sliwserver.config.MqttConfig;
import es.uji.al259348.sliwserver.exceptions.NoSuchDeviceException;
import es.uji.al259348.sliwserver.model.Sample;
import es.uji.al259348.sliwserver.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    MqttConfig.MqttGateway messageGateway;

    @Autowired
    private UserService userService;

    @Autowired
    private SampleService sampleService;

    private static final String SMARTWATCH_MAC_ADDRESS = "44:d4:e0:fe:f5:3f";
    private static final int MSG_QOS = 2;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handleMessage(String topic, String payload, int qos, boolean retained) {
        logger.info("Message received from topic (" + topic + "):");
        logger.info(payload);

        String[] topicFields = topic.split("/");

        if (topicFields[0].equals("user")) {

            if (topicFields[1].equals("linkedTo")) {
                String deviceId = topicFields[2];
                handleUserLinkedTo(deviceId);
            } else {
                String userId = topicFields[1];
                logger.info("userId: " + userId);
                User user = userService.getUser(userId);
                logger.info("user: " + user);

                if (user != null) {
                    if (topicFields[2].equals("sample")) {
                        try {
                            Sample sample = (new ObjectMapper()).readValue(payload, Sample.class);
                            sampleService.classify(sample);
                            sampleService.save(sample);
                        } catch (IOException e) {
                            logger.error("Error unmarshalling sample: " + payload);
                        }
                    } else {
                        try {
                            List<Sample> samples = (new ObjectMapper()).readValue(payload, new TypeReference<List<Sample>>() {});
                            userService.configure(user, samples);
                        } catch (IOException e) {
                            logger.error("Error unmarshalling samples: " + payload);
                        }
                    }
                }

            }

        }
    }

    @Override
    public void publish(String topic, String payload, int qos, boolean retained) {
        messageGateway.publish(topic, payload, qos, retained);
    }

    public void handleUserLinkedTo(String deviceId) {
        String responseTopic = "user/linkedTo/" + deviceId + "/response";
        User user = null;
        try {
            user = userService.getUserLinkedTo(deviceId);
            logger.info("User: " + user);

            if (user == null) {
                logger.info("El dispositivo {} no tiene usuario vinculado.", deviceId);
                publish(responseTopic, "El dispositivo no tiene usuario vinculado.", MSG_QOS, false);
            } else {
                String json = objectMapper.writeValueAsString(user);
                publish(responseTopic, json, MSG_QOS, true);
            }

        } catch (NoSuchDeviceException e) {
            logger.info(e.getLocalizedMessage());
            publish(responseTopic, "El dispositivo no ha sido dado de alta.", MSG_QOS, false);
        } catch (JsonProcessingException e) {
            logger.error("Error marshalling user: " + user);
            publish(responseTopic, "El dispositivo no tiene usuario vinculado.", MSG_QOS, false);
        }
    }

}
