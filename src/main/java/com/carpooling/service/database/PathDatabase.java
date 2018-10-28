package com.carpooling.service.database;

import com.carpooling.service.Security;
import com.carpooling.service.model.Pickup;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.neo4j.driver.v1.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.v1.Values.parameters;

public class PathDatabase {

    @Autowired
    private MongoDatabase mongoDatabase;

    @Autowired
    private Driver neo4jDriver;

    private static final Logger LOG = LogManager.getLogger(PathDatabase.class);

    public void addDriverPath(final List<Pickup> pickups, final String driverId, Integer hour, Integer minute) {
        for (int i = 0; i < pickups.size(); i++) {
            final int u = i;
            Long startingPickupId = 0L;
            try ( Session session = neo4jDriver.session())
            {
                Record record = session.writeTransaction(new TransactionWork<Record>()
                {
                    @Override
                    public Record execute(Transaction tx )
                    {
                        Map<String, Object> params = new HashMap<>();
                        params.put("pickupId", pickups.get(u).getId());
                        params.put("driverId", driverId);
                        StatementResult result = tx.run( "CREATE (p:Pickup {id: $pickupId, driverId: $driverId})",
                                parameters( params) );
                        return result.single();
                    }
                } );
                startingPickupId = record.get("c.id").asLong();
                if (i > 0) {
                    Map<String, Object> params2 = new HashMap<>();
                    String pathNodeQuery = "MATCH (p1:Pickup {id: $currentPickUpId}), " +
                            "(p2: Pickup {id: $previousPickUpId}) " +
                            "MERGE (p2)-[r:NEXT_STOP]->(p1)";
                    params2.put("currentPickUpId", pickups.get(i).getId());
                    params2.put("previousPickUpId", pickups.get(i-1).getId());
                    session.run(pathNodeQuery, params2).consume();
                    session.close();
                }
            }
            if(startingPickupId != 0L) {
                MongoCollection<Document> pathsCollection = mongoDatabase.getCollection("paths");
                Document path = new Document("driverId", driverId)
                        .append("startingPickup", startingPickupId)
                        .append("members", new ArrayList<>())
                        .append("hour", hour)
                        .append("minute", minute);
                pathsCollection.insertOne(path);
            }
        }
    }
}
