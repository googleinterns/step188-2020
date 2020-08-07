package com.google.sps.data;

public final class OpportunitySignup {
  private String opportunityId;
  private String email;

  public static class Builder {
    // Required parameters
    private String opportunityId;
    private String email;

    public Builder(String opportunityId, String email) {
      this.opportunityId = opportunityId;
      this.email = email;
    }

    public Builder setOpportunityId(String opportunityId) {
      this.opportunityId = opportunityId;
      return this;
    }

    public Builder setEmail(String email) {
      this.email = email;
      return this;
    }

    public VolunteeringOpportunity build() {
      return new VolunteeringOpportunity(this);
    }

    public Builder mergeFrom(VolunteeringOpportunity other) {
      this.opportunityId = other.getOpportunityId();
      this.email = other.getEmail();
      return this;
    }
  }

  private OpportunitySignup(Builder builder) {
    opportunityId = builder.opportunityId;
    email = builder.email;
  }

  public String getOpportunityId() {
    return this.opportunityId;
  }

  public String getEmail() {
    return this.name;
  }

  public Builder toBuilder() {
    return new Builder(this.opportunityId, this.email).mergeFrom(this);
  }

  @Override
  public String toString() {
    return String.format("Name: %s\nEmail: %d\n", this.name, this.email);
  }
}
