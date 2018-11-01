package com.carpooling.service.controller;

import com.carpooling.service.database.ApplicationDatabase;
import com.carpooling.service.model.Application;
import com.carpooling.service.model.Employee;
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

@RestController
public class ApplicationController {
    @Autowired
    private ApplicationDatabase db;
    private ConnectionFactory factory;

    public ApplicationController() {
        factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5672);
    }

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
            try {
                Connection conn = factory.newConnection();
                Channel channel = conn.createChannel();

                String queueName = "ssl-akka";
                channel.queueDeclare(queueName, true, false, false, null);
                channel.exchangeDeclare(queueName, "direct", true);
                channel.queueBind(queueName, queueName, queueName);

                String[] domains = {"post-new-application"};

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
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/application/approve",
            method = RequestMethod.POST)
    public ResponseEntity<?> approveEmployeeApplication(@RequestHeader(value = "APP-ID") String appId) {

        db.approveEmployeeApplication(appId);
        try {
            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();

            String queueName = "ssl-akka";
            channel.queueDeclare(queueName, true, false, false, null);
            channel.exchangeDeclare(queueName, "direct", true);
            channel.queueBind(queueName, queueName, queueName);

            String[] domains = {"post-approve-application"};

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

    @RequestMapping(value = "/applications",
            method = RequestMethod.GET)
    public ResponseEntity<?> getApplicationsByCompanyId(@RequestHeader(value = "COMPANY-ID") String companyId) {

        ArrayList<Application> appList = db.getApplicationsFromCompanyId(companyId);

        return new ResponseEntity<>(appList, HttpStatus.OK);
    }

    @RequestMapping(value = "/company/employees",
            method = RequestMethod.GET)
    public ResponseEntity<?> getEmployeesByCompanyId(@RequestHeader(value = "COMPANY-ID") String companyId) {

        ArrayList<Employee> employees = db.getApprovedEmployeesFromCompanyId(companyId);

        return new ResponseEntity<>(employees, HttpStatus.OK);
    }
}
