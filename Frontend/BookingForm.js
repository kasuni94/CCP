import React, { useEffect, useState } from 'react';
import './BookingForms.css'; // Import the CSS file where the styles are defined
import Notification from './Notification'; 


const BookingForm = ({ type, onClose, authenticatedID, selectedCourseBooking}) => {
  console.log("selected id and user in bookingform.js"+ authenticatedID+selectedCourseBooking.id);

  const [isActive, setIsActive] = useState(false);

  const [authID, setAuthID] = useState(null);

  const [selectedID, setSelectedID] = useState(null);

  const [selectedCourse, setselectedCourse] = useState(null);

  const [notification, setNotification] = useState(null);
  const showNotification = (message) => {
    setNotification(message);
};

  const handleNotificationClose = () => {
      setNotification(null); // Clear the notification when it is closed
  };

  // Trigger the transition animation after the component is mounted
  useEffect(() => {

    setIsActive(true); // Activate the animation once the component is mounted
    setAuthID(authenticatedID);
    setSelectedID(selectedCourseBooking.id);
    setselectedCourse(selectedCourseBooking);
console.log("selected id and user in bookingform.js"+ authID+selectedCourseBooking.id);
    // Clean up function to handle animation when closing the form
    return () => setIsActive(false);
  }, []);

  useEffect(() => {
    console.log("Updated authID and selectedID:", authID, selectedID);
  }, [authID, selectedID]);

  const renderFormFields = () => {
    console.log();
    switch (type) {
      case 'Course':
        return (
          <div>
            <h2>Book a Course</h2>
            <input type="text" placeholder="Course Name" required />
            <input type="date" placeholder="Start Date" required />
            <input type="number" placeholder="Number of Participants" required />
            <input type="text" placeholder="Location" required />
            <input type="text" placeholder="Duration" required />
            <textarea placeholder="Additional Notes"></textarea>
          </div>
        );
      case 'Travel Location':
        return (
          <div>
            <h2>Book a Travel Location</h2>
            <input type="text" placeholder="Destination" required />
            <input type="date" placeholder="Travel Date" required />
            <input type="number" placeholder="Number of Travelers" required />
            <input type="text" placeholder="Travel Type (e.g., Leisure, Business)" />
            <input type="text" placeholder="Accommodation Preference" />
            <textarea placeholder="Special Requests"></textarea>
          </div>
        );
      case 'Hotel':
        return (
          <div>
            <h2>Book a Hotel</h2>
            <input type="text" placeholder="Hotel Name" required />
            <input type="date" placeholder="Check-in Date" required />
            <input type="date" placeholder="Check-out Date" required />
            <input type="number" placeholder="Number of Rooms" required />
            <input type="number" placeholder="Number of Guests per Room" required />
          </div>
        );
      case 'Package':
        return (
          <div>
            <h2>Book a Package</h2>
            <input type="text" placeholder="Package Name" required />
            <input type="date" placeholder="Start Date" required />
            <input type="date" placeholder="End Date" />
            <input type="number" placeholder="Number of Participants" required />
            <input type="text" placeholder="Accommodation Type" />
            <textarea placeholder="Customizations or Special Requests"></textarea>
          </div>
        );
      case 'Transport':
        return (
          <div>
            <h2>Book Transport</h2>
            <input type="text" placeholder="Transport Type (e.g., Car, Bus, Flight)" required />
            <input type="date" placeholder="Date" required />
            <input type="time" placeholder="Pickup Time" required />
            <input type="text" placeholder="Pickup Location" required />
            <input type="text" placeholder="Drop-off Location" required />
            <input type="number" placeholder="Number of Passengers" required />
            <textarea placeholder="Additional Notes"></textarea>
          </div>
        );
      case 'Event':
        return (
          <div>
            <h2>Book an Event</h2>
            <input type="text" placeholder="Event Name" required />
            <input type="date" placeholder="Event Date" required />
            <input type="time" placeholder="Start Time" required />
            <input type="number" placeholder="Number of Attendees" required />
            <input type="text" placeholder="Venue" required />
            <textarea placeholder="Special Requirements (e.g., Seating Arrangement, AV Setup)"></textarea>
          </div>
        );
      default:
        return <div>Please select a valid booking type.</div>;
    }
  };
 

  const confirmBooking = async () => {
    try {
      const travelerID = authID;
      const serviceID = selectedID;
      const bookCriteria = {
        travelerID,
        serviceID
      };
      const response = await fetch('http://localhost:8080/booking/bookService', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(bookCriteria),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      const result = await response.text();
      console.log(result.message); // "Course booked successfully!"
      onClose();
      alert("Course booked successfully!") 
     

    } catch (error) {
      console.error('Error booking course:', error);
    }

    const itinerary = {
      stop: selectedCourse.title,
      location: selectedCourse.location, // Use the course location
      description: "Enjoy this amazing travel experience", // Common description
      date: new Date().toISOString().split('T')[0], // Set today's date (YYYY-MM-DD)
    };
  
    try {
      console.log("Booking course for user ID:", authID);
      console.log("Booking course for course title:", selectedCourse.title);
  
      console.log("Booking course for course location:", selectedCourse.location);
  
  
      // Send the itinerary object to the backend
      const response = await fetch(`http://localhost:8080/api/travellers/${authID}/itinerary`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(itinerary), // Send the full itinerary object
      });
  
      if (response.ok) {
        console.log("Course booked successfully");
        alert(`Course "${selectedCourseBooking.title}" booked successfully!`);
      } else {
        const errorData = await response.json();
        console.error("Failed to book course:", errorData);
        alert(`Failed to book course: ${errorData.message || 'Unknown error'}`);
      }
    } catch (error) {
      console.error('Error booking course:', error);
      alert('An error occurred while booking the course.');
    }

  };

  return (
    <div className={`booking-form-wrapper ${isActive ? 'active' : ''}`}>
      <div className="booking-form">
        <button className="book-close-button" onClick={onClose}>×</button>
        {renderFormFields()}
      
        <button type="submit" onClick={confirmBooking}>Submit</button>
      
      </div>
   
    </div>
  );
};

export default BookingForm;
