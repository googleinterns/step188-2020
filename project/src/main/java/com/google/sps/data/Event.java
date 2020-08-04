package com.google.sps.data;

import com.google.cloud.Date;
import java.lang.StringBuilder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Class containing Event object setters for variables that user can change about event */
public final class Event {
  private final String eventId;
  private String name;
  private String description;
  private Set<String> labels;
  private String location;
  private Date date;
  private Set<VolunteeringOpportunity> opportunities;
  private Set<User> attendees;
  private User host;

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("Event Info: \n");
    str.append("ID: " + eventId + "\n");
    str.append("Host: " + host.toString() + "\n");
    str.append("Name: " + name + "\n");
    str.append("Description: " + description + "\n");
    str.append("Location: " + location + "\n");
    str.append("Date: " + date.toString() + "\n");
    str.append("Volunteer Opportunities: ");
    for (VolunteeringOpportunity opportunity : this.opportunities) {
      str.append(opportunity.toString() + "\n");
    }
    str.append("Attendees: ");
    for (User attendee : this.attendees) {
      str.append(attendee.toString() + "\n");
    }
    return str.toString();
  }

  public static class Builder {
    // Required parameters
    private String name;
    private String description;
    private Set<String> labels;
    private String location;
    private Date date;
    private User host;

    // Optional parameters
    private String eventId = UUID.randomUUID().toString();
    private Set<VolunteeringOpportunity> opportunities = new HashSet<>();
    private Set<User> attendees = new HashSet<>();

    public Builder(String name, String description, Set<String> labels, String location, Date date,
        User host) {
      this.name = name;
      this.description = description;
      this.labels = labels;
      this.location = location;
      this.date = date;
      this.host = host;
    }

    public Builder setId(String eventId) {
      this.eventId = eventId;
      return this;
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

    public Builder setDate(Date date) {
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
    this.eventId = builder.eventId;
    this.name = builder.name;
    this.description = builder.description;
    this.labels = builder.labels;
    this.location = builder.location;
    this.date = builder.date;
    this.opportunities = builder.opportunities;
    this.attendees = builder.attendees;
    this.host = builder.host;
  }

  public String getId() {
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

  public Date getDate() {
    return this.date;
  }

  public Set<VolunteeringOpportunity> getOpportunities() {
    return this.opportunities;
  }

  public Set<String> getOpportunitiesIds() {
    Set<String> opportunityIds = new HashSet<>();
    for (VolunteeringOpportunity opportunity : this.opportunities) {
      opportunityIds.add(opportunity.getOpportunityId());
    }
    return opportunityIds;
  }

  public Set<User> getAttendees() {
    return this.attendees;
  }

  public Set<Long> getAttendeeIds() {
    Set<Long> attendeeIds = new HashSet<>();
    for (User attendee : this.attendees) {
      attendeeIds.add(attendee.getUserId());
    }
    return attendeeIds;
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
