package com.carpooling.service.security;



import com.carpooling.service.model.Employee;
import com.carpooling.service.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Service;

@Service
public class FacebookConnectionSignup implements ConnectionSignUp {

    @Autowired
    private UserRepository userRepository;

    @Override
    public String execute(Connection<?> connection) {
        System.out.println("signup === ");
        final Employee user = new Employee();
        user.setFirstName(connection.fetchUserProfile().getFirstName());
        user.setLastName(connection.fetchUserProfile().getLastName());
        user.setPassword("supers3cr3t");
        userRepository.save(user);
        return user.getFirstName()+user.getLastName();
    }

}
