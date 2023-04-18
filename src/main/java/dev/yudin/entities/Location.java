package dev.yudin.entities;

import lombok.Data;

@Data
public class Location {
    private long id;
    private String title;
    private String workingHours;
    private String type;
    private String address;
    private String description;
    private int capacityPeople;
}

