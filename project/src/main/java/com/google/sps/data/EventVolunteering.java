package com.google.sps.data;

public final class EventVolunteering {
  private final Event event;
  private final String opportunityName;

  public EventVolunteering(Event event, String opportunityName) {
    this.event = event;
    this.opportunityName = opportunityName;
  }

  public Event getEvent() {
    return event;
  }

  public String getOpportunityName() {
    return opportunityName;
  }
}