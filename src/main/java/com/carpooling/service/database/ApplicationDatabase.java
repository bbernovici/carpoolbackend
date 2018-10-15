package com.carpooling.service.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class ApplicationDatabase {

    @Autowired
    private MongoDatabase mongoDatabase;

    @Autowired
    private Driver neo4jDriver;

    private static final Logger LOG = LogManager.getLogger(ApplicationDatabase.class);

    /**
     * Method for the employee to apply in a company as a driver or rider
     *
     * @param employeeId
     * @param companyId
     * @param vehicleSeats
     * @param homeLatitude
     * @param homeLongitude
     * @return
     */
    public Boolean employeeApplyToCompany(String employeeId,
                                          String companyId,
                                          String type,
                                          Integer vehicleSeats,
                                          Double homeLatitude,
                                          Double homeLongitude) {

        MongoCollection<Document> applicationCollection = mongoDatabase.getCollection("applications");
        Document application = new Document("employeeId", employeeId)
                .append("companyId", companyId)
                .append("type", type)
                .append("homeLatitude", homeLatitude)
                .append("homeLongitutde", homeLongitude);

        if (type.equals("driver")) {
            application.append("vehicleSeats", vehicleSeats);

        }
        applicationCollection.insertOne(application);
        return true;
    }


    public void approveEmployeeApplication(String appId) {
        MongoCollection<Document> applicationCollection = mongoDatabase.getCollection("applications");

        Document doc = applicationCollection.find(eq("_id", new ObjectId(appId))).first();

        if (doc.getString("type").equals("driver")) {
            Session session = neo4jDriver.session();
            String query = "MATCH (e:Employee {id:{e_id}}), " +
                    "(c:Company {id:{c_id}}) " +
                    "MERGE (e)-[:DRIVER_OF]->(c)";
            Map<String, Object> params = new HashMap<>();
            params.put("e_id", doc.getString("employeeId"));
            params.put("c_id", doc.getString("companyId"));
            session.run(query, params).consume();
            session.close();
        }
    }
}
