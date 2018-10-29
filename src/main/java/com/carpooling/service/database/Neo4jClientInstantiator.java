package com.carpooling.service.database;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

public class Neo4jClientInstantiator {

    private Driver driver;

    public Neo4jClientInstantiator() {
        driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("localhost", "carpooling"));
    }
    public Driver getDriver() {
        return driver;
    }

}
