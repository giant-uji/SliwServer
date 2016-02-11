package es.uji.al259348.sliwserver.services;

public interface MessageService {

    void handleMessage(String topic, String payload, int qos, boolean retained);
    void publish(String topic, String payload, int qos, boolean retained);

}
