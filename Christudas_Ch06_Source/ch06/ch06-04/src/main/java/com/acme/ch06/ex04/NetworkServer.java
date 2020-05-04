package com.acme.ch06.ex04;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NetworkServer implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkServer.class);

    private static volatile Hashtable<Long, Socket> clientSockets = new Hashtable<Long, Socket>();
    private volatile long count = 1000;
    private static AmqpTemplate amqpTemplate = null;
    private static ThreadPoolExecutor executorPool = null;

    private void init() {
        //LOGGER.info("Start");
        RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executorPool = new ThreadPoolExecutor(3, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), threadFactory, rejectionHandler);
        MonitorThread monitor = new MonitorThread(executorPool, 1 * 30);
        Thread monitorThread = new Thread(monitor);
        monitorThread.start();
        //LOGGER.info("End");
    }

    public static void main(String[] args) {
        //LOGGER.info("Start");
        ApplicationContext context = new ClassPathXmlApplicationContext("network-server-context.xml");//loading beans
        amqpTemplate = (AmqpTemplate) context.getBean("amqpTemplate");// getting a reference to the sender bean
        NetworkServer server = new NetworkServer();
        server.listen();
        //LOGGER.info("End");
    }

    private synchronized long getNextCorrelationId() {
        return ++count;
    }

    public static void addItem(long key, Socket socket) {
        clientSockets.put(key, socket);
    }

    public static Socket getItem(long key) {
        return clientSockets.get(key);
    }

    public static void removeItem(long key) {
        clientSockets.remove(key);
    }

    private void listen() {
        LOGGER.info("Start");
        init();
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        NetworkReaderTask networkReaderTask = null;
        long correlationIdNew = 0L;
        try {
            serverSocket = new ServerSocket(1100);
            LOGGER.debug("Listening for new connections...");
            while (true) {
                clientSocket = serverSocket.accept();
                LOGGER.debug("Received a new connection.");
                correlationIdNew = getNextCorrelationId();
                addItem(correlationIdNew, clientSocket);
                networkReaderTask = new NetworkReaderTask(amqpTemplate, correlationIdNew);
                executorPool.execute(new Worker(networkReaderTask));
            }
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    public void onMessage(Message message) {
        //LOGGER.info("Start");
        NetworkWriterTask networkWriterTask = new NetworkWriterTask(message);
        if (null == executorPool) {
            LOGGER.error("executorPool is null");
        }
        if (null == message) {
            LOGGER.error("message is null");
        }
        executorPool.execute(new Worker(networkWriterTask));
        //LOGGER.info("End");
    }

}