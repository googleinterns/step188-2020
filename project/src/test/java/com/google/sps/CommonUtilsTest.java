package com.google.sps;

import com.google.sps.data.User;
import com.google.sps.data.VolunteeringOpportunity;
import com.google.sps.utilities.CommonUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArray;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** */
@RunWith(JUnit4.class)
public final class CommonUtilsTest {
  @Test
  public void emptyStringToEmptyJson() {
    // Given an empty String, verify that an empty JSON object is returned
    Assert.assertEquals(wrapInQuotes(""), CommonUtils.convertToJson(""));
  }

  @Test
  public void volunteeringOpportunityToJson() {
    // Given a VolunteeringOpportunity, verify that all of its fields are properly converted to JSON
    String name = "Meal Prep Workshop";
    int numSpotsLeft = 40;
    String requiredSkill = "Cooking";
    User volunteer = new User.Builder("Bob Smith", "bobsmith@example.com").build();
    VolunteeringOpportunity opportunity =
        new VolunteeringOpportunity.Builder(name, numSpotsLeft)
            .setRequiredSkills(new HashSet<>(Arrays.asList(requiredSkill)))
            .setVolunteers(new HashSet<>(Arrays.asList(volunteer)))
            .build();
    String expectedJson =
        String.format(
            "{%s:%s,%s:%s,%s:%d,%s:%s,%s:[{%s:%s,%s:%s,%s:%s,%s:%s,%s:%s,%s:%s,%s:%s}]}",
            wrapInQuotes("opportunityId"),
            wrapInQuotes(opportunity.getOpportunityId()),
            wrapInQuotes("name"),
            wrapInQuotes(name),
            wrapInQuotes("numSpotsLeft"),
            numSpotsLeft,
            wrapInQuotes("requiredSkills"),
            new HashSet<String>(Arrays.asList(wrapInQuotes(requiredSkill))),
            wrapInQuotes("volunteers"),
            wrapInQuotes("name"),
            wrapInQuotes(volunteer.getName()),
            wrapInQuotes("email"),
            wrapInQuotes(volunteer.getEmail()),
            wrapInQuotes("interests"),
            volunteer.getInterests(),
            wrapInQuotes("skills"),
            volunteer.getSkills(),
            wrapInQuotes("eventsHosting"),
            volunteer.getEventsHosting(),
            wrapInQuotes("eventsParticipating"),
            volunteer.getEventsParticipating(),
            wrapInQuotes("eventsVolunteering"),
            volunteer.getEventsVolunteering());
    Assert.assertEquals(expectedJson, CommonUtils.convertToJson(opportunity));
  }

  @Test
  public void emptySetToEmptyJsonArray() {
    // Given an empty Set of Strings, verify that an empty JSON Array is returned
    Set<String> emptySet = new HashSet<>();
    Assert.assertTrue(CommonUtils.createJsonArray(emptySet).isEmpty());
  }

  @Test
  public void populatedSetToPopulatedJsonArray() {
    // Given a populated Set of Strings, verify that the correct JSON Array is returned
    String firstItem = "item one";
    String secondItem = "item two";
    Set<String> elements = new HashSet<>(Arrays.asList(firstItem, secondItem));
    JsonArray expectedElements = Json.createArrayBuilder().add(secondItem).add(firstItem).build();
    Assert.assertTrue(CommonUtils.createJsonArray(elements).containsAll(expectedElements));
  }

  private static String wrapInQuotes(String s) {
    return "\"" + s + "\"";
  }
}
