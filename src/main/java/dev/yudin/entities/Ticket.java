package dev.yudin.entities;

import lombok.Data;

import java.util.Date;

@Data
public class Ticket {
    private long id;
    private String eventName;
    private String uniqueCode;
    private Date creationDate;
    private String status;
    private long userId;
    private long eventId;
}

