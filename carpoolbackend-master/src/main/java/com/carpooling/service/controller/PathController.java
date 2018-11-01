package com.carpooling.service.controller;

import com.carpooling.service.database.PathDatabase;
import com.carpooling.service.model.Path;
import com.carpooling.service.model.Pickup;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PathController {
    @Autowired
    private PathDatabase db;
    private ConnectionFactory factory;

    public PathController() {
        factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5672);
    }

    @RequestMapping(value = "/path/create",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPath(@RequestHeader(name = "EMPLOYEE-ID") String driverId,
                                        @RequestHeader(name = "START-HOUR") Integer hour,
                                        @RequestHeader(name = "START-MINUTE") Integer minute,
                                        @RequestBody List<Pickup> pickups) {

        db.addDriverPath(pickups, driverId, hour, minute);
        try {
            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();

            String queueName = "ssl-akka";
            channel.queueDeclare(queueName, true, false, false, null);
            channel.exchangeDeclare(queueName, "direct", true);
            channel.queueBind(queueName, queueName, queueName);

            String[] domains = {"post-new-path"};

            for (String domain : domains) {
                byte[] messageBodyBytes = domain.getBytes(StandardCharsets.UTF_8);
                channel.basicPublish(queueName, queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, messageBodyBytes);
            }
            channel.close();
            conn.close();
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/paths",
            method = RequestMethod.GET)
    public ResponseEntity<?> getPathsByPickupId(@RequestHeader(value = "PICKUP-ID") String pickupId) {

        ArrayList<Path> paths = db.getDriversForPickUp(pickupId);

        return new ResponseEntity<>(paths, HttpStatus.OK);
    }

    @RequestMapping(value = "/path/join",
            method = RequestMethod.POST)
    public ResponseEntity<?> joinPath(@RequestHeader(name = "PATH-ID") String pathId,
                                      @RequestHeader(name = "RIDER-ID") String riderId) {

        db.joinPath(riderId, pathId);
        try {
            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();

            String queueName = "ssl-akka";
            channel.queueDeclare(queueName, true, false, false, null);
            channel.exchangeDeclare(queueName, "direct", true);
            channel.queueBind(queueName, queueName, queueName);

            String[] domains = {"post-join-path"};

            for (String domain : domains) {
                byte[] messageBodyBytes = domain.getBytes(StandardCharsets.UTF_8);
                channel.basicPublish(queueName, queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, messageBodyBytes);
            }
            channel.close();
            conn.close();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
