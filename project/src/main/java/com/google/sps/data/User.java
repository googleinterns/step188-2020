package com.google.sps.data;

import java.util.HashSet;
import java.util.Set;

public final class User {
  private final long userId;
  private String name;
  private String email;
  private Set<String> interests;
  private Set<String> skills;
  private Set<Event> eventsHosting;
  private Set<Event> eventsParticipating;
  private Set<Event> eventsVolunteering;

  /**
   * Creates a new user.
   *
   * @param name The human-readable name for the user. Must be non-null.
   * @param email The e-mail with which the user is reachable. Must be non-null.
   */
  public User(String name, String email) {
    this.userId = 10000000L;
    this.name = name;
    this.email = email;
    this.interests = new HashSet<>();
    this.skills = new HashSet<>();
    this.eventsHosting = new HashSet<>();
    this.eventsParticipating = new HashSet<>();
    this.eventsVolunteering = new HashSet<>();
  }

  public long getId() {
    return this.userId;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Set<String> getInterests() {
    return this.interests;
  }

  public void addInterest(String interest) {
    this.interests.add(interest);
  }

  public Set<String> getSkills() {
    return this.skills;
  }

  public void addSkill(String skill) {
    this.skills.add(skill);
  }

  public Set<Event> getEventsHosting() {
    return this.eventsHosting;
  }

  public void addEventHosting(Event event) {
    this.eventsHosting.add(event);
  }

  public Set<Event> getEventsParticipating() {
    return this.eventsParticipating;
  }

  public void addEventParticipating(Event event) {
    this.eventsParticipating.add(event);
  }

  public Set<Event> getEventsVolunteering() {
    return this.eventsVolunteering;
  }

  public void addEventVolunteering(Event event) {
    this.eventsVolunteering.add(event);
  }
}
