package com.google.sps.data;

import java.util.HashSet;
import java.util.Set;

public final class VolunteeringOpportunity {
  private final long opportunityId = 10000000L;
  private String name;
  private int numSpotsLeft;
  private Set<String> requiredSkills;
  private Set<String> volunteers;

  public static class Builder {
    // Required parameters
    private String name;
    private int numSpotsLeft;

    // Optional parameters
    private Set<String> requiredSkills;
    private Set<String> volunteers;

    public Builder(String name, int numSpotsLeft) {
      this.name = name;
      this.numSpotsLeft = numSpotsLeft;
      this.requiredSkills = new HashSet<>();
      this.volunteers = new HashSet<>();
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setNumSpotsLeft(int numSpotsLeft) {
      this.numSpotsLeft = numSpotsLeft;
      return this;
    }

    public Builder setRequiredSkills(Set<String> requiredSkills) {
      this.requiredSkills = requiredSkills;
      return this;
    }

    public Builder addRequiredSkill(String requiredSkill) {
      this.requiredSkills.add(requiredSkill);
      return this;
    }

    public Builder removeRequiredSkill(String requiredSkill) {
      this.requiredSkills.remove(requiredSkill);
      return this;
    }

    public Builder setVolunteers(Set<String> volunteers) {
      this.volunteers = volunteers;
      return this;
    }

    public Builder addVolunteer(String volunteer) {
      this.volunteers.add(volunteer);
      return this;
    }

    public Builder removeVolunteer(String volunteer) {
      this.volunteers.remove(volunteer);
      return this;
    }

    public VolunteeringOpportunity build() {
      return new VolunteeringOpportunity(this);
    }

    public Builder mergeFrom(VolunteeringOpportunity other) {
      this.name = other.getName();
      this.numSpotsLeft = other.getNumSpotsLeft();

      if (!other.getRequiredSkills().isEmpty()) {
        this.requiredSkills = other.getRequiredSkills();
      }
      if (!other.getVolunteers().isEmpty()) {
        this.volunteers = other.getVolunteers();
      }
      return this;
    }
  }

  private VolunteeringOpportunity(Builder builder) {
    name = builder.name;
    numSpotsLeft = builder.numSpotsLeft;
    requiredSkills = builder.requiredSkills;
    volunteers = builder.volunteers;
  }

  public long getOpportunityId() {
    return this.opportunityId;
  }

  public String getName() {
    return this.name;
  }

  public Set<String> getRequiredSkills() {
    return this.requiredSkills;
  }

  public int getNumSpotsLeft() {
    return this.numSpotsLeft;
  }

  public Set<String> getVolunteers() {
    return this.volunteers;
  }

  public Builder toBuilder() {
    return new Builder(this.name, this.numSpotsLeft).mergeFrom(this);
  }

  @Override
  public String toString() {
    return String.format("Name: %s\nNumber of spots left: %d\n", this.name, this.numSpotsLeft);
  }
}