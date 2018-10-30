package com.carpooling.service.controller;

import com.carpooling.service.model.Car;
import com.carpooling.service.model.TimeRemaining;
import org.joda.time.DateTime;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;

@RestController
public class TimeRemainingController {

    // map from username to arrival time
    private HashMap<String, DateTime> times = new HashMap<>();

    @MessageMapping("/car")
    @SendTo("/topic/arrival")
    public TimeRemaining greeting(Car carOwner) throws Exception {
        if (!times.containsKey(carOwner.getCarOwner())) {
            times.put(carOwner.getCarOwner(), DateTime.now().plusMinutes(60));
        }
        DateTime arrivalTime = times.get(carOwner.getCarOwner());
        return new TimeRemaining(arrivalTime);
    }

}
