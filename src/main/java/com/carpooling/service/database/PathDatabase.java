package com.carpooling.service.database;

import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.v1.Driver;
import org.springframework.beans.factory.annotation.Autowired;

public class PathDatabase {

    @Autowired
    private MongoDatabase mongoDatabase;

    @Autowired
    private Driver neo4jDriver;

    private static final Logger LOG = LogManager.getLogger(PathDatabase.class);
}
