package com.carpooling.service.database;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.springframework.stereotype.Component;

public class MongoClientInstantiator {

    private MongoClient mongoClient;

    public MongoClientInstantiator() {
        mongoClient = new MongoClient(System.getProperty("mongodb.uri"));
    }

    public MongoClient getClientInstance() {
        return mongoClient;
    }

    public MongoDatabase getDatabase() {
        return mongoClient.getDatabase("carpooling");
    }
}
