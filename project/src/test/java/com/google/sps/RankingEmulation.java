// package com.google.sps;

// import com.google.cloud.Date;
// import com.google.sps.data.Event;
// import com.google.sps.data.User;
// import com.google.sps.data.VolunteeringOpportunity;
// import com.google.sps.utilities.SpannerClient;
// import com.google.sps.utilities.SpannerTasks;
// import com.google.sps.utilities.SpannerTestTasks;
// import com.google.sps.utilities.TestUtils;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;
// import javax.servlet.ServletContextEvent;
// import org.junit.After;
// import org.junit.Assert;
// import org.junit.BeforeClass;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.junit.runners.JUnit4;
// import org.springframework.mock.web.MockServletContext;

// /** Tests the ranking algorithm for event discovery */
// @RunWith(JUnit4.class)
// public final class RankingEmulation {
//   private static final String CONSERVATION = "conservation";
//   private static final String FOOD = "food";
//   private static final String MUSIC = "music";
//   private static final String SEWING = "sewing";
//   private static final String CLEANING = "cleaning";
//   private static final Set<String> INTERESTS_CONSERVATION_FOOD =
//       new HashSet<>(Arrays.asList(CONSERVATION, FOOD));
//   private static final Set<String> SKILLS_MUSIC = new HashSet<>(Arrays.asList(MUSIC));
//   private static Event EVENT_CONSERVATION_FOOD_MUSIC;
//   private static Event EVENT_FOOD_MUSIC;
//   private static Event EVENT_CONSERVATION_MUSIC;
//   private static Event EVENT_FOOD;
//   private static Event EVENT_SEWING;
//   private static Event EVENT_CLEANING;
//   private static User USER_CONSERVATION_FOOD_MUSIC;
//   private static User USER_NO_INTERESTS_OR_SKILLS;
//   private static VolunteeringOpportunity OPPORTUNITY_MUSIC;
//   private static VolunteeringOpportunity OPPORTUNITY_RECYCLER;

//   @BeforeClass
//   public static void setUp() throws Exception {
//     // Mock a request to trigger the SpannerClient setup to run
//     MockServletContext mockServletContext = new MockServletContext();
//     new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
//     SpannerTestTasks.setup();

//     int currentYear = new java.util.Date().getYear();
//     EVENT_CONSERVATION_FOOD_MUSIC =
//         TestUtils.newEvent().toBuilder()
//             .setLabels(new HashSet<>(Arrays.asList(CONSERVATION, FOOD, MUSIC)))
//             .setName("Annual town festival - Food, music, and more!")
//             .setDescription("Come celebrate another year in Manhattan! No purchase necessary, just come to enjoy the food, music, games, and prizes, and learn about our new conservation efforts!")
//             .setImageUrl("https://s3.amazonaws.com/psf_blog/blog/wp-content/uploads/2019/09/shutterstock_565464055.jpg")
//             .build();
//     EVENT_FOOD_MUSIC =
//         TestUtils.newEvent().toBuilder()
//             .setLabels(new HashSet<>(Arrays.asList(FOOD, MUSIC)))
//             .setName("Pizza and Piano")
//             .setDescription("A weekly master class open to aspiring piano professionals")
//             .build();
//     EVENT_CONSERVATION_MUSIC =
//         TestUtils.newEvent().toBuilder()
//             .setLabels(new HashSet<>(Arrays.asList(CONSERVATION, MUSIC)))
//             .setName("Plant new trees")
//             .setDescription("Come plant trees with the MHS Garden Club!")
//             .setImageUrl("https://scx1.b-cdn.net/csz/news/800/2019/plantingtree.jpg")
//             .build();
//     EVENT_FOOD =
//         TestUtils.newEvent().toBuilder()
//             .setLabels(new HashSet<>(Arrays.asList(FOOD)))
//             .setName("Weekly meal prep")
//             .setDescription("Join the Cooking Club as they teach you to make a chicken and rice meal.")
//             .setImageUrl("https://purewows3.imgix.net/images/articles/2018_04/meal-prep-honey-sesame-chicken-with-broccolini-recipe-hero.jpg?auto=format,compress&cs=strip")
//             .build();
//     EVENT_SEWING =
//         TestUtils.newEvent().toBuilder()
//             .setLabels(new HashSet<>(Arrays.asList(SEWING)))
//             .setName("Quilting bee")
//             .setDescription("This Quilting bee is open to anyone who is interested in sewing with us. No experience or equipment required.")
//             .setImageUrl("https://www.picturethisgallery.com/wp-content/uploads/The-Quilting-Bee-19th-Century-Americana-Morgan-Weistling.jpg")
//             .build();
//     EVENT_CLEANING =
//         TestUtils.newEvent().toBuilder()
//             .setLabels(new HashSet<>(Arrays.asList(CLEANING)))
//             .setName("Spring cleaning")
//             .setDescription("The public library is holding a spring cleaning event!")
//             .build();
//     USER_CONSERVATION_FOOD_MUSIC =
//         TestUtils.newUser().toBuilder()
//             .setInterests(INTERESTS_CONSERVATION_FOOD)
//             .setSkills(SKILLS_MUSIC)
//             .build();
//     USER_NO_INTERESTS_OR_SKILLS =
//         TestUtils.newUser().toBuilder().setInterests(new HashSet<>()).setSkills(new HashSet<>()).build();
//     OPPORTUNITY_MUSIC =
//         TestUtils.newVolunteeringOpportunityWithEventId(EVENT_FOOD_MUSIC.getId());
//     OPPORTUNITY_RECYCLER =
//         TestUtils.newVolunteeringOpportunityWithEventId(EVENT_CONSERVATION_FOOD_MUSIC.getId());
//     EVENT_FOOD_MUSIC = EVENT_FOOD_MUSIC.toBuilder().addOpportunity(OPPORTUNITY_MUSIC).build();
//     EVENT_CONSERVATION_FOOD_MUSIC = EVENT_CONSERVATION_FOOD_MUSIC.toBuilder().addOpportunity(OPPORTUNITY_RECYCLER).build();
 
//     SpannerTasks.insertOrUpdateUser(USER_CONSERVATION_FOOD_MUSIC);
//     SpannerTasks.insertorUpdateEvent(EVENT_CONSERVATION_FOOD_MUSIC);
//     SpannerTasks.insertorUpdateEvent(EVENT_FOOD_MUSIC);
//     SpannerTasks.insertorUpdateEvent(EVENT_CONSERVATION_MUSIC);
//     SpannerTasks.insertorUpdateEvent(EVENT_FOOD);
//     SpannerTasks.insertorUpdateEvent(EVENT_SEWING);
//     SpannerTasks.insertorUpdateEvent(EVENT_CLEANING);
//   }

//   // @After
//   // public void tearDown() {
//   //   SpannerTestTasks.cleanup();
//   // }

//   @Test
//   public void testThis() {
//     Assert.assertEquals("", "");
//   }
// }
