package com.acme.ch06.ex04;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;

public class MessageProcessorListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessorListener.class);

    private AmqpTemplate amqpTemplate;

    public MessageProcessorListener(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void onMessage(Message message) {
        //LOGGER.info("Start");
        /* ASSUME THAT WE RECEIVED THE HEAVY JOB FOR EXECUTION HERE */
        String messageBody = new String(message.getBody());
        String correlationId = new String(message.getMessageProperties().getCorrelationId());
        LOGGER.debug("Listener received message: " + messageBody + "; correlationId : " + correlationId);
        /* ASSUME THAT THE HEAVY JOB EXECUTION TAKES PLACE HERE */
        /* ASSUME THAT WE HAVE EXECUTED THE HEAVY JOB AND NOW WE CAN SEND THE PROCESSED RESPONSE BACK */
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId(correlationId.getBytes());
        Message messageToSend = new Message(messageBody.getBytes(), messageProperties);
        amqpTemplate.send("my.routingkey.1", messageToSend);
        //LOGGER.info("End");
    }

}
