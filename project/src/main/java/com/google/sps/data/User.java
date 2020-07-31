package com.google.sps.data;

import java.util.HashSet;
import java.util.Set;

public final class User {
  private String name;
  private String email;
  private Set<String> interests;
  private Set<String> skills;
  private Set<Event> eventsHosting;
  private Set<Event> eventsParticipating;
  private Set<Event> eventsVolunteering;

  public static class Builder {
    // Required parameters
    private String name;
    private String email;

    // Optional parameters
    private Set<String> interests = new HashSet<>();
    private Set<String> skills = new HashSet<>();
    private Set<Event> eventsHosting = new HashSet<>();
    private Set<Event> eventsParticipating = new HashSet<>();
    private Set<Event> eventsVolunteering = new HashSet<>();

    public Builder(String name, String email) {
      this.name = name;
      this.email = email;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setEmail(String email) {
      this.email = email;
      return this;
    }

    public Builder setInterests(Set<String> interests) {
      this.interests = interests;
      return this;
    }

    public Builder addInterest(String interest) {
      this.interests.add(interest);
      return this;
    }

    public Builder removeInterest(String interest) {
      this.interests.remove(interest);
      return this;
    }

    public Builder setSkills(Set<String> skills) {
      this.skills = skills;
      return this;
    }

    public Builder addSkill(String skill) {
      this.skills.add(skill);
      return this;
    }

    public Builder removeSkill(String skill) {
      this.skills.remove(skill);
      return this;
    }

    public Builder setEventsHosting(Set<Event> eventsHosting) {
      this.eventsHosting = eventsHosting;
      return this;
    }

    public Builder addEventHosting(Event eventHosting) {
      this.eventsHosting.add(eventHosting);
      return this;
    }

    public Builder removeEventHosting(Event eventHosting) {
      this.eventsHosting.remove(eventHosting);
      return this;
    }

    public Builder setEventsParticipating(Set<Event> eventsParticipating) {
      this.eventsParticipating = eventsParticipating;
      return this;
    }

    public Builder addEventParticipating(Event eventParticipating) {
      this.eventsParticipating.add(eventParticipating);
      return this;
    }

    public Builder removeEventParticipating(Event eventParticipating) {
      this.eventsParticipating.remove(eventParticipating);
      return this;
    }

    public Builder setEventsVolunteering(Set<Event> eventsVolunteering) {
      this.eventsVolunteering = eventsVolunteering;
      return this;
    }

    public Builder addEventVolunteering(Event eventVolunteering) {
      this.eventsVolunteering.add(eventVolunteering);
      return this;
    }

    public Builder removeEventVolunteering(Event eventVolunteering) {
      this.eventsVolunteering.remove(eventVolunteering);
      return this;
    }

    public User build() {
      return new User(this);
    }

    private Builder mergeFrom(User other) {
      this.name = other.getName();
      this.email = other.getEmail();

      if (!other.getInterests().isEmpty()) {
        this.interests = other.getInterests();
      }
      if (!other.getSkills().isEmpty()) {
        this.skills = other.getSkills();
      }
      if (!other.getEventsHosting().isEmpty()) {
        this.eventsHosting = other.getEventsHosting();
      }
      if (!other.getEventsParticipating().isEmpty()) {
        this.eventsParticipating = other.getEventsParticipating();
      }
      if (!other.getEventsVolunteering().isEmpty()) {
        this.eventsVolunteering = other.getEventsVolunteering();
      }
      return this;
    }
  }

  private User(Builder builder) {
    this.name = builder.name;
    this.email = builder.email;

    this.interests = builder.interests;
    this.skills = builder.skills;
    this.eventsHosting = builder.eventsHosting;
    this.eventsParticipating = builder.eventsParticipating;
    this.eventsVolunteering = builder.eventsVolunteering;
  }

  public String getName() {
    return this.name;
  }

  public String getEmail() {
    return this.email;
  }

  public Set<String> getInterests() {
    return this.interests;
  }

  public Set<String> getSkills() {
    return this.skills;
  }

  public Set<Event> getEventsHosting() {
    return this.eventsHosting;
  }

  /** Returns a Set of eventHosting Id fields */
  public Set<String> getEventsHostingIds() {
    Set<String> eventIds = new HashSet<>();
    for (Event event : this.eventsHosting) {
      eventIds.add(event.getId());
    }
    return eventIds;
  }

  public Set<Event> getEventsParticipating() {
    return this.eventsParticipating;
  }

  /** Returns a Set of eventParticipating Id fields */
  public Set<String> getEventsParticipatingIds() {
    Set<String> eventIds = new HashSet<>();
    for (Event event : this.eventsParticipating) {
      eventIds.add(event.getId());
    }
    return eventIds;
  }

  public Set<Event> getEventsVolunteering() {
    return this.eventsVolunteering;
  }

  /** Returns a Set of eventVolunteering Id fields */
  public Set<String> getEventsVolunteeringIds() {
    Set<String> eventIds = new HashSet<>();
    for (Event event : this.eventsVolunteering) {
      eventIds.add(event.getId());
    }
    return eventIds;
  }

  public Builder toBuilder() {
    return new Builder(this.name, this.email).mergeFrom(this);
  }

  @Override
  public String toString() {
    return String.format("User:\t%s\nE-mail:\t%s", this.name, this.email);
  }
}
