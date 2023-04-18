package dev.yudin.entities;

import lombok.Data;

import java.util.Date;

@Data
public class Event {
    private long id;
    private String title;
    private Date date;
    private int price;
    private String status;
    private String description;
    private long locationId;
}

