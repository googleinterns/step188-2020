package com.google.sps.data;

import java.util.HashSet;
import java.util.Set;

public final class User {
  private final long userId = 10000000L;
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

    public Builder setInterests(Set<String> interests) {
      this.interests = interests;
      return this;
    }

    public Builder setSkills(Set<String> skills) {
      this.skills = skills;
      return this;
    }

    public Builder setEventsHosting(Set<Event> eventsHosting) {
      this.eventsHosting = eventsHosting;
      return this;
    }

    public Builder setEventsParticipating(Set<Event> eventsParticipating) {
      this.eventsParticipating = eventsParticipating;
      return this;
    }

    public Builder setEventsVolunteering(Set<Event> eventsVolunteering) {
      this.eventsVolunteering = eventsVolunteering;
      return this;
    }

    public User build() {
      return new User(this);
    }

    public Builder mergeFrom(User other) {
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

  public Set<Event> getEventsParticipating() {
    return this.eventsParticipating;
  }

  public Set<Event> getEventsVolunteering() {
    return this.eventsVolunteering;
  }

  public Builder toBuilder() {
    return new Builder(this.name, this.email).mergeFrom(this);
  }
}
