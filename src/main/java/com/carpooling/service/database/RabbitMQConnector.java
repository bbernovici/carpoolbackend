package com.carpooling.service.database;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQConnector {

    private ConnectionFactory factory = new ConnectionFactory();
    private Connection conn;
    private Channel channel;

    public RabbitMQConnector() {
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        try {
            conn = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        try {
            channel = conn.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Channel getChannel() {
        return channel;
    }


}
