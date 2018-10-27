package com.carpooling.service.controller;

import com.carpooling.service.database.UserDatabase;
import com.carpooling.service.model.Company;
import com.carpooling.service.model.Employee;
import com.carpooling.service.model.Pickup;
import com.carpooling.service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserDatabase db;

    @RequestMapping(value = "/login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestHeader(value = "USER-TYPE") String type, @RequestBody Employee employee) {
        User user = db.login(employee.getMail(), employee.getPassword(), type);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/logout",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logout(@RequestHeader(value = "USER-TYPE") String type, @RequestHeader(value = "TOKEN") String token) {
        db.logout(token, type);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/employee/signup",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> employeeSignUp(@RequestBody Employee employee) {
        HashMap statusMap = new HashMap<String, Integer>();
        Integer status = db.registerEmployee(employee.getFirstName(),
                employee.getLastName(),
                employee.getMail(),
                employee.getPassword());

        statusMap.put("status", status);
        if (status == 2) {
            return new ResponseEntity<>(statusMap, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(statusMap, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/company/signup",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<?> companySignUp(@RequestBody Company company) {
        HashMap statusMap = new HashMap<String, Integer>();
        Integer status = db.registerCompany(company.getName(),
                company.getMail(),
                company.getPassword());

        statusMap.put("status", status);
        if (status == 2) {
            return new ResponseEntity<>(statusMap, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(statusMap, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/company/{name}",
            method = RequestMethod.GET)

    public ResponseEntity<?> getCompanyFromName(@PathVariable(value = "name") String companyName) {
        Company company = db.getCompanyFromName(companyName);
        if (company != null) {
            company.setPassword("");
            return new ResponseEntity<>(company, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(company, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/employee/{id}",
            method = RequestMethod.GET)

    public ResponseEntity<?> getEmployeeFromId(@PathVariable(value = "id") String employeeId) {
        Employee employee = db.getEmployeeFromId(employeeId);
        if (employee != null) {
            employee.setPassword("");
            return new ResponseEntity<>(employee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(employee, HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/company/riders/location",
            method = RequestMethod.GET)

    public ResponseEntity<?> getCompanyRidersLocation(@RequestHeader(value = "COMPANY-ID") String companyId) {
        List<Pickup> startingLocations = db.getRidersStartingLocation(companyId);
        return new ResponseEntity<>(startingLocations, HttpStatus.OK);
    }

    @RequestMapping(value = "/company/drivers/location",
            method = RequestMethod.GET)

    public ResponseEntity<?> getCompanyDriversLocation(@RequestHeader(value = "COMPANY-ID") String companyId) {
        List<Pickup> startingLocations = db.getDriversStartingLocation(companyId);
        return new ResponseEntity<>(startingLocations, HttpStatus.OK);
    }

    @RequestMapping(value = "/company/pickups/add",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<?> companySignUp(@ModelAttribute List<Pickup> pickups) {
        db.addCompanyPickups(pickups);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
