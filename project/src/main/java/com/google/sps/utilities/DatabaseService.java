package com.google.sps.utilities;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.Spanner;

public interface DatabaseService {
  Spanner getSpanner();
  DatabaseClient getDatabaseClient();
}
