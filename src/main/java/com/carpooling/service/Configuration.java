package com.carpooling.service;

import com.carpooling.service.database.*;
import com.mongodb.client.MongoDatabase;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.neo4j.driver.v1.Driver;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public MongoDatabase mongoDatabase() {
        return new MongoClientInstantiator().getDatabase();
    }

    @Bean
    public Driver neo4jSession() {
        return new Neo4jClientInstantiator().getDriver();
    }

    @Bean
    public Connection rabbitMqChannel() {
        return new RabbitMQConnector().getConnection();
    }

    @Bean
    public UserDatabase userDatabase() {
        return new UserDatabase();
    }

    @Bean
    public ApplicationDatabase applicationDatabase() {
        return new ApplicationDatabase();
    }

    @Bean
    public PathDatabase pathDatabase() { return new PathDatabase(); }
}
