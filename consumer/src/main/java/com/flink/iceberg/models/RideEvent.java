package com.flink.iceberg.models;

import java.io.Serializable;

public class RideEvent implements Serializable {
    private long bookingId;
    private int userId;
    private int driverId;
    private Location pickupLocation;
    private Location dropoffLocation;
    private double fare;
    private double distanceKm;
    private int durationMin;
    private String status;
    private String paymentMethod;
    private long timestampNs;
    private long scheduledTimeNs;
    private String vehicleType;
    private String rideType;
    private int passengerCount;
    private double driverRating;
    private double userRating;
    private String promoCode;
    private String cancellationReason;

    public static class Location {
        private double latitude;
        private double longitude;
        private String address;

        public Location() {}

        public Location(double latitude, double longitude, String address) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
        }

        // Getters and Setters
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }

        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        @Override
        public String toString() {
            return "Location{latitude=" + latitude + ", longitude=" + longitude + ", address='" + address + "'}";
        }
    }

    public RideEvent() {}

    public RideEvent(long bookingId, int userId, int driverId, Location pickupLocation, Location dropoffLocation,
                     double fare, double distanceKm, int durationMin, String status, String paymentMethod,
                     long timestampNs, long scheduledTimeNs, String vehicleType, String rideType, int passengerCount,
                     double driverRating, double userRating, String promoCode, String cancellationReason) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.driverId = driverId;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.fare = fare;
        this.distanceKm = distanceKm;
        this.durationMin = durationMin;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.timestampNs = timestampNs;
        this.scheduledTimeNs = scheduledTimeNs;
        this.vehicleType = vehicleType;
        this.rideType = rideType;
        this.passengerCount = passengerCount;
        this.driverRating = driverRating;
        this.userRating = userRating;
        this.promoCode = promoCode;
        this.cancellationReason = cancellationReason;
    }

    // Getters and Setters
    public long getBookingId() { return bookingId; }
    public void setBookingId(long bookingId) { this.bookingId = bookingId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public Location getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(Location pickupLocation) { this.pickupLocation = pickupLocation; }

    public Location getDropoffLocation() { return dropoffLocation; }
    public void setDropoffLocation(Location dropoffLocation) { this.dropoffLocation = dropoffLocation; }

    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public int getDurationMin() { return durationMin; }
    public void setDurationMin(int durationMin) { this.durationMin = durationMin; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public long getTimestampNs() { return timestampNs; }
    public void setTimestampNs(long timestampNs) { this.timestampNs = timestampNs; }

    public long getScheduledTimeNs() { return scheduledTimeNs; }
    public void setScheduledTimeNs(long scheduledTimeNs) { this.scheduledTimeNs = scheduledTimeNs; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getRideType() { return rideType; }
    public void setRideType(String rideType) { this.rideType = rideType; }

    public int getPassengerCount() { return passengerCount; }
    public void setPassengerCount(int passengerCount) { this.passengerCount = passengerCount; }

    public double getDriverRating() { return driverRating; }
    public void setDriverRating(double driverRating) { this.driverRating = driverRating; }

    public double getUserRating() { return userRating; }
    public void setUserRating(double userRating) { this.userRating = userRating; }

    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    @Override
    public String toString() {
        return "RideEvent{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", driverId=" + driverId +
                ", pickupLocation=" + pickupLocation +
                ", dropoffLocation=" + dropoffLocation +
                ", fare=" + fare +
                ", distanceKm=" + distanceKm +
                ", durationMin=" + durationMin +
                ", status='" + status + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", timestampNs=" + timestampNs +
                ", scheduledTimeNs=" + scheduledTimeNs +
                ", vehicleType='" + vehicleType + '\'' +
                ", rideType='" + rideType + '\'' +
                ", passengerCount=" + passengerCount +
                ", driverRating=" + driverRating +
                ", userRating=" + userRating +
                ", promoCode='" + promoCode + '\'' +
                ", cancellationReason='" + cancellationReason + '\'' +
                '}';
    }
}

