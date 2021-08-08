package io.zrz.psqlwriter;

public enum IsolationLevel {

  SERIALIZABLE(w -> w.writeKeyword(SqlKeyword.SERIALIZABLE)),
  REPEATABLE_READ(w -> w.writeKeyword(SqlKeyword.REPEATABLE, SqlKeyword.READ)),
  READ_COMMITTED(w -> w.writeKeyword(SqlKeyword.READ, SqlKeyword.COMMITED)),
  READ_UNCOMMITTED(w -> w.writeKeyword(SqlKeyword.READ, SqlKeyword.UNCOMMITED)),

  ;

  private final SqlGenerator g;

  IsolationLevel(SqlGenerator g) {
    this.g = g;
  }

  public void writeTo(SqlWriter w) {
    g.write(w);
  }

}
