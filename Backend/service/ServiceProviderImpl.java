package com.EduExplore.System.service;


import com.EduExplore.System.controller.AuthController;
import com.EduExplore.System.model.ServiceProvider;
import com.EduExplore.System.repository.ServiceProviderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ServiceProviderImpl implements ServiceProviderService{

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ServiceProvider saveServiceProvider(ServiceProvider serviceProvider) {
        return serviceProviderRepository.save(serviceProvider);
    }
    @Override
    public ServiceProvider loginServiceProvider(String username, String password) {
        ServiceProvider serviceProvider = serviceProviderRepository.findByUsername(username);
        logger.info("Searched for: {}"+ username+ password);

        if(serviceProvider != null && serviceProvider.getPassword().equals(password)){

            return serviceProvider;
        }
        return null;
    }

    @Override
    public ServiceProvider getServiceProviderDetailsById(int id) {
        return serviceProviderRepository.findById(id);
    }

    @Override
    public ServiceProvider updateServiceProvider(String username, ServiceProvider updatedServiceProvider) {
        ServiceProvider existingServiceProvider = serviceProviderRepository.findByUsername(username);
        if (existingServiceProvider != null) {
            existingServiceProvider.setName(updatedServiceProvider.getName());
            existingServiceProvider.setEmail(updatedServiceProvider.getEmail());
            existingServiceProvider.setServiceType(updatedServiceProvider.getServiceType());
            existingServiceProvider.setServiceProviderDescription(updatedServiceProvider.getServiceProviderDescription());
            return serviceProviderRepository.save(existingServiceProvider);
        }
        return null;
    }

    public boolean ifUsernameExists(String username) {
        return serviceProviderRepository.existsByUsername(username);
    }

    public boolean ifEmailExists(String email) {
        return serviceProviderRepository.existsByEmail(email);
    }

    @Override
    public String sendOtp(String email) {

        //  ServiceProvider serviceProvider1 = serviceProviderRepository.findByUsername(username);
        ServiceProvider serviceProvider = serviceProviderRepository.findByEmail(email);

        if ( serviceProvider !=null ){
            String otp = generateOtp(10);
            serviceProvider.setOtp(otp);
            serviceProviderRepository.save(serviceProvider);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("OTP on Forgot Password");
            message.setText(otp);

            javaMailSender.send(message);

            return otp;
        }
        else {
            throw new RuntimeException("Invalid email address.");
        }

    }

    @Override
    public String VerifyOtp(String otp, String email) {

        ServiceProvider serviceProvider = serviceProviderRepository.findByEmail(email);
        if (serviceProvider!=null && serviceProvider.getOtp().equals(otp)){
            String message = "OTP is correct";
            return message;

        }
        else {
            throw new RuntimeException("Invalid email address.");
        }
    }

    @Override
    public String addNewPassword(String password,String confirmPassword, String email) {

        String message;

        if (!(password.equals(confirmPassword))){
            message = "Passwords don't match";

        }
        else{
            ServiceProvider serviceProvider = serviceProviderRepository.findByEmail(email);
            String encodedPassword = passwordEncoder.encode(password);

            serviceProvider.setPassword(encodedPassword);
            serviceProviderRepository.save(serviceProvider);
            logger.info("Searched for pw in implementation: {}", password);

            message = "Password changed successfully!";

        }

        return message;
    }

    private String generateOtp(int length) {
        // Create a Random instance
        Random rand = new Random();

        // Create a StringBuilder to store the OTP
        StringBuilder otp = new StringBuilder(length);

        // Loop through the desired length
        for (int i = 0; i < length; i++) {
            // Generate a random digit between 0 and 9
            int digit = rand.nextInt(10);

            // Append the digit to the OTP
            otp.append(digit);
        }

        // Return the generated OTP as a string
        return otp.toString();
    }

    @Override
    public ServiceProvider saveServiceProviderMain(ServiceProvider serviceProvider) {
        return serviceProviderRepository.save(serviceProvider);
    }

    @Override
    public ServiceProvider loginServiceProviderLogin(String username, String password) {
        ServiceProvider serviceProvider = serviceProviderRepository.findByUsername(username);
        if (serviceProvider != null && passwordEncoder.matches(password,serviceProvider.getPassword())) {
            return serviceProvider;
        }
        return null;
    }

    @Override
    public String LoginSendOtp(String email, String username) {
        ServiceProvider serviceProvider = serviceProviderRepository.findByEmail(email);
        ServiceProvider serviceProviderByUsername = serviceProviderRepository.findByUsername(username);

        if (serviceProvider != null && serviceProviderByUsername.getEmail().equals(email)) {
            String otp = generateOtp(6);
            serviceProviderByUsername.setOtp(otp);
            serviceProviderRepository.save(serviceProviderByUsername);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Verification OTP");
            message.setText("Your OTP for login is: " + otp);

            javaMailSender.send(message);

            return "OTP sent successfully!";
        } else {
            throw new RuntimeException("Invalid email address.");
        }
    }

    @Override
    public ServiceProvider LoginVerifyOtp(String otp, String username) {
        ServiceProvider serviceProvider = serviceProviderRepository.findByUsername(username);

        if (serviceProvider != null && serviceProvider.getOtp().equals(otp)) {
//            return "OTP verified successfully!";
            return serviceProvider;
        } else {
            throw new RuntimeException("Invalid OTP.");
        }
    }


};