package com.google.sps;
import com.google.cloud.Date;
import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class EventTest {
    private static final String HOST_NAME = "Bob Smith";
    private static final String EMAIL = "bobsmith@google.com";
    private static final User HOST = new User.Builder(HOST_NAME, EMAIL).build();
    private static final String EVENT_NAME = "Team Meeting";
    private static final String NEW_EVENT_NAME = "Daily Team Meeting";
    private static final String DESCRIPTION = "Daily Team Sync";
    private static final Set <String> LABELS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Tech", "Work")));
    private static final String LOCATION = "Remote";
    private static final Date DATE = Date.fromYearMonthDay(2016, 9, 15);
    private static final String TIME = "3:00PM-5:00PM";
    private static final String VOLUNTEER_OPPORTUNITY_NAME = "Volunteer";
    private static final int NUM_SPOTS_LEFT = 5;
    private static final String EVENT_ID = "0883de79-17d7-49a3-a866-dbd5135062a8";
    private static final VolunteeringOpportunity OPPORTUNITY = new VolunteeringOpportunity.Builder(EVENT_ID, VOLUNTEER_OPPORTUNITY_NAME, NUM_SPOTS_LEFT).build();
    private static final Set <VolunteeringOpportunity> OPPORTUNITIES = new HashSet<VolunteeringOpportunity> (Arrays.asList(OPPORTUNITY));
    private static final User USER1 = new User.Builder("USER1", "USER1@test.com").build();
    private static final User USER2 = new User.Builder("USER2", "USER2@test.com").build();
    private static final User USER3 = new User.Builder("USER3", "USER3@test.com").build();
    private static final Set <User> ATTENDEES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(USER1)));

	/** Create an event and verify Builder class is created with correct required and optional fields */
	@Test
	public void testEventBuild() {
        Event event = new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST)
            .setOpportunities(OPPORTUNITIES).setAttendees(ATTENDEES).build();

        Assert.assertEquals(event.getName(), EVENT_NAME);
        Assert.assertEquals(event.getDescription(), DESCRIPTION);
        Assert.assertEquals(event.getLabels(), LABELS);
        Assert.assertEquals(event.getLocation(), LOCATION);
        Assert.assertEquals(event.getDate(), DATE);
        Assert.assertEquals(event.getTime(), TIME);
        Assert.assertEquals(event.getHost(), HOST);
        Assert.assertEquals(event.getOpportunities(), OPPORTUNITIES);
        Assert.assertEquals(event.getAttendees(), ATTENDEES);
	}
    
	/** Edit an event and verify Event mergeFrom setter sets required and optional field */
    @Test
	public void setEventFields() {
        Event event = new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).setAttendees(ATTENDEES).build();
        Set <User> user2Attendees = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(USER2)));
        event = event.toBuilder().setName(NEW_EVENT_NAME).setAttendees(user2Attendees).build();

        Assert.assertEquals(event.getAttendees(), user2Attendees);
        Assert.assertEquals(event.getName(), NEW_EVENT_NAME);
    }

	/** Add attendees and verify event adding attendees */
    @Test
	public void addAttendeeField() {
        Event event = new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).setAttendees(
            new HashSet<User> (Arrays.asList(USER1, USER2))).build();
        event = event.toBuilder().addAttendee(USER3).build();

        Assert.assertEquals(event.getAttendees(), new HashSet<User> (Arrays.asList(USER1, USER2, USER3)));
    }
}