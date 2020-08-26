/*
package com.google.sps;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.cloud.Date;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.sps.data.Event;
import com.google.sps.data.EventResult;
import com.google.sps.data.Keyword;
import com.google.sps.data.User;
import com.google.sps.servlets.EventCreationServlet;
import com.google.sps.servlets.SearchDataServlet;
import com.google.sps.store.SearchStore;
import com.google.sps.utilities.CommonUtils;
import com.google.sps.utilities.KeywordHelper;
import com.google.sps.utilities.NlpProcessing;
import com.google.sps.utilities.SpannerClient;
import com.google.sps.utilities.SpannerTasks;
import com.google.sps.utilities.SpannerTestTasks;
import com.google.sps.utilities.TestUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockServletContext;


  private static final ImmutableList<Keyword> KEYWORDS_DESCRIPTION_WITHOUT_GAMES =
      ImmutableList.of(
          new Keyword("Sutter Middle School", 0.43f),
          new Keyword("McKinley Park", 0.14f),
          new Keyword("teachers", 0.10f),
          new Keyword("class", 0.10f),
          new Keyword("picnic", 0.09f),
          new Keyword("park", 0.08f),
          new Keyword("lunch", 0.03f),
          new Keyword("Clunie Pool", 0.03f));
  private static final ImmutableList<Keyword> KEYWORDS_DESCRIPTION_WITH_GAMES_IN_LOW_RELEVANCE =
    ImmutableList.of(
      ImmutableList.of(
          new Keyword("Sutter Middle School", 0.41f),
          new Keyword("McKinley Park", 0.13f),
          new Keyword("teachers", 0.09f),
          new Keyword("class", 0.09f),
          new Keyword("picnic", 0.09f),
          new Keyword("park", 0.08f),
          new Keyword("lunch", 0.07f),
          new Keyword(GAMES, 0.01f),
          new Keyword("Clunie Pool", 0.03f));
  private static final String DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE =
      "Community harvest festival with games, food, and candy. Event open to the public 5pm-9pm."
          + "Complete full closure for 700 attendees.";
  private static final ImmutableList<Keyword> KEYWORDS_DESCRIPTION_WITH_GAMES_IN_HIGH_RELEVANCE =
      ImmutableList.of(
          new Keyword("Community Harvest festival", 0.40f),
          new Keyword(GAMES, 0.17f),
          new Keyword("food", 0.17f),
          new Keyword("candy", 0.12f),
          new Keyword("Event", 0.06f),
          new Keyword("closure", 0.04f),
          new Keyword("attendees", 0.03f));
*/