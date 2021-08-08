package io.zrz.psqlwriter;

import java.time.Duration;

public class SqlUtils {

  public static String toSqlString(Duration d) {
    return d.toString();
  }

}
