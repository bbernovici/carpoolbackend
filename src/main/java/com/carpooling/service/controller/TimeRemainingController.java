package com.carpooling.service.controller;

import com.carpooling.service.model.TimeRemaining;
import com.carpooling.service.model.HelloMessage;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;

@RestController
public class TimeRemainingController {
    // map from username to arrival time
    private HashMap<String, DateTime> times = new HashMap<>();
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public TimeRemaining greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        if (!times.containsKey(message.getName())) {
            times.put(message.getName(), DateTime.now().plusMinutes(60));
        }
        DateTime arrivalTime = times.get(message.getName());
        return new TimeRemaining(arrivalTime);
    }

}
