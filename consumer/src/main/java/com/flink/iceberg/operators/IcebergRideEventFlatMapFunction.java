package com.flink.iceberg.operators;

import com.flink.iceberg.models.RideEvent;
import com.flink.iceberg.models.IcebergRideEvent;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class IcebergRideEventFlatMapFunction implements FlatMapFunction<RideEvent, IcebergRideEvent> {

    @Override
    public void flatMap(RideEvent rideEvent, Collector<IcebergRideEvent> out) {
        IcebergRideEvent simpleRideEvent = new IcebergRideEvent(
            rideEvent.getBookingId(),
            rideEvent.getUserId(),
            rideEvent.getDriverId(),
            rideEvent.getFare(),
            rideEvent.getDistanceKm(),
            rideEvent.getDurationMin(),
            rideEvent.getStatus(),
            rideEvent.getPaymentMethod(),
            rideEvent.getTimestampNs(),
            rideEvent.getVehicleType(),
            rideEvent.getRideType(),
            rideEvent.getPassengerCount()
        );
        out.collect(simpleRideEvent);
    }
}