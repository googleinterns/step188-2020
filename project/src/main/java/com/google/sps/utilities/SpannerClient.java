/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.sps.utilities;

import com.google.cloud.spanner.DatabaseAdminClient;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class SpannerClient implements ServletContextListener {
  private static String INSTANCE_ID;
  private static Spanner spanner = null;
  private static DatabaseAdminClient databaseAdminClient = null;
  private static DatabaseClient databaseClient = null;
  private static ServletContext sc;

  public static void connect() throws IOException {
    if (INSTANCE_ID == null) {
      if (sc != null) {
        sc.log("environment variable SPANNER_INSTANCE need to be defined.");
      }
      return;
    }
    SpannerOptions options =
        SpannerOptions.newBuilder().setProjectId(DatabaseConstants.PROJECT_ID).build();
    spanner = options.getService();
    databaseAdminClient = spanner.getDatabaseAdminClient();
  }

  static DatabaseAdminClient getDatabaseAdminClient() {
    if (databaseAdminClient == null) {
      try {
        connect();
      } catch (IOException e) {
        if (sc != null) {
          System.out.println("getDatabaseAdminClient ");
        }
      }
    }
    if (databaseAdminClient == null) {
      if (sc != null) {
        System.out.println("Spanner : Unable to connect");
      }
    }
    return databaseAdminClient;
  }

  static DatabaseClient getDatabaseClient() {
    if (databaseClient == null) {
      databaseClient =
          spanner.getDatabaseClient(
              DatabaseId.of(
                  DatabaseConstants.PROJECT_ID, INSTANCE_ID, DatabaseConstants.DATABASE_ID));
    }
    return databaseClient;
  }

  @Override
  public void contextInitialized(ServletContextEvent event) {
    String envInstanceId = System.getenv("SPANNER_INSTANCE");
    INSTANCE_ID = (envInstanceId == null) ? DatabaseConstants.INSTANCE_ID : envInstanceId;

    try {
      connect();
    } catch (IOException e) {
      if (sc != null) {
        sc.log("SpannerConnection - connect ", e);
      }
    }
    if (databaseAdminClient == null) {
      if (sc != null) {
        sc.log("SpannerConnection - No Connection");
      }
    }
    if (sc != null) {
      sc.log("ctx Initialized: " + INSTANCE_ID + " " + DatabaseConstants.DATABASE_ID);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    // App Engine does not currently invoke this method but override is mandatory.
    databaseAdminClient = null;
  }

  static String getInstanceId() {
    return INSTANCE_ID;
  }

  static String getDatabaseId() {
    return DatabaseConstants.DATABASE_ID;
  }
}
