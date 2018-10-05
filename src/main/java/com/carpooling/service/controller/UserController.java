package com.carpooling.service.controller;

import com.carpooling.service.database.UserDatabase;
import com.carpooling.service.model.Company;
import com.carpooling.service.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class UserController {

    @Autowired
    private UserDatabase db;

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
        if (status == 1) {
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
        if (status == 1) {
            return new ResponseEntity<>(statusMap, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(statusMap, HttpStatus.CONFLICT);
        }
    }

}
