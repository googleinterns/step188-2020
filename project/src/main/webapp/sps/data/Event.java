import java.util.Date;
import java.util.Random; 
import java.util.Set;

/** Class containing Event object 
Setters for variables that user can change about event
*/
public final class Event {
  private final int eventId;
  private String name;
  private String description;
  private Set<String> labels;
  private String location;
  private Date date;
  private Set<VolunteeringOpportunity> opportunities;
  private Set<User> attendees;
  private User host;

  Random rand = new Random(); 

  /** TO DO (MVP): Add Event to Event db*/
  public static class Builder {
  //required params
    this.eventId = rand.nextInt(1000000); //this will later be a unique number based on the database id
    private final int eventId;
    private String name;
    private String description;
    private Set<String> labels;
    private String location;
    private Date date;
    private User host;
    
    //optional params
    private Set<VolunteeringOpportunity> opportunities = new HashSet<>();
    private Set<User> attendees = new HashSet<>();

    public Builder(String name, String description, Set<String> labels, String location, Date date, 
    User host) {
        this.eventId = rand.nextInt(1000000); //this will later be a unique number based on the database id
        this.name = name;
        this.description = description;
        this.labels = labels;
        this.location = location;
        this.date = date;
        this.host = host;
    }

    public Builder opportunities(VolunteeringOpportunity opportunity) {
        opportunities.add(opportunity);
        return this;
    }
    public Builder attendees(User attendee) {
        attendees.add(attendee);
        return this;
    }

    public Event build() {
        return new Event(this);
    }
  }

  private Event(Builder builder) {
  eventId = builder.eventId;
  name = builder.name;
  description = builder.description;
  labels = builder.labels;
  location = builder.location;
  date = builder.date;
  opportunities = builder.opportunities;
  attendees = builder.attendees;
  host = builder.host;
  }

  /** TO DO (MVP) for all getters: get from Event db*/
  /** TO DO (MVP) for all setters: set in Event db*/
  public int getID() {
    return eventId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Set<String> getLabels() {
    return labels;
  }

  public void setLabel(String newLabel) {
    labels.add(newLabel);
  }

  public void removeLabel(String deletedLabel) {
    labels.remove(deletedLabel);
  }

  public String getLocation() {
    return location;
  }

  public Date getDate() {
    return date;
  }

  public Set<VolunteeringOpportunity> getOpportunities() {
    return opportunities;
  }

  public Set<User> getAttendees() {
    return attendees;
  }

  public User getHost() {
    return host;
  }
}