package com.carpooling.service.controller;

import com.carpooling.service.model.Location;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ThreadLocalRandom;

@Controller
public class LocationController {

    @MessageMapping("/location")
    @SendTo("/sockets/location")
    public Location getLocation(){

        Location locationMessage = new Location();

        try{
            Thread.sleep(1000);
        }catch (InterruptedException iE){
            iE.printStackTrace();

            locationMessage.setLatitude(ThreadLocalRandom.current().nextDouble(100, 200));
            locationMessage.setLongitude(ThreadLocalRandom.current().nextDouble(100, 200));
        }

        return locationMessage;
    }
}
