package com.acme.ch06.ex04;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class NetworkWriterTask implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkWriterTask.class);

    private Message message = null;

    public NetworkWriterTask(Message message) {
        this.message = message;
    }

    public void execute() {
        //LOGGER.info("Start");
        receiveMessageAndRespondToNetwork();
        //LOGGER.info("End");
    }

    private void receiveMessageAndRespondToNetwork() {
        String messageBody = new String(message.getBody());
        String correlationId = new String(message.getMessageProperties().getCorrelationId());
        LOGGER.debug("Message From Queue: " + messageBody + "; correlationId : " + correlationId);
        Socket clientSocket = NetworkServer.getItem(Long.parseLong(correlationId));
        NetworkServer.removeItem(Long.parseLong(correlationId));
        OutputStream outputStream = null;
        DataOutputStream dataOutputStream = null;
        try {
            outputStream = clientSocket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(messageBody);
            dataOutputStream.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                dataOutputStream.close();
                clientSocket.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

}