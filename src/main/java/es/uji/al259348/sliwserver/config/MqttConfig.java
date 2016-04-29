package es.uji.al259348.sliwserver.config;

import es.uji.al259348.sliwserver.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.event.MqttConnectionFailedEvent;
import org.springframework.integration.mqtt.event.MqttIntegrationEvent;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Configuration
@IntegrationComponentScan
public class MqttConfig {

    @Autowired
    MessageService messageService;

    @Value("${mqtt.uri}")
    String uri;
    @Value("${mqtt.username}")
    String username;
    @Value("${mqtt.password}")
    String password;
    @Value("${mqtt.clientId}")
    String clientId;

    @Bean
    public DefaultMqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory clientFactory = new DefaultMqttPahoClientFactory();
        clientFactory.setServerURIs(uri);
        clientFactory.setUserName(username);
        clientFactory.setPassword(password);
        return clientFactory;
    }

    @Bean
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId+"In", mqttClientFactory());

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInboundChannel());
        adapter.addTopic("devices/register/request");
        adapter.addTopic("user/linkedTo/+/request");
        adapter.addTopic("user/+/configurar");
        adapter.addTopic("user/+/sample");
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInboundChannel")
    public MessageHandler handler() {
        return message -> {
            String topic = (String) message.getHeaders().get(MqttHeaders.TOPIC);
            String payload = (String) message.getPayload();
            int qos = (int) message.getHeaders().get(MqttHeaders.QOS);
            boolean retained = (boolean) message.getHeaders().get(MqttHeaders.RETAINED);

            messageService.handleMessage(topic, payload, qos, retained);
        };
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId+"Out", mqttClientFactory());
        messageHandler.setAsync(true);
        return messageHandler;
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MqttGateway {

        void publish(@Header(MqttHeaders.TOPIC) String topic, @Payload String payload, @Header(MqttHeaders.QOS) int qos, @Header(MqttHeaders.RETAINED) boolean retained);

    }

    @Component
    public static class MqttEventListener implements ApplicationListener<MqttIntegrationEvent> {

        @Override
        public void onApplicationEvent(MqttIntegrationEvent event) {
            if (event instanceof MqttConnectionFailedEvent)
                System.err.println("MQTT Error: " + event.getCause().getMessage());
        }

    }

}
