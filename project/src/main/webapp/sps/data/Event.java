import java.util.Date;
import java.util.Random; 
import java.util.Set;

/** Class containing Event object 
Setters for variables that user can change about event
*/
public class Event {
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
  public Event(String name, String description, Set<String> labels, String location, Date date, 
  Set<VolunteeringOpportunity> opportunities, Set<User> attendees, User host) {
    this.eventId = rand.nextInt(1000000); //this will later be a unique number based on the database id
    this.name = name;
    this.description = description;
    this.labels = labels;
    this.location = location;
    this.date = date;
    this.opportunities = opportunities;
    this.attendees = attendees;
    this.host = host;
  }

  /** TO DO (MVP) for all getters: get from Event db*/
  /** TO DO (MVP) for all setters: set in Event db*/
  public int getID() {
    return eventId;
  }

  public String getName() {
    return name;
  }

  public void setName(String newName) {
    this.name = newName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String newDescription) {
    this.description = newDescription;
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

  public void setLocation(String newLocation) {
    this.location = newLocation;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date newDate) {
    this.date = newDate;
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