package com.carpooling.service.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mongodb.client.model.Filters.eq;


public class UserDatabase {

    @Autowired
    private MongoDatabase mongoDatabase;

    private static final Logger LOG = LogManager.getLogger(UserDatabase.class);


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
                MongoCollection<Document> employeeCollection = mongoDatabase.getCollection("employees");
                Document employee = new Document("firstName", firstName)
                        .append("lastName", lastName)
                        .append("mail", mail)
                        .append("password", password);
                employeeCollection.insertOne(employee);
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
                MongoCollection<Document> companyCollection = mongoDatabase.getCollection("companies");
                Document company = new Document("name", companyName)
                        .append("mail", mail)
                        .append("password", password);
                companyCollection.insertOne(company);
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

}
