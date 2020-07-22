package com.google.sps.data;

import java.util.Set;

public final class VolunteeringOpportunity {
  private String name;
  private Set<String> skillsRequired;
  private int numSpotsLeft;
  private Set<User> volunteers;

  public VolunteeringOpportunity(
       String name, Set<String> requiredSkills, int numSpotsLeft, Set<User> volunteers) {
    this.name = name;
    this.skillsRequired = skillsRequired;
    this.numSpotsLeft = numSpotsLeft;
    this.volunteers = new HashSet<User>();
  }
  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<String> getRequiredSkills() {
    return this.skillsRequired;
  }

  public void setRequiredSkills(Set<String> requiredSkills) {
    this.skillsRequired = requiredSkills;
  }
    
  public void addRequiredSkill(String requiredSkill) {
    this.skillsRequired.add(requiredSkill);
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