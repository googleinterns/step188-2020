package com.google.sps;

import com.google.sps.data.Event;
import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import java.time.LocalDate;
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
	private static final String DESCRIPTION = "Daily Team Sync";
	private static final Set <String> LABELS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Tech", "Work")));
	private static final String LOCATION = "Remote";
	private static final LocalDate DATE = LocalDate.of(2020, 7, 24);
	private static final String VOLUNTEER_OPPORTUNITY_NAME = "Volunteer";
	private static final int NUM_SPOTS_LEFT = 5;
    private static final VolunteeringOpportunity OPPORTUNITY = new VolunteeringOpportunity.Builder(VOLUNTEER_OPPORTUNITY_NAME, NUM_SPOTS_LEFT).build();
	private static final Set <VolunteeringOpportunity> OPPORTUNITIES = new HashSet<VolunteeringOpportunity> (Arrays.asList(OPPORTUNITY));
	private static final User USER1 = new User.Builder("USER1", "USER1@test.com").build();
	private static final User USER2 = new User.Builder("USER2", "USER2@test.com").build();
	private static final Set <User> ATTENDEES = Collections.unmodifiableSet(new HashSet<User> (Arrays.asList(USER1, USER2)));

	// Verify Builder class is created with correct required and optional fields
	@Test
	public void getEventAfterBuild() {
		Event event = new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, HOST).setOpportunities(OPPORTUNITIES).setAttendees(ATTENDEES).build();
		String actual_name = event.getName();
		String expected_name = EVENT_NAME;
		String actual_description = event.getDescription();
		String expected_description = DESCRIPTION;
        Set<String> actual_labels = event.getLabels();
        Set<String> expected_labels = LABELS;
        String actual_location = event.getLocation();
		String expected_location = LOCATION;
        LocalDate actual_date = event.getDate();
        LocalDate expected_date = DATE;
        User actual_host = event.getHost();
		User expected_host = HOST;
        Set<VolunteeringOpportunity> actual_opportunities = event.getOpportunities();
        Set<VolunteeringOpportunity> expected_opportunities = OPPORTUNITIES;
        Set<User> actual_attendees = event.getAttendees();
        Set<User> expected_attendees = ATTENDEES;

		Assert.assertTrue(actual_name.equals(expected_name) && actual_description.equals(expected_description) && actual_labels.equals(expected_labels) &&
        actual_location.equals(expected_location) && actual_date.equals(expected_date) && actual_host.equals(expected_host));
        Assert.assertTrue(actual_opportunities.equals(expected_opportunities) && actual_attendees.equals(expected_attendees));
	}

	// Verify Event mergeFrom setter sets fields
    @Test
	public void setEventFields() {
        Event event = new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, HOST).build();
        Event.Builder changedEventBuilder = event.toBuilder().setAttendees(ATTENDEES).build().toBuilder();
        changedEventBuilder.mergeFrom(event);
        event = changedEventBuilder.build();

        Set<User> actual_attendees = event.getAttendees();
        Set<User> expected_attendees = ATTENDEES;
        Assert.assertEquals(expected_attendees, actual_attendees);
    }
}