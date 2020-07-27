package com.google.sps.data;

import java.lang.StringBuilder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
Class containing Event object
Setters for variables that user can change about event
*/
public final class Event {
  private long eventId;
  private String name;
  private String description;
  private Set<String> labels;
  private String location;
  private LocalDate date;
  private Set<VolunteeringOpportunity> opportunities;
  private Set<User> attendees;
  private User host;

  @Override
  public String toString() {
    // TO DO: add VolunteeringOpp and User toString() based on those classes when pushed
    StringBuilder str = new StringBuilder();
    str.append("Event Info: \n");
    str.append("Name: " + name + "\n");
    str.append("Description: " + description + "\n");
    str.append("Location: " + location + "\n");
    str.append("Date: " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n");

    return str.toString();
  }

  public static class Builder {
    // Required parameters
    private final long eventId;
    private String name;
    private String description;
    private Set<String> labels;
    private String location;
    private LocalDate date;
    private User host;

    // Optional parameters
    private Set<VolunteeringOpportunity> opportunities = new HashSet<>();
    private Set<User> attendees = new HashSet<>();

    public Builder(String name, String description, Set<String> labels, String location,
        LocalDate date, User host) {
      this.eventId = 1000000L; //will be 
      this.name = name;
      this.description = description;
      this.labels = labels;
      this.location = location;
      this.date = date;
      this.host = host;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder setLabels(Set<String> labels) {
      this.labels = labels;
      return this;
    }

    public Builder setLocation(String location) {
      this.location = location;
      return this;
    }

    public Builder setDate(LocalDate date) {
      this.date = date;
      return this;
    }

    public Builder setHost(User host) {
      this.host = host;
      return this;
    }

    public Builder setOpportunities(Set<VolunteeringOpportunity> opportunities) {
      this.opportunities = opportunities;
      return this;
    }

    public Builder addOpportunity(VolunteeringOpportunity opportunity) {
      this.opportunities.add(opportunity);
      return this;
    }

    public Builder removeOpportunity(VolunteeringOpportunity opportunity) {
      this.opportunities.remove(opportunity);
      return this;
    }

    public Builder setAttendees(Set<User> attendees) {
      this.attendees = attendees;
      return this;
    }

    public Builder addAttendee(User attendee) {
      this.attendees.add(attendee);
      return this;
    }

    public Builder removeAttendee(User attendee) {
      this.attendees.remove(attendee);
      return this;
    }

    public Event build() {
      return new Event(this);
    }

    public Builder mergeFrom(Event other) {
      this.name = other.getName();
      this.description = other.getDescription();
      this.labels = other.getLabels();
      this.location = other.getLocation();
      this.date = other.getDate();
      this.host = other.getHost();

      if (!other.getOpportunities().isEmpty()) {
        this.opportunities = other.getOpportunities();
      }
      if (!other.getAttendees().isEmpty()) {
        this.attendees = other.getAttendees();
      }
      return this;
    }
  }

  private Event(Builder builder) {
    this.name = builder.name;
    this.description = builder.description;
    this.labels = builder.labels;
    this.location = builder.location;
    this.date = builder.date;
    this.opportunities = builder.opportunities;
    this.attendees = builder.attendees;
    this.host = builder.host;
  }

  /** TO DO (MVP) for all getters: get from Event db*/
  /** TO DO (MVP) for all setters: set in Event db*/
  public long getID() {
    return this.eventId;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public Set<String> getLabels() {
    return this.labels;
  }

  public void setLabel(String newLabel) {
    this.labels.add(newLabel);
  }

  public void removeLabel(String deletedLabel) {
    this.labels.remove(deletedLabel);
  }

  public String getLocation() {
    return this.location;
  }

  public LocalDate getDate() {
    return this.date;
  }

  public Set<VolunteeringOpportunity> getOpportunities() {
    return this.opportunities;
  }

  public Set<User> getAttendees() {
    return this.attendees;
  }

  public User getHost() {
    return this.host;
  }

  public Builder toBuilder() {
    return new Builder(
        this.name, this.description, this.labels, this.location, this.date, this.host)
        .mergeFrom(this);
  }
}