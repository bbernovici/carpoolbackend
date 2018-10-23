package com.carpooling.service.database;

import com.carpooling.service.model.Application;
import com.carpooling.service.model.Company;
import com.carpooling.service.model.Employee;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

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
                .append("homeLongitude", homeLongitude)
                .append("status", "applied");

        if (type.equals("driver")) {
            application.append("vehicleSeats", vehicleSeats);

        }
        applicationCollection.insertOne(application);

        MongoCollection<Document> employeeCollection = mongoDatabase.getCollection("employees");
        employeeCollection.updateOne(eq("_id", new ObjectId(employeeId)),
                combine(set("status", "applied")));

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

        MongoCollection<Document> employeeCollection = mongoDatabase.getCollection("employees");
        employeeCollection.updateOne(eq("_id", new ObjectId(doc.getString("employeeId"))),
                combine(set("status", "approved")));
    }

    public ArrayList<Application> getApplicationsFromCompanyId(String companyId) {
        MongoCollection<Document> applicationCollection = mongoDatabase.getCollection("applications");

        final FindIterable<Document> applications = applicationCollection.find(eq("companyId", companyId));

        final ArrayList<Application> appList = new ArrayList<>();
        applications.forEach(new Block<Document>() {
            @Override
            public void apply(final Document appDoc) {
                    Application app = new Application();
                    app.setId(appDoc.getObjectId("_id").toString());
                    app.setEmployeeId(appDoc.getString("employeeId"));
                    app.setCompanyId(appDoc.getString("companyId"));
                    app.setHomeLatitude(appDoc.getDouble("homeLatitude"));
                    app.setHomeLongitude(appDoc.getDouble("homeLongitude"));
                    app.setType(appDoc.getString("type"));
                    app.setVehicleSeats(appDoc.getInteger("vehicleSeats"));
                    appList.add(app);
            }
        });

        return appList;
    }
}
