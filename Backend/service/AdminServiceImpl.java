package com.EduExplore.System.service;

import com.EduExplore.System.model.Admin;
import com.EduExplore.System.model.ServiceProvider;
import com.EduExplore.System.model.Traveller;
import com.EduExplore.System.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;



@Service
public class AdminServiceImpl implements AdminService{
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Admin createAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    @Override
    public Admin saveAdmin(Admin admin) {

        return adminRepository.save(admin);
    }

    @Override
    public Admin loginAdmin(String username, String password) {
        Admin admin = adminRepository.findByUsername(username);


        /*if (admin != null && admin.getPassword().equals(password)) {
            return admin;
        }*/
        if (admin != null && passwordEncoder.matches(password, admin.getPassword())) {
            return admin;
        }
        return null; // Return null if traveller is not found or password does not match
    }

    @Override
    public String sendOtp(String username) {

        Admin admin = adminRepository.findByUsername(username);
        String email = admin.getEmail();

        if (admin != null) {
            // Generate a random OTP
            String otp = generateOtp(10);

            // Update the user's OTP in the database
            admin.setOtp(otp);
            adminRepository.save(admin);

            // Send the OTP to the user's email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("OTP on Authentication");
            message.setText(otp);

            javaMailSender.send(message);

            return otp;
        } else {
            throw new RuntimeException("Invalid email address.");
        }

    }

    @Override
    public Admin VerifyOtp(String otp, String username) {

        Admin admin = adminRepository.findByUsername(username);

        if (adminRepository!=null && admin.getOtp().equals(otp)){
            String message = "OTP is correct";
            return admin;

        }
        else {
            throw new RuntimeException("Invalid email address.");
        }
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


}

