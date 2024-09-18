package com.EduExplore.System.controller;

import com.EduExplore.System.model.ServiceProvider;
import com.EduExplore.System.service.ProgramService;
import com.EduExplore.System.service.ServiceProviderService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Map;


@RestController
@RequestMapping("/service-provider")
@CrossOrigin
public class ServiceProviderController {
    @Autowired
    private ServiceProviderService serviceProviderService;

    @Autowired
    private ProgramService courseService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        ServiceProvider serviceProvider = serviceProviderService.loginServiceProviderLogin(loginRequest.getUsername(), loginRequest.getPassword());

        if (serviceProvider != null) {
            serviceProviderService.LoginSendOtp(serviceProvider.getEmail(), serviceProvider.getUsername());
            return ResponseEntity.ok("OTP sent to registered email");
        } else {
            return ResponseEntity.status(401).body("Username or password is incorrect");
        }
    }

 /*   @PostMapping("/verify-otp")
    public ResponseEntity<LoginResponse> verifyOtp(@RequestBody OtpRequest otpRequest) {
        try {

            ServiceProvider serviceProvider = serviceProviderService.LoginVerifyOtp(otpRequest.getOtp(), otpRequest.getUsername());
            String serviceProviderId = String.valueOf(serviceProvider.getId());

            return ResponseEntity.ok(new LoginResponse("OTP verification successful!",serviceProviderId));
        } catch (Exception e) {
            return ResponseEntity.status(400).body( new LoginResponse(e.getMessage(),null) );
        }
    }
*/

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest otpRequest) {
        ServiceProvider serviceProvider = serviceProviderService.LoginVerifyOtp(otpRequest.getOtp(), otpRequest.getUsername());
        //String serviceProviderId = String.valueOf(serviceProvider.getId());


        try {
            if (serviceProvider != null) {
                SecureRandom secureRandom = new SecureRandom();
                byte[] key = new byte[32]; // 32 bytes = 256 bits
                secureRandom.nextBytes(key);
                String base64Key = Base64.getEncoder().encodeToString(key);

                // Create JWT Token using the secret key
                String token = Jwts.builder()
                        .setSubject(serviceProvider.getName())
                        .claim("id", serviceProvider.getId())// Add ID to the token
                        .claim("username", serviceProvider.getUsername()) // Add Username to the token
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour expiration
                        .signWith(SignatureAlgorithm.HS256, base64Key)
                        .compact();

                // Return token in response
                return ResponseEntity.ok(Map.of("token", token));
            } else {
                return ResponseEntity.ok("OTP verification failed!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while loging in" +e);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ServiceProvider> getServiceProviderById(@PathVariable int id) {
        try {
            ServiceProvider serviceProvider = serviceProviderService.getServiceProviderDetailsById(id);
            return ResponseEntity.ok(serviceProvider);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/updateProfile/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable int id, @RequestBody ServiceProvider updatedServiceProvider) {
        try {
            ServiceProvider existingServiceProvider = serviceProviderService.getServiceProviderDetailsById(id);

            if (existingServiceProvider != null) {
                // Check if the username or email has changed
                if (!existingServiceProvider.getUsername().equals(updatedServiceProvider.getUsername())) {
                    // Check if the new username is unique
                    if (serviceProviderService.ifUsernameExists(updatedServiceProvider.getUsername())) {
                        return ResponseEntity.ok("Username already exists!");
                    }
                }

                if (!existingServiceProvider.getEmail().equals(updatedServiceProvider.getEmail())) {
                    // Check if the new email is unique
                    if (serviceProviderService.ifEmailExists(updatedServiceProvider.getEmail())) {
                        return ResponseEntity.ok("Email already exists!");
                    }
                }

                // Update the service provider details
                existingServiceProvider.setName(updatedServiceProvider.getName());
                existingServiceProvider.setUsername(updatedServiceProvider.getUsername());
                existingServiceProvider.setEmail(updatedServiceProvider.getEmail());
                existingServiceProvider.setServiceType(updatedServiceProvider.getServiceType());
                existingServiceProvider.setServiceProviderDescription(updatedServiceProvider.getServiceProviderDescription());

                serviceProviderService.saveServiceProvider(existingServiceProvider);
                return ResponseEntity.ok("Profile updated successfully");
            } else {
                return ResponseEntity.status(404).body("Service Provider not found!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while updating the profile");
        }
    }

    @PostMapping("/addService")
    public ResponseEntity<String> addService(@RequestBody ServiceData service) {
//        try {
//            Course program = new Course();
//            program.setName(service.title);
//            program.setPrice(service.price);
//            program.setLocation(service.location);
//            program.setStandard(service.standard);
//            program.setEducationalFocus(service.educationalFocus);
//            program.setEventDuration(service.eventDuration);
//            program.setLearningOutcome(service.learningOutcome);
//            program.setImage(service.image);
//            // Fetch ServiceProvider by id
//            ServiceProvider serviceProvider = serviceProviderService.getServiceProviderDetailsById(service.getServiceProvider());
//            System.out.println("Fetched ServiceProvider: " + serviceProvider);
//            program.setServiceProvider(serviceProvider);
//
//            serviceProvider.addService(program);
//
//            program.setServiceProviderIdCode(service.getServiceProvider());
//
//            courseService.saveCourse(program);
//            return ResponseEntity.ok("ok");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("An error occurred while adding the new service"+ e.getMessage());
//        }
        return null;
    }

    @PostMapping("/updateService")
    public ResponseEntity<String> updateService(@RequestBody ServiceData service) {
//        try {
//            // Course program = new Course();
//            Course program =courseService.getCourseById(service.postId);
//
//            program.setName(service.title);
//            program.setPrice(service.price);
//            program.setLocation(service.location);
//            program.setStandard(service.standard);
//            program.setEducationalFocus(service.educationalFocus);
//            program.setEventDuration(service.eventDuration);
//            program.setLearningOutcome(service.learningOutcome);
//            program.setImage(service.image);
//            program.setServiceProviderIdCode(service.getServiceProvider());
//
//            courseService.saveCourse(program);
//            //  ServiceProvider serviceProvider = serviceProviderService.getServiceProviderDetailsById(service.getServiceProviderId());
//            System.out.println(program.getId());
//
//            // serviceProvider.addToPostIdList(program.getId());
//
//            return ResponseEntity.ok("ok");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("An error occurred while updating the service"+ e.getMessage());
//        }
        return null;
    }

    static class LoginRequest {
        private String username;
        private String password;

        // Getters and setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    static class OtpRequest {
        private String otp;
        private String username;

        // Getters and setters
        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    static class LoginResponse {
        private String message;
        private String id;

        public LoginResponse(String message, String id) {
            this.message = message;
            this.id = id;
        }

        // Getters and setters
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class ServiceData {
        private String title;
        private String price;
        private String standard;
        private String location;
        private String image;
        private String educationalFocus;
        private String learningOutcome;
        private String eventDuration;
        private int serviceProviderId;
        private int postId;


        // Getters and Setters

        public int getPostId() {
            return postId;
        }

        public void setPostId(int postId) {
            this.postId = postId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getStandard() {
            return standard;
        }

        public void setStandard(String standard) {
            this.standard = standard;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getEducationalFocus() {
            return educationalFocus;
        }

        public void setEducationalFocus(String educationalFocus) {
            this.educationalFocus = educationalFocus;
        }

        public String getLearningOutcome() {
            return learningOutcome;
        }

        public void setLearningOutcome(String learningOutcome) {
            this.learningOutcome = learningOutcome;
        }

        public String getEventDuration() {
            return eventDuration;
        }

        public void setEventDuration(String eventDuration) {
            this.eventDuration = eventDuration;
        }

        public int getServiceProvider() {
            System.out.println("Returning serviceProviderId: " + serviceProviderId);
            return this.serviceProviderId; }

        public void setServiceProvider(int serviceProviderId) { this.serviceProviderId = serviceProviderId; }
    }


}
