package com.carpooling.service.controller;

import com.carpooling.service.database.ApplicationDatabase;
import com.carpooling.service.model.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApplicationController {

    @Autowired
    private ApplicationDatabase db;

    @RequestMapping(value = "/application/create",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> employeeApplyToCompany(@RequestBody Application app) {

        Boolean success = db.employeeApplyToCompany(app.getEmployeeId(),
                app.getCompanyId(),
                app.getType(),
                app.getVehicleSeats(),
                app.getHomeLatitude(),
                app.getHomeLongitude());

        if (success) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/application/approve",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> approveEmployeeApplication(@RequestHeader(value="APP-ID") String appId) {

        db.approveEmployeeApplication(appId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}