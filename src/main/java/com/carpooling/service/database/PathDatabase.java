package com.carpooling.service.database;

import com.carpooling.service.Security;
import com.carpooling.service.model.Employee;
import com.carpooling.service.model.Path;
import com.carpooling.service.model.Pickup;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
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

    @Autowired
    private UserDatabase userDatabase;

    private static final Logger LOG = LogManager.getLogger(PathDatabase.class);

    public void addDriverPath(final List<Pickup> pickups, final String driverId, Integer hour, Integer minute) {
        Long startingPickupId = 0L;
        for (int i = 0; i < pickups.size(); i++) {
            final int u = i;
            try ( Session session = neo4jDriver.session())
            {
                Record record = session.writeTransaction(new TransactionWork<Record>()
                {
                    @Override
                    public Record execute(Transaction tx )
                    {
                        StatementResult result = tx.run( "CREATE (p:Pickup {id: $pickupId, driverId: $driverId}) RETURN id(p)",
                                parameters( "pickupId", pickups.get(u).getId(), "driverId", driverId) );
                        return result.single();
                    }
                } );
                startingPickupId = record.get("id(p)").asLong();
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

    public ArrayList<Path> getDriversForPickUp(final String pickupId) {
        try ( Session session = neo4jDriver.session())
        {
            List<Record> records = session.writeTransaction( new TransactionWork<List<Record>>()
            {
                @Override
                public List<Record> execute(Transaction tx )
                {
                    StatementResult result = tx.run( "MATCH (p:Pickup) " +
                                    "WHERE p.id = $pickupId " +
                                    "RETURN p.driverId",
                            parameters( "pickupId", pickupId));
                    return result.list();
                }
            } );
            for(Record r : records) {
                MongoCollection<Document> pathsCollection = mongoDatabase.getCollection("paths");
                final FindIterable<Document> paths = pathsCollection.find(Filters.eq("driverId", r.get("p.driverId").asString()));
                final ArrayList<Path> pathList = new ArrayList<>();
                paths.forEach(new Block<Document>() {
                    @Override
                    public void apply(final Document pathDoc) {
                        Path path = new Path();
                        path.setId(pathDoc.getObjectId("_id").toString());
                        path.setDriverId(pathDoc.getString("driverId"));
                        Employee e = userDatabase.getEmployeeFromId(path.getDriverId());
                        path.setDriverFirstName(e.getFirstName());
                        path.setDriverLastName(e.getLastName());
                        path.setStartingPickup(pathDoc.getLong("startingPickup"));
                        path.setMembers((String[]) pathDoc.get("members"));
                        path.setHour(pathDoc.getInteger("hour"));
                        path.setMinute(pathDoc.getInteger("minute"));
                        pathList.add(path);
                    }
                });
                return pathList;
            }
        }
        return new ArrayList<>();
    }
}
