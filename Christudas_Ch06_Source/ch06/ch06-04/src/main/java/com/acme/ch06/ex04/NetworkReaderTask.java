package com.acme.ch06.ex04;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.Socket;

public class NetworkReaderTask implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkReaderTask.class);

    private AmqpTemplate amqpTemplate = null;
    private long correlationId = 0L;

    public NetworkReaderTask(AmqpTemplate amqpTemplate, long correlationId) {
        this.amqpTemplate = amqpTemplate;
        this.correlationId = correlationId;
    }

    public void execute() {
        //LOGGER.info("Start");
        receiveFromNetwrokAndSendMessage();
        //LOGGER.info("End");
    }

    private void receiveFromNetwrokAndSendMessage() {
        //LOGGER.info("Start");
        Socket clientSocket = NetworkServer.getItem(correlationId);
        InputStream inputStream = null;
        DataInputStream dataInputStream = null;
        try {
            inputStream = clientSocket.getInputStream();
            dataInputStream = new DataInputStream(inputStream);
            String fromClient = dataInputStream.readUTF();
            LOGGER.debug("Message From Client: " + fromClient);
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setCorrelationId(Long.toString(correlationId).getBytes());
            Message message = new Message(fromClient.getBytes(), messageProperties);
            amqpTemplate.send("my.routingkey.1", message);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                //dataInputStream.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        //LOGGER.info("End");
    }

}