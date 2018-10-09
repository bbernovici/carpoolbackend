package com.carpooling.service.database;

import com.carpooling.service.Security;
import com.carpooling.service.model.Company;
import com.carpooling.service.model.Employee;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
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


public class UserDatabase {

    @Autowired
    private MongoDatabase mongoDatabase;

    @Autowired
    private Driver neo4jDriver;

    private static final Logger LOG = LogManager.getLogger(UserDatabase.class);


    public String login(String mail, String password, String type) {

        if (type.equals("employee")) {
            if(isEmployeeMailAlreadyRegistered(mail)) {
                System.out.println("Test");
                Employee employee = getEmployeeFromMail(mail);
                Boolean isMatch = Security.doPasswordsMatch(password, employee.getPassword());
                if(isMatch) return employee.getToken();
            }
        } else if (type.equals("company")) {
            if(isCompanyMailAlreadyRegistered(mail)) {
                Company company = getCompanyFromMail(mail);
                Boolean isMatch = Security.doPasswordsMatch(password, company.getPassword());
                if(isMatch) return company.getToken();
            }
        }

        return null;
    }

    public void logout(String token, String type) {

        if(type.equals("employee")) {
            MongoCollection<Document> employeeCollection = mongoDatabase.getCollection("employees");
            employeeCollection.updateOne(eq("token", token),
                    combine(set("token", Security.generateToken())));
        } else if (type.equals("company")) {
            MongoCollection<Document> companyCollection = mongoDatabase.getCollection("companies");
            companyCollection.updateOne(eq("token", token),
                    combine(set("token", Security.generateToken())));
        }
    }

    /**
     * Database method to sign up the employee
     * Status [2, successfully created], [1, password too short], [0, mail already exists]
     *
     * @param firstName
     * @param lastName
     * @param mail
     * @param password
     * @return
     */
    public Integer registerEmployee(String firstName,
                                    String lastName,
                                    String mail,
                                    String password) {

        if (!isEmployeeMailAlreadyRegistered(mail)) {
            if (password.length() >= 8) {

                // MongoDB
                MongoCollection<Document> employeeCollection = mongoDatabase.getCollection("employees");
                Document employee = new Document("firstName", firstName)
                        .append("lastName", lastName)
                        .append("mail", mail)
                        .append("password", Security.encryptPassword(password))
                        .append("token", Security.generateToken());
                employeeCollection.insertOne(employee);

                ObjectId id = employee.getObjectId("_id");

                // Neo4j
                Session session = neo4jDriver.session();
                String query = "CREATE (a:Employee {id:{id}})";
                Map<String,Object> params = new HashMap<>();
                params.put( "id", id.toString() );
                session.run( query, params ).consume();

                return 2;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

    /**
     * Utility function that checks if a employee mail already exists
     *
     * @param mail
     * @return
     */
    public Boolean isEmployeeMailAlreadyRegistered(String mail) {
        MongoCollection<Document> employeeCollection = mongoDatabase.getCollection("employees");
        if (employeeCollection.find(eq("mail", mail)).first() == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Database method to sign up as a company
     * Status [2, successfully created], [1, password too short], [0, mail already exists]
     *
     * @param companyName
     * @param mail
     * @param password
     * @return
     */
    public Integer registerCompany(String companyName,
                                   String mail,
                                   String password) {

        if (!isCompanyMailAlreadyRegistered(mail)) {
            if (password.length() >= 8) {

                // MongoDB
                MongoCollection<Document> companyCollection = mongoDatabase.getCollection("companies");
                Document company = new Document("name", companyName)
                        .append("mail", mail)
                        .append("password", Security.encryptPassword(password))
                        .append("token", Security.generateToken());
                companyCollection.insertOne(company);

                ObjectId id = company.getObjectId("_id");

                // Neo4j
                Session session = neo4jDriver.session();
                String query = "CREATE (a:Company {id:{id}})";
                Map<String,Object> params = new HashMap<>();
                params.put( "id", id.toString() );
                session.run( query, params ).consume();
                session.close();

                return 2;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

    /**
     * Utility function that checks if a company mail already exists
     *
     * @param mail
     * @return
     */
    public Boolean isCompanyMailAlreadyRegistered(String mail) {
        MongoCollection<Document> companyCollection = mongoDatabase.getCollection("companies");
        if (companyCollection.find(eq("mail", mail)).first() == null) {
            return false;
        } else {
            return true;
        }
    }

    public Employee getEmployeeFromId(String id) {
        MongoCollection<Document> employeeCollection = mongoDatabase.getCollection("employees");
        List<Bson> queryFilters = new ArrayList<>();
        queryFilters.add(Filters.eq("_id", new ObjectId(id)));
        Bson searchFilter = Filters.and(queryFilters);

        List<Bson> returnFilters = new ArrayList<>();
        returnFilters.add(Filters.eq("firstName", 1));
        returnFilters.add(Filters.eq("lastName", 1));
        returnFilters.add(Filters.eq("mail", 1));
        returnFilters.add(Filters.eq("token", 1));
        returnFilters.add(Filters.eq("password", 1));


        Bson returnFilter = Filters.and(returnFilters);

        Document doc = employeeCollection.find(searchFilter).projection(returnFilter).first();

        Employee employee = new Employee(doc.getObjectId("_id").toString(),
                doc.getString("firstName"),
                doc.getString("lastName"),
                doc.getString("mail"),
                doc.getString("token"),
                doc.getString("password"));

        return employee;
    }

    public Employee getEmployeeFromMail(String mail) {
        MongoCollection<Document> employeeCollection = mongoDatabase.getCollection("employees");
        List<Bson> queryFilters = new ArrayList<>();
        queryFilters.add(Filters.eq("mail", mail));
        Bson searchFilter = Filters.and(queryFilters);

        List<Bson> returnFilters = new ArrayList<>();
        returnFilters.add(Filters.eq("firstName", 1));
        returnFilters.add(Filters.eq("lastName", 1));
        returnFilters.add(Filters.eq("mail", 1));
        returnFilters.add(Filters.eq("token", 1));
        returnFilters.add(Filters.eq("password", 1));


        Bson returnFilter = Filters.and(returnFilters);

        Document doc = employeeCollection.find(searchFilter).projection(returnFilter).first();

        Employee employee = new Employee(doc.getObjectId("_id").toString(),
                doc.getString("firstName"),
                doc.getString("lastName"),
                doc.getString("mail"),
                doc.getString("token"),
                doc.getString("password"));

        return employee;
    }

    public Employee getEmployeeFromToken(String token) {
        MongoCollection<Document> employeeCollection = mongoDatabase.getCollection("employees");
        List<Bson> queryFilters = new ArrayList<>();
        queryFilters.add(Filters.eq("token", token));
        Bson searchFilter = Filters.and(queryFilters);

        List<Bson> returnFilters = new ArrayList<>();
        returnFilters.add(Filters.eq("firstName", 1));
        returnFilters.add(Filters.eq("lastName", 1));
        returnFilters.add(Filters.eq("mail", 1));
        returnFilters.add(Filters.eq("token", 1));
        returnFilters.add(Filters.eq("password", 1));


        Bson returnFilter = Filters.and(returnFilters);

        Document doc = employeeCollection.find(searchFilter).projection(returnFilter).first();

        Employee employee = new Employee(doc.getObjectId("_id").toString(),
                doc.getString("firstName"),
                doc.getString("lastName"),
                doc.getString("mail"),
                doc.getString("token"),
                doc.getString("password"));

        return employee;
    }

    public Company getCompanyFromId(String id) {
        MongoCollection<Document> companyCollection = mongoDatabase.getCollection("companies");
        List<Bson> queryFilters = new ArrayList<>();
        queryFilters.add(Filters.eq("_id", new ObjectId(id)));
        Bson searchFilter = Filters.and(queryFilters);

        List<Bson> returnFilters = new ArrayList<>();
        returnFilters.add(Filters.eq("name", 1));
        returnFilters.add(Filters.eq("mail", 1));
        returnFilters.add(Filters.eq("token", 1));
        returnFilters.add(Filters.eq("password", 1));


        Bson returnFilter = Filters.and(returnFilters);

        Document doc = companyCollection.find(searchFilter).projection(returnFilter).first();

        Company company = new Company(doc.getObjectId("_id").toString(),
                doc.getString("name"),
                doc.getString("mail"),
                doc.getString("password"),
                doc.getString("token"),
                doc.getString("locationLatitude"),
                doc.getString("locationLongitude"));

        return company;
    }

    public Company getCompanyFromMail(String mail) {
        MongoCollection<Document> companyCollection = mongoDatabase.getCollection("companies");
        List<Bson> queryFilters = new ArrayList<>();
        queryFilters.add(Filters.eq("mail", mail));
        Bson searchFilter = Filters.and(queryFilters);

        List<Bson> returnFilters = new ArrayList<>();
        returnFilters.add(Filters.eq("name", 1));
        returnFilters.add(Filters.eq("mail", 1));
        returnFilters.add(Filters.eq("token", 1));
        returnFilters.add(Filters.eq("password", 1));


        Bson returnFilter = Filters.and(returnFilters);

        Document doc = companyCollection.find(searchFilter).projection(returnFilter).first();

        Company company = new Company(doc.getObjectId("_id").toString(),
                doc.getString("name"),
                doc.getString("mail"),
                doc.getString("password"),
                doc.getString("token"),
                doc.getString("locationLatitude"),
                doc.getString("locationLongitude"));

        return company;
    }

    public Company getCompanyFromToken(String token) {
        MongoCollection<Document> companyCollection = mongoDatabase.getCollection("companies");
        List<Bson> queryFilters = new ArrayList<>();
        queryFilters.add(Filters.eq("token", token));
        Bson searchFilter = Filters.and(queryFilters);

        List<Bson> returnFilters = new ArrayList<>();
        returnFilters.add(Filters.eq("name", 1));
        returnFilters.add(Filters.eq("mail", 1));
        returnFilters.add(Filters.eq("token", 1));
        returnFilters.add(Filters.eq("password", 1));


        Bson returnFilter = Filters.and(returnFilters);

        Document doc = companyCollection.find(searchFilter).projection(returnFilter).first();

        Company company = new Company(doc.getObjectId("_id").toString(),
                doc.getString("name"),
                doc.getString("mail"),
                doc.getString("password"),
                doc.getString("token"),
                doc.getString("locationLatitude"),
                doc.getString("locationLongitude"));

        return company;
    }


}
