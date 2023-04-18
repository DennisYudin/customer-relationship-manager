package dev.yudin.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EventDTO {
    private long eventId;
    private String eventName;
    private Date eventDate;
    private int eventPrice;
    private String eventStatus;
    private String eventDescription;

    private List<String> eventCategories;

    private String locationName;
    private String locationWorkingHours;
    private String locationType;
    private String locationAddress;
    private String locationDescription;
    private int capacityPeople;

    private EventDTO(Builder builder) {
        this.eventId = builder.getId();
        this.eventName = builder.getName();
        this.eventDate = builder.getDate();
        this.eventPrice = builder.getPrice();
        this.eventStatus = builder.getEventStatus();
        this.eventDescription = builder.getEventDescription();
        this.eventCategories = builder.getEventCategories();
        this.locationName = builder.getLocationName();
        this.locationWorkingHours = builder.getLocationWorkingHours();
        this.locationType = builder.getLocationType();
        this.locationAddress = builder.getLocationAddress();
        this.locationDescription = builder.getLocationDescription();
        this.capacityPeople = builder.getCapacityPeople();
    }

    public static class Builder {
        private long id;
        private String name;
        private Date date;
        private int price;
        private String eventStatus;
        private String eventDescription;

        private List<String> eventCategories;

        private String locationName;
        private String locationWorkingHours;
        private String locationType;
        private String locationAddress;
        private String locationDescription;
        private int capacityPeople;


        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder price(int price) {
            this.price = price;
            return this;
        }

        public Builder eventStatus(String eventStatus) {
            this.eventStatus = eventStatus;
            return this;
        }

        public Builder eventDescription(String eventDescription) {
            this.eventDescription = eventDescription;
            return this;
        }

        public Builder eventCategories(List<String> eventCategories) {
            this.eventCategories = eventCategories;
            return this;
        }

        public Builder locationName(String locationName) {
            this.locationName = locationName;
            return this;
        }

        public Builder locationWorkingHours(String locationWorkingHours) {
            this.locationWorkingHours = locationWorkingHours;
            return this;
        }

        public Builder locationType(String locationType) {
            this.locationType = locationType;
            return this;
        }

        public Builder locationAddress(String locationAddress) {
            this.locationAddress = locationAddress;
            return this;
        }

        public Builder locationDescription(String locationDescription) {
            this.locationDescription = locationDescription;
            return this;
        }

        public Builder capacityPeople(int capacityPeople) {
            this.capacityPeople = capacityPeople;
            return this;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Date getDate() {
            return date;
        }

        public int getPrice() {
            return price;
        }

        public String getEventStatus() {
            return eventStatus;
        }

        public String getEventDescription() {
            return eventDescription;
        }

        public List<String> getEventCategories() {
            return eventCategories;
        }

        public String getLocationName() {
            return locationName;
        }

        public String getLocationWorkingHours() {
            return locationWorkingHours;
        }

        public String getLocationType() {
            return locationType;
        }

        public String getLocationAddress() {
            return locationAddress;
        }

        public String getLocationDescription() {
            return locationDescription;
        }

        public int getCapacityPeople() {
            return capacityPeople;
        }

        public EventDTO build() {
            return new EventDTO(this);
        }
    }
}

