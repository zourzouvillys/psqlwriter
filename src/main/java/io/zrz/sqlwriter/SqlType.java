package io.zrz.sqlwriter;

public interface SqlType {

  DbIdent ident();

  default SqlGenerator literal(String value) {
    return SqlWriters.literal(value, this);
  }

}
