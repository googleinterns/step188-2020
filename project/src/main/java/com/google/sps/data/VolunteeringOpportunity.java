package com.google.sps.data;

import java.util.HashSet;
import java.util.Set;

public final class VolunteeringOpportunity {
  private String name;
  private Set<String> requiredSkills;
  private int numSpotsLeft;
  private Set<User> volunteers = new HashSet<>();

  public VolunteeringOpportunity(String name, Set<String> requiredSkills, int numSpotsLeft) {
    this.name = name;
    this.numSpotsLeft = numSpotsLeft;
    this.requiredSkills = requiredSkills;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<String> getRequiredSkills() {
    return this.requiredSkills;
  }

  public void setRequiredSkills(Set<String> requiredSkills) {
    this.requiredSkills = requiredSkills;
  }

  public void removeRequiredSkill(String requiredSkill) {
      this.requiredSkills.remove(requiredSkill);
  }

  public void addRequiredSkill(String requiredSkill) {
    this.requiredSkills.add(requiredSkill);
  }

  public int getNumSpotsLeft() {
    return this.numSpotsLeft;
  }

  public void setNumSpotsLeft(int numSpotsLeft) {
    this.numSpotsLeft = numSpotsLeft;
  }

  public Set<User> getVolunteers() {
    return this.volunteers;
  }

  public void addVolunteer(User volunteer) {
    this.volunteers.add(volunteer);
  }
}
