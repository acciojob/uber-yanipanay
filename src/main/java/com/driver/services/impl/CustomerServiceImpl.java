package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.CabRepository;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Autowired
	CabRepository cabRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		customerRepository2.deleteById(customerId);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		List<Cab> cabList=cabRepository2.findAll();
		TripBooking booking = new TripBooking();

		Customer customer = customerRepository2.findById(customerId).get();
		List<Cab> cabs = cabRepository2.findAll();

		Cab bookedCab = null;
		for(Cab cab : cabs){
			if (cab.getAvailable()){
				bookedCab=cab;
				break;
			}
		}

		int fare = bookedCab.getPerKmRate()*distanceInKm;
		Driver driver = bookedCab.getDriver();

		booking.setCustomer(customer);
		booking.setBill(fare);
		booking.setDriver(driver);

		booking.setDistanceInKm(distanceInKm);
		booking.setFromLocation(fromLocation);
		booking.setToLocation(toLocation);
		booking.setTripStatus(TripStatus.CONFIRMED);

		tripBookingRepository2.save(booking);

		return booking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setTripStatus(TripStatus.CANCELED);
		tripBookingRepository2.save(tripBooking);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setTripStatus(TripStatus.COMPLETED);
		tripBookingRepository2.save(tripBooking);
	}
}
