package com.carpooling.service.controller;

import com.carpooling.service.database.ApplicationDatabase;
import com.carpooling.service.model.Application;
import com.carpooling.service.model.Employee;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

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

        sendToRabbit("applicationscreate", app.getEmployeeId());
        if (success) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    private void sendToRabbit(String queueName, String id){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = null;
        try {
            connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicPublish("", queueName, null, id.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        //This is the consumer

//        Consumer consumer = new DefaultConsumer(channel) {
//            @Override
//            public void handleDelivery(String consumerTag, Envelope envelope,
//                                       AMQP.BasicProperties properties, byte[] body)
//                    throws IOException {
//                String message = new String(body, "UTF-8");
//                System.out.println(" [x] Received '" + message + "'");
//            }
//        };
//        channel.basicConsume(QUEUE_NAME, true, consumer);


    }
    @RequestMapping(value = "/application/approve",
            method = RequestMethod.POST)
    public ResponseEntity<?> approveEmployeeApplication(@RequestHeader(value="APP-ID") String appId) {

        db.approveEmployeeApplication(appId);

        sendToRabbit("applicationsapprove", appId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/applications",
                    method = RequestMethod.GET)
    public ResponseEntity<?>  getApplicationsByCompanyId(@RequestHeader(value="COMPANY-ID") String companyId) {

        ArrayList<Application> appList = db.getApplicationsFromCompanyId(companyId);

        sendToRabbit("applications", companyId);


        return new ResponseEntity<>(appList, HttpStatus.OK);
    }

    @RequestMapping(value = "/company/employees",
            method = RequestMethod.GET)
    public ResponseEntity<?>  getEmployeesByCompanyId(@RequestHeader(value="COMPANY-ID") String companyId) {

        ArrayList<Employee> employees = db.getApprovedEmployeesFromCompanyId(companyId);

        sendToRabbit("companyemployees", companyId);

        return new ResponseEntity<>(employees, HttpStatus.OK);
    }
}
