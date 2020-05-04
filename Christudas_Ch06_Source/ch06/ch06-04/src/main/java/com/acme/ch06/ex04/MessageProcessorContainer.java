package com.acme.ch06.ex04;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MessageProcessorContainer {

    public static void main(String[] args) {
        ApplicationContext c1 = new ClassPathXmlApplicationContext("message-processor-context.xml");
    }

    private static final void log(Object msg) {
        System.out.println(msg.toString());
    }

}
