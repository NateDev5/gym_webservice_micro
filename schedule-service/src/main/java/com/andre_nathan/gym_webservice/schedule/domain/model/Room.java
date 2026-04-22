package com.andre_nathan.gym_webservice.schedule.domain.model;

import com.andre_nathan.gym_webservice.schedule.domain.exception.InvalidCapacityException;

import java.util.Objects;

public class Room {
    private final RoomId roomId;
    private final String roomName;
    private final int roomCapacity;

    public Room(RoomId roomId, String roomName, int roomCapacity) {
        this.roomId = Objects.requireNonNull(roomId, "roomId cannot be null");
        this.roomName = Objects.requireNonNull(roomName, "roomName cannot be null").trim();
        this.roomCapacity = roomCapacity;

        if (roomCapacity <= 0) {
            throw new InvalidCapacityException("roomCapacity must be greater than 0");
        }
    }

    public RoomId getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getRoomCapacity() {
        return roomCapacity;
    }
}
