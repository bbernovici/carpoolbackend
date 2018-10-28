package com.carpooling.service.controller;

import com.carpooling.service.database.PathDatabase;
import com.carpooling.service.model.Application;
import com.carpooling.service.model.Path;
import com.carpooling.service.model.Pickup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PathController {

    @Autowired
    private PathDatabase db;

    @RequestMapping(value = "/path/create",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> employeeApplyToCompany(@RequestHeader(name="EMPLOYEE-ID") String driverId,
                                                    @RequestHeader(name="START-HOUR") Integer hour,
                                                    @RequestHeader(name="START-MINUTE") Integer minute,
                                                    @RequestBody List<Pickup> pickups) {

        db.addDriverPath(pickups, driverId, hour, minute);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/paths",
            method = RequestMethod.GET)
    public ResponseEntity<?>  getPathsByPickupId(@RequestHeader(value="PICKUP-ID") String pickupId) {

        ArrayList<Path> paths = db.getDriversForPickUp(pickupId);

        return new ResponseEntity<>(paths, HttpStatus.OK);
    }
}
