package com.carpooling.service.controller;

import com.carpooling.service.database.PathDatabase;
import com.carpooling.service.model.Path;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class SimulatorController {

    @Autowired
    private PathDatabase db;

    @Autowired
    private Channel channel;

    @RequestMapping(value = "/simulatePath",
            method = RequestMethod.POST)
    public ResponseEntity<?> getPathsByPickupId(@RequestHeader(value="PATH-ID") String pathId) {

        Gson gson = new Gson();
        Path path = db.getPathFromId(pathId);
        try {
            channel.queueDeclare("paths.simulator",false,false,false,null);
            channel.basicPublish("","paths.simulator",null, gson.toJson(path).getBytes());
            System.out.println("Message sent through RabbitMQ: " + gson.toJson(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(path, HttpStatus.OK);
    }
}
