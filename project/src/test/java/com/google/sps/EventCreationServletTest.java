// package com.google.sps;
// import com.google.cloud.Date;
// import com.google.sps.data.Event;
// import com.google.sps.data.User;
// import com.google.sps.data.VolunteeringOpportunity;
// import com.google.sps.utilities.SpannerClient;
// import com.google.sps.utilities.SpannerTasks;
// import com.google.sps.utilities.SpannerTestTasks;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;
// import java.util.stream.Collectors;
// import java.util.stream.Stream; 
// import javax.servlet.ServletContextEvent;
// import org.junit.AfterClass;
// import org.junit.Assert;
// import org.junit.BeforeClass;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.junit.runners.JUnit4;
// import org.springframework.mock.web.MockServletContext;
// import com.google.sps.servlets.EventCreationServlet;
// import javax.servlet.annotation.WebServlet;
// import javax.servlet.http.HttpServlet;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import java.io.IOException;

// import javax.servlet.ServletException;
// import javax.servlet.http.HttpServlet;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

// import org.junit.Before;
// import org.junit.Test;
// import org.springframework.mock.web.MockHttpServletRequest;
// import org.springframework.mock.web.MockHttpServletResponse;

// public class EventCreationServletTest{
//   private static final String HOST_NAME = "Bob Smith";
//   private static final String EMAIL = "bobsmith@example.com";
//   private static final User HOST = new User.Builder(HOST_NAME, EMAIL).build();
//   private static final String EVENT_NAME = "Team Meeting";
//   private static final String NEW_EVENT_NAME = "Daily Team Meeting";
//   private static final String DESCRIPTION = "Daily Team Sync";
//   private static final Set<String> LABELS =
//         Collections.unmodifiableSet(
//             new HashSet<>(
//                 Arrays.asList("None")));
//   private static final String LOCATION = "Remote";
//   private static final Date DATE = Date.fromYearMonthDay(2016, 9, 15);
//   private static final String DATE_STRING = "09/15/2016";
//   private static final String TIME = "3:00PM-5:00PM";

//     // private EventCreationServlet servlet;
//     // private MockHttpServletRequest request;
//     // private MockHttpServletResponse response;

//   @BeforeClass
//   public static void setUp() throws Exception {
//     // Mock a request to trigger the SpannerClient setup to run
//     MockServletContext mockServletContext = new MockServletContext();
//     new SpannerClient().contextInitialized(new ServletContextEvent(mockServletContext));
//     SpannerTestTasks.setup();
//         // servlet = new EventCreationServlet();
//         // request = new MockHttpServletRequest();
//         // response = new MockHttpServletResponse();
//   }

//   @AfterClass
//   public static void tearDown() throws Exception {
//     SpannerTestTasks.cleanup();
//   }

//     @Test
//     public void testEventCreationDoPost() throws Exception {
//         MockHttpServletRequest request = new MockHttpServletRequest();
//         MockHttpServletResponse response = new MockHttpServletResponse();
//          request.addParameter("name", EVENT_NAME);
//          request.addParameter("date", DATE_STRING);
//          request.addParameter("time", TIME);
//          request.addParameter("description", DESCRIPTION);
//          request.addParameter("location", LOCATION);

            
//         new EventCreationServlet().doPost(request, response);
//         System.out.println("RESPONSE IS");
//         //String eventId = (response);
//         //System.out.println(eventId);
//                 System.out.println(response.getRedirectedUrl() );
//     Event event =
//         new Event.Builder(EVENT_NAME, DESCRIPTION, LABELS, LOCATION, DATE, TIME, HOST).build();
//         System.out.println(event.getId());
//         // Event dbEvent = SpannerTasks.getEventById(eventId).get();


//        Assert.assertEquals(response.getRedirectedUrl(), "/event-details.html?eventId=" + event.getId());
//     }
// }


