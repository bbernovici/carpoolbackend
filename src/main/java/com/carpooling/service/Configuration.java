package com.carpooling.service;

import com.carpooling.service.database.ApplicationDatabase;
import com.carpooling.service.database.MongoClientInstantiator;
import com.carpooling.service.database.Neo4jClientInstantiator;
import com.carpooling.service.database.UserDatabase;
import com.mongodb.client.MongoDatabase;
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
    public UserDatabase userDatabase() {
        return new UserDatabase();
    }

    @Bean
    public ApplicationDatabase applicationDatabase() {
        return new ApplicationDatabase();
    }

}
