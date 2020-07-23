package com.google.sps.data;

import java.util.Set;

public final class VolunteeringOpportunity {
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
    }

    public Builder requiredSkills(Set<String> requiredSkills) {
      this.requiredSkills = requiredSkills;
      return this;
    }

    public Builder volunteers(Set<String> volunteers) {
      this.volunteers = volunteers;
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
    volunteers = builder.requiredSkills;
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
}
