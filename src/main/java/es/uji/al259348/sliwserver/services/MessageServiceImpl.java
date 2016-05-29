package es.uji.al259348.sliwserver.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.uji.al259348.sliwserver.config.MqttConfig;
import es.uji.al259348.sliwserver.exceptions.NoSuchDeviceException;
import es.uji.al259348.sliwserver.model.Device;
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
    private DeviceService deviceService;

    @Autowired
    private UserService userService;

    @Autowired
    private SampleService sampleService;

    private static final int MSG_QOS = 2;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handleMessage(String topic, String payload, int qos, boolean retained) {
        logger.info("Message received from topic (" + topic + "):");
        logger.info(payload);

        String[] topicFields = topic.split("/");
        String scope = topicFields[0];

        if (scope.equals("devices")) {

            String deviceId = topicFields[1];
            String action = topicFields[2];

            if (action.equals("register"))
                handleRegisterDevice(deviceId);
            else if (action.equals("user"))
                handleUserLinkedTo(deviceId);
        }

        if (scope.equals("users")) {

            String userId = topicFields[1];
            String action = topicFields[2];

            if (action.equals("configure"))
                handleConfigureUser(userId, payload);

        }

        if (scope.equals("samples")) {

            String sampleId = topicFields[1];
            String action = topicFields[2];

            if (action.equals("save"))
                handleSaveSample(sampleId, payload);

        }

    }

    @Override
    public void publish(String topic, String payload, int qos, boolean retained) {
        messageGateway.publish(topic, payload, qos, retained);
    }

    private void handleRegisterDevice(String deviceId) {
        Device device = new Device();
        device.setId(deviceId);
        deviceService.save(device);
        publish("devices/" + deviceId + "/register/response", "200 OK", MSG_QOS, false);
    }

    private void handleUserLinkedTo(String deviceId) {
        String responseTopic = "devices/" + deviceId + "/user/response";
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

    private void handleConfigureUser(String userId, String payload) {
        String responseTopic = "users/" + userId + "/configure/response";
        User user = userService.getUser(userId);

        if (user != null) {
            try {
                List<Sample> samples = (new ObjectMapper()).readValue(payload, new TypeReference<List<Sample>>() {});
                userService.configure(user, samples);
                publish(responseTopic, "200 OK", MSG_QOS, true);
            } catch (IOException e) {
                logger.error("Error unmarshalling samples: " + payload);
                publish(responseTopic, "Error configurando usuario.", MSG_QOS, false);
            }
        } else {
            logger.info("Intentando configurar usuario no existente.");
            publish(responseTopic, "Error configurando usuario.", MSG_QOS, false);
        }
    }

    private void handleSaveSample(String sampleId, String payload) {
        try {
            Sample sample = (new ObjectMapper()).readValue(payload, Sample.class);
            sampleService.classify(sample);
            sampleService.save(sample);
        } catch (IOException e) {
            logger.error("Error unmarshalling sample: " + payload);
        }
    }

}
