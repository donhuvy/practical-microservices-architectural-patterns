package com.acme.ch06.ex04;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientSideTask implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientSideTask.class);
    private static final String MSG_SEED = "Msg # : ";
    private long id;

    public ClientSideTask(long id) {
        this.id = id;
    }

    public void execute() {
        //LOGGER.info("Start");
        Socket clientSocket = null;
        OutputStream outputStream = null;
        DataOutputStream dataOutputStream = null;
        InputStream inputStream = null;
        DataInputStream dataInputStream = null;
        try {
            //LOGGER.debug("Client.connect. Connecting");
            clientSocket = new Socket("localhost", 1100);
            //LOGGER.debug("Client.connect. Connected");
            outputStream = clientSocket.getOutputStream();
            inputStream = clientSocket.getInputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            dataInputStream = new DataInputStream(inputStream);
            dataOutputStream.writeUTF(MSG_SEED + id);
            dataOutputStream.flush();
            String fromServer = dataInputStream.readUTF();
            LOGGER.debug("Message Send to Server: " + MSG_SEED + id + "; Message Received back from Server: " + fromServer);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        //LOGGER.info("End");
    }

}