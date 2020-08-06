package com.google.sps.utilities;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;

/** Class that initializes spanner and database client. */
public class DatabaseServiceImpl implements DatabaseService {
  private final Spanner spanner;
  private final DatabaseClient databaseClient;

  public DatabaseServiceImpl() {
    SpannerOptions options = SpannerOptions.newBuilder().build();
    spanner = options.getService();
    DatabaseId database =
        DatabaseId.of(options.getProjectId(), DatabaseConstants.INSTANCE_ID, DatabaseConstants.DATABASE_ID);
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
