package com.acme.ch06.ex04;

public class Worker implements Runnable {

    private Task task;

    public Worker(Task task) {
        this.task = task;
    }

    public void run() {
        log("Thread : " + Thread.currentThread().getId() + " - " + Thread.currentThread().getName() + " executing. Wrapper Object Ref: " + this);
        task.execute();
    }

    protected void finalize() {
        log("Wrapper Object Ref: " + this + " finalized!");
    }

    private static final void log(Object msg) {
        System.out.println(msg.toString());
    }

}