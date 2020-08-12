package com.google.sps.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class VolunteeringOpportunity {
  private String opportunityId;
  private String eventId;
  private String name;
  private long numSpotsLeft;
  private Set<String> requiredSkills;

  public static class Builder {
    // Required parameters
    private String opportunityId = UUID.randomUUID().toString();
    private String eventId;
    private String name;
    private long numSpotsLeft;

    // Optional parameters
    private Set<String> requiredSkills;

    public Builder(String eventId, String name, long numSpotsLeft) {
      this.eventId = eventId;
      this.name = name;
      this.numSpotsLeft = numSpotsLeft;
      this.requiredSkills = new HashSet<>();
    }

    public Builder setOpportunityId(String opportunityId) {
      this.opportunityId = opportunityId;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setNumSpotsLeft(long numSpotsLeft) {
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

    public VolunteeringOpportunity build() {
      return new VolunteeringOpportunity(this);
    }

    public Builder mergeFrom(VolunteeringOpportunity other) {
      this.opportunityId = other.getOpportunityId();
      this.eventId = other.getEventId();
      this.name = other.getName();
      this.numSpotsLeft = other.getNumSpotsLeft();

      if (!other.getRequiredSkills().isEmpty()) {
        this.requiredSkills = other.getRequiredSkills();
      }

      return this;
    }
  }

  private VolunteeringOpportunity(Builder builder) {
    opportunityId = builder.opportunityId;
    eventId = builder.eventId;
    name = builder.name;
    numSpotsLeft = builder.numSpotsLeft;
    requiredSkills = builder.requiredSkills;
  }

  public String getOpportunityId() {
    return this.opportunityId;
  }

  public String getEventId() {
    return this.eventId;
  }

  public String getName() {
    return this.name;
  }

  public Set<String> getRequiredSkills() {
    return this.requiredSkills;
  }

  public long getNumSpotsLeft() {
    return this.numSpotsLeft;
  }

  public Builder toBuilder() {
    return new Builder(this.eventId, this.name, this.numSpotsLeft).mergeFrom(this);
  }

  @Override
  public String toString() {
    return String.format("Name: %s\nNumber of spots left: %d\n", this.name, this.numSpotsLeft);
  }
}
