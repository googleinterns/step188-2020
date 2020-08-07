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

    public OpportunitySignup build() {
      return new OpportunitySignup(this);
    }

    public Builder mergeFrom(OpportunitySignup other) {
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
    return this.email;
  }

  public Builder toBuilder() {
    return new Builder(this.opportunityId, this.email).mergeFrom(this);
  }

  @Override
  public String toString() {
    return String.format("opportunityId: %s\nemail: %d\n", this.opportunityId, this.email);
  }
}
