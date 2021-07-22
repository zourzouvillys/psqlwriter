package io.zrz.sqlwriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

class SqlWritersTest {

  @Test
  void beginReadOnly() {
    assertEquals(
      SqlWriters.beginReadOnly(IsolationLevel.REPEATABLE_READ).asString(),
      "BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ");
  }

  @Test
  void setTransactionSnapshot() {
    assertEquals(
      SqlWriters.setTransactionSnapshot("xyz").asString(),
      "SET TRANSACTION SNAPSHOT 'xyz'");
  }

  @Test
  void jsonBuildObject() {
    assertEquals(
      "pg_catalog.json_build_object('username', a.username, 'password', a.password)",
      SqlWriters.jsonBuildObject(
        ImmutableMap.of(
          SqlWriters.literal("username"),
          DbIdent.of("a", "username"),
          SqlWriters.literal("password"),
          DbIdent.of("a", "password")
        //
        )).asString());

  }

}
