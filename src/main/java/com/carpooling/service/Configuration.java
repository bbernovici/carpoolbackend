package com.carpooling.service;

import com.carpooling.service.database.MongoClientInstantiator;
import com.carpooling.service.database.UserDatabase;
import com.mongodb.client.MongoDatabase;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public MongoDatabase mongoDatabase() {
        return new MongoClientInstantiator().getDatabase();
    }

    @Bean
    public UserDatabase userDatabase() {
        return new UserDatabase();
    }
}
