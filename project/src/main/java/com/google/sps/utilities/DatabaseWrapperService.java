package com.google.sps.utilities;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;

public class DatabaseWrapperService implements DatabaseService {
  public static final String INSTANCE_ID = "step-188-instance";
  public static final String DATABASE_ID = "event-organizer-db";
  private Spanner spanner;
  private DatabaseClient databaseClient;

  public DatabaseWrapperService() { 
    SpannerOptions options = SpannerOptions.newBuilder().build();
    spanner = options.getService();
    DatabaseId database = DatabaseId.of(options.getProjectId(), INSTANCE_ID, DATABASE_ID);
    databaseClient = spanner.getDatabaseClient(database);
  }
  
  @Override
  public Spanner getSpanner() {
    return spanner;
  }
  
  @Override
  public DatabaseClient getDatabaseClient() {
    return databaseClient;
  }
}