package com.EduExplore.System.controller;

import com.EduExplore.System.model.*;
import com.EduExplore.System.service.ProgramService;
import com.EduExplore.System.service.ServiceProviderService;
import com.EduExplore.System.service.TravellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/booking")
@CrossOrigin
public class BookingController {
    @Autowired
    private TravellerService travellerService;
    @Autowired
    private ProgramService programService;

    @Autowired
    private ServiceProviderService serviceProviderService;

    @PostMapping("/getList")
    public ResponseEntity<List<CourseDto>> getBookingsList(@RequestBody String id) {
        try {
            // Get the booking list string for the traveler
            String bookingList = travellerService.getTravellerById(id).getBookings();
            System.out.println(bookingList+"booking list id :"+id);
            // Split the booking list string into individual ID strings
            String[] idStrings = bookingList.split(",");

            // Convert the ID strings into a list of Integer IDs
            List<Integer> programIDs = Arrays.stream(idStrings)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            // Fetch each course by its ID, convert to CourseDto, and collect them into a list
            List<CourseDto> courseDtos = programIDs.stream()
                    .map(programService::getCourseById) // Fetch each course by ID
                    .map(program -> {
                        // Create a new CourseDto object and populate it
                        CourseDto dto = new CourseDto();
                        dto.setId(program.getId());
                        dto.setTitle(program.getTitle());
                        dto.setPrice(program.getPrice());
                        dto.setLocation(program.getLocation());
                        dto.setImage(program.getImages());
                        dto.setType(program.getType());

                        switch (program.getType()) {
                            case "Course":
                                CourseProgram courseProgram = (CourseProgram) program;
                                dto.setDuration(courseProgram.getDuration());
                                dto.setParticipants(courseProgram.getParticipants());
                                break;

                            case "Hotel":
                                HotelProgram hotelProgram = (HotelProgram) program;
                                dto.setCheckInDate(hotelProgram.getCheckInDate());
                                dto.setCheckOutDate(hotelProgram.getCheckOutDate());
                                dto.setRooms(hotelProgram.getRooms());
                                break;

                            case "TravelLocation":
                                TravelLocationProgram travelLocationProgram = (TravelLocationProgram) program;
                                dto.setTravelDate(travelLocationProgram.getTravelDate());
                                dto.setTravelers(travelLocationProgram.getTravelers());
                                dto.setTravelType(travelLocationProgram.getTravelType());

                                break;

                            // Add cases for other program types like Package, Transport, Event, etc.
                            case "Package":
                                PackageProgram packageProgram = (PackageProgram) program;
                                dto.setStartDate(packageProgram.getStartDate());
                                dto.setEndDate(packageProgram.getEndDate());
                                dto.setAccommodationType(packageProgram.getAccommodationType());
                                break;

                            case "Transport":
                                TransportProgram transportProgram = (TransportProgram) program;
                                dto.setTransportType(transportProgram.getTransportType());
                                dto.setPickupLocation(transportProgram.getPickupLocation());
                                dto.setDropoffLocation(transportProgram.getDropoffLocation());
                                dto.setPickupTime(transportProgram.getPickupTime());
                                dto.setPassengers(transportProgram.getPassengers());
                                break;

                            case "Event":
                                EventProgram eventProgram = (EventProgram) program;
                                dto.setEventDate(eventProgram.getEventDate());
                                dto.setStartTime(eventProgram.getStartTime());
                                dto.setAttendees(eventProgram.getAttendees());
                                dto.setVenue(eventProgram.getVenue());
                                break;


                        }
                        return dto;
                    })
                    .collect(Collectors.toList());
            System.out.println("get booking list :"+id);
            // Return the list of CourseDto objects in the response
            return ResponseEntity.ok(courseDtos);
        } catch (Exception e) {
            System.out.println("get booking list error:"+id);
            // Handle any exceptions and return a 500 status code with an error message
            return ResponseEntity.status(500).body(Collections.singletonList(null)); // Return null in case of error
        }
    }


    @PostMapping("/setList")
    public ResponseEntity<String> setBookingsList(@RequestBody bookingUpdate bookingupdate) {

        try {
           Traveller temp =  travellerService.getTravellerById(bookingupdate.getId());

           temp.setBookings(bookingupdate.getBookingList());

           travellerService.updateTraveller(temp);

            System.out.println("set booking list:"+temp.getBookings());
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while updating the booking list");
        }
    }
    @PostMapping("/serviceProviderSignup")
    public ResponseEntity<String> add(@RequestBody ServiceProvider serviceProvider) {
        try {
            if (serviceProviderService.ifUsernameExists(serviceProvider.getUsername())) {
                return ResponseEntity.ok("Username already exists");
            }

            if (serviceProviderService.ifEmailExists(serviceProvider.getEmail())) {
                return ResponseEntity.ok("Email already exists");
            }

            serviceProviderService.saveServiceProvider(serviceProvider);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while adding the new service provider");
        }
    }

    @PostMapping("/bookService")
    public ResponseEntity<String> bookService(@RequestBody BookingCriteria bookingCriteria) {

        int travellerID = bookingCriteria.getTravelerID();
        int serviceID = bookingCriteria.getServiceID();
        // System.out.println("Traveller ID: " + travellerID + ", Service ID: " + serviceID);
        Traveller traveller = null;
        try {
            traveller = travellerService.saveTravellerBooking(travellerID, serviceID);

            if (traveller != null) {
                return ResponseEntity.ok("Booking successfully for user " + traveller.getUsername());
            } else {
                return ResponseEntity.status(404).body("Traveler doesn't exist or booking failed");
            }
        } catch (Exception e) {
            e.printStackTrace(); // This will print the stack trace to the server logs
            return ResponseEntity.status(500).body("An error occurred while adding the new booking: " + e.getMessage());
        }
    }

    public static class BookingCriteria {
        private int travelerID=0;


        private int serviceID=0;

        public int getTravelerID() {
            return travelerID;
        }
        public int getServiceID() {
            return serviceID;
        }

        public void setServiceID(int serviceID) {
            this.serviceID = serviceID;
        }
        public void setTravelerID(int travelerID) {
            this.travelerID = travelerID;
        }
    }

    // Inner class representing the login request JSON structure
    static class bookingUpdate {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBookingList() {
            return bookingList;
        }

        public void setBookingList(String bookingList) {
            this.bookingList = bookingList;
        }

        private String bookingList;

        // Getters and setters

    }

    public class CourseDto {
        private int id;
        private String title;
        private String price;
        private String location;

        public void setImage(List<String> image) {
            this.image = image;
        }

        private List<String> image;
        private String type;  // Added to track program type

        // Course-specific fields
        private String duration;
        private int participants;

        // Hotel-specific fields
        private String checkInDate;
        private String checkOutDate;
        private int rooms;

        // TravelLocation-specific fields
        private String travelDate;
        private int travelers;
        private String travelType;
        private String accommodation;

        // Package-specific fields
        private String startDate;
        private String endDate;
        private String accommodationType;

        // Transport-specific fields
        private String transportType;
        private String pickupLocation;
        private String dropoffLocation;
        private String pickupTime;
        private int passengers;

        // Event-specific fields
        private String eventDate;
        private String startTime;
        private int attendees;
        private String venue;

        // Getters and Setters for all fields

        // Common fields
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public List<String> getImage() {
            return image;
        }



        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        // Course-specific fields
        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public int getParticipants() {
            return participants;
        }

        public void setParticipants(int participants) {
            this.participants = participants;
        }

        // Hotel-specific fields
        public String getCheckInDate() {
            return checkInDate;
        }

        public void setCheckInDate(String checkInDate) {
            this.checkInDate = checkInDate;
        }

        public String getCheckOutDate() {
            return checkOutDate;
        }

        public void setCheckOutDate(String checkOutDate) {
            this.checkOutDate = checkOutDate;
        }

        public int getRooms() {
            return rooms;
        }

        public void setRooms(int rooms) {
            this.rooms = rooms;
        }

        // TravelLocation-specific fields
        public String getTravelDate() {
            return travelDate;
        }

        public void setTravelDate(String travelDate) {
            this.travelDate = travelDate;
        }

        public int getTravelers() {
            return travelers;
        }

        public void setTravelers(int travelers) {
            this.travelers = travelers;
        }

        public String getTravelType() {
            return travelType;
        }

        public void setTravelType(String travelType) {
            this.travelType = travelType;
        }

        public String getAccommodation() {
            return accommodation;
        }

        public void setAccommodation(String accommodation) {
            this.accommodation = accommodation;
        }

        // Package-specific fields
        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getAccommodationType() {
            return accommodationType;
        }

        public void setAccommodationType(String accommodationType) {
            this.accommodationType = accommodationType;
        }

        // Transport-specific fields
        public String getTransportType() {
            return transportType;
        }

        public void setTransportType(String transportType) {
            this.transportType = transportType;
        }

        public String getPickupLocation() {
            return pickupLocation;
        }

        public void setPickupLocation(String pickupLocation) {
            this.pickupLocation = pickupLocation;
        }

        public String getDropoffLocation() {
            return dropoffLocation;
        }

        public void setDropoffLocation(String dropoffLocation) {
            this.dropoffLocation = dropoffLocation;
        }

        public String getPickupTime() {
            return pickupTime;
        }

        public void setPickupTime(String pickupTime) {
            this.pickupTime = pickupTime;
        }

        public int getPassengers() {
            return passengers;
        }

        public void setPassengers(int passengers) {
            this.passengers = passengers;
        }

        // Event-specific fields
        public String getEventDate() {
            return eventDate;
        }

        public void setEventDate(String eventDate) {
            this.eventDate = eventDate;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public int getAttendees() {
            return attendees;
        }

        public void setAttendees(int attendees) {
            this.attendees = attendees;
        }

        public String getVenue() {
            return venue;
        }

        public void setVenue(String venue) {
            this.venue = venue;
        }
    }


}
