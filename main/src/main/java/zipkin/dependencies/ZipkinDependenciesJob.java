/**
 * Copyright 2016 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin.dependencies;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import zipkin.dependencies.cassandra.CassandraDependenciesJob;
import zipkin.dependencies.elasticsearch.ElasticsearchDependenciesJob;
import zipkin.dependencies.mysql.MySQLDependenciesJob;

public final class ZipkinDependenciesJob {

  /** Runs with defaults, starting today */
  public static void main(String[] args) {
    long day = args.length == 1 ? parseDay(args[0]) : System.currentTimeMillis();
    String storageType = System.getenv("STORAGE_TYPE");
    switch (storageType) {
      case "cassandra":
        CassandraDependenciesJob.builder().day(day).build().run();
        break;
      case "mysql":
        MySQLDependenciesJob.builder().day(day).build().run();
        break;
      case "elasticsearch":
        ElasticsearchDependenciesJob.builder().day(day).build().run();
        break;
      default:
        throw new UnsupportedOperationException("Unsupported STORAGE_TYPE: " + storageType);
    }
  }

  static long parseDay(String formattedDate) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    df.setTimeZone(TimeZone.getTimeZone("UTC"));
    try {
      return df.parse(formattedDate).getTime();
    } catch (ParseException e) {
      throw new IllegalArgumentException(
          "First argument must be a yyy-MM-dd formatted date. Ex. 2016-07-16");
    }
  }
}