package com.google.sps.data;

/** Class that represents an opportunity an user is volunteering for. */
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
