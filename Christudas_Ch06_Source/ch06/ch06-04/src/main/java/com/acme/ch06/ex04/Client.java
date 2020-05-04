package com.acme.ch06.ex04;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private static ThreadPoolExecutor executorPool = null;

    public static void main(String[] args) {
        LOGGER.info("Start");
        Client client = new Client();
        client.connect();
        LOGGER.info("End");
    }

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

    private void connect() {
        init();
        ClientSideTask[] clientSideTasks = new ClientSideTask[5];
        long seed = 0L;
        while (true) {
            clientSideTasks[0] = new ClientSideTask(++seed);
            clientSideTasks[1] = new ClientSideTask(++seed);
            clientSideTasks[2] = new ClientSideTask(++seed);
            clientSideTasks[3] = new ClientSideTask(++seed);
            clientSideTasks[4] = new ClientSideTask(++seed);
            Arrays.stream(clientSideTasks).forEach(item -> executorPool.execute(new Worker(item)));
            try {
                Thread.sleep(30 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}