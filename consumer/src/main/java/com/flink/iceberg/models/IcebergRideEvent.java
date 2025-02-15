package com.flink.iceberg.models;

import java.io.Serializable;

public class IcebergRideEvent implements Serializable {
    private long bookingId;
    private int userId;
    private int driverId;
    private double fare;
    private double distanceKm;
    private int durationMin;
    private String status;
    private String paymentMethod;
    private long timestampNs;
    private String vehicleType;
    private String rideType;
    private int passengerCount;

    public IcebergRideEvent() {}

    public IcebergRideEvent(long bookingId, int userId, int driverId, double fare, double distanceKm, int durationMin,
                           String status, String paymentMethod, long timestampNs, String vehicleType, String rideType,
                           int passengerCount) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.driverId = driverId;
        this.fare = fare;
        this.distanceKm = distanceKm;
        this.durationMin = durationMin;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.timestampNs = timestampNs;
        this.vehicleType = vehicleType;
        this.rideType = rideType;
        this.passengerCount = passengerCount;
    }

    // Getters and Setters
    public long getBookingId() { return bookingId; }
    public void setBookingId(long bookingId) { this.bookingId = bookingId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

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

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getRideType() { return rideType; }
    public void setRideType(String rideType) { this.rideType = rideType; }

    public int getPassengerCount() { return passengerCount; }
    public void setPassengerCount(int passengerCount) { this.passengerCount = passengerCount; }

    @Override
    public String toString() {
        return "IcebergRideEvent{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", driverId=" + driverId +
                ", fare=" + fare +
                ", distanceKm=" + distanceKm +
                ", durationMin=" + durationMin +
                ", status='" + status + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", timestampNs=" + timestampNs +
                ", vehicleType='" + vehicleType + '\'' +
                ", rideType='" + rideType + '\'' +
                ", passengerCount=" + passengerCount +
                '}';
    }
}