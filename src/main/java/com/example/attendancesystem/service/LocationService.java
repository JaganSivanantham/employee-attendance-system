package com.example.attendancesystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    @Value("${company.location.latitude}")
    private double companyLat;

    @Value("${company.location.longitude}")
    private double companyLon;

    @Value("${company.location.radius.meters}")
    private double allowedRadius;

    public boolean isWithinCompanyRadius(double userLat, double userLon) {
        double distance = calculateDistance(userLat, userLon, companyLat, companyLon);
        return distance <= allowedRadius;
    }

    // Haversine formula to calculate distance between two lat-lon points
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        return distance;
    }
}