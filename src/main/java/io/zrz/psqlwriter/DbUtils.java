package io.zrz.psqlwriter;

import java.sql.SQLException;
import java.util.Objects;

import com.google.common.base.CharMatcher;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

public class DbUtils {

  private static final CharMatcher IDENT_CHARS = CharMatcher.inRange('a', 'z').or(CharMatcher.is('_')).or(CharMatcher.digit());

  /**
   * returns an escaped string literal that can be included in an SQL query. The value will not
   * contain the leading or trailing "'" .
   */

  public static String escape(final String strval) {
    try {
      return PsqlUtils.escapeLiteral(null, Objects.requireNonNull(strval), true).toString();
    }
    catch (final SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public static String ident(final String ident) {
    if (DbUtils.IDENT_CHARS.matchesAllOf(ident) && !SqlKeyword.isKeyword(ident)) {
      return ident;
    }
    try {
      return PsqlUtils.escapeIdentifier(null, ident).toString();
    }
    catch (final SQLException e) {
      throw new RuntimeException(e);
    }
  }

  //

  public static Range<Integer> parseIntRange(final String content) {

    if (content.equals("empty")) {
      return Range.all();
    }

    final BoundType lowerBoundType;
    if (content.startsWith("(")) {
      lowerBoundType = BoundType.OPEN;
    }
    else if (content.startsWith("[")) {
      lowerBoundType = BoundType.CLOSED;
    }
    else {
      throw new IllegalArgumentException(String.format("invalid lower bound type '%s'", content));
    }

    final BoundType upperBoundType;
    if (content.endsWith(")")) {
      upperBoundType = BoundType.OPEN;
    }
    else if (content.endsWith("]")) {
      upperBoundType = BoundType.CLOSED;
    }
    else {
      throw new IllegalArgumentException("invalid upper bound type");
    }

    final var inner = content.substring(1, content.length() - 1);

    inner.split(",");

    final var idx = inner.indexOf(',');

    if (idx == -1) {
      throw new IllegalArgumentException(String.format("invalid range: '%s' %d", inner));
    }

    final var lower = inner.substring(0, idx);
    final var upper = inner.substring(idx + 1);

    if (lower.isEmpty()) {

      if (upper.isEmpty()) {
        return Range.all();
      }

      return Range.upTo(Integer.parseInt(upper), upperBoundType);

    }
    if (upper.isEmpty()) {

      return Range.downTo(Integer.parseInt(lower), lowerBoundType);

    }

    return Range.range(
      Integer.parseInt(lower),
      lowerBoundType,
      Integer.parseInt(upper),
      upperBoundType);
  }

  public static String toExclusivity(final Range<?> range) {
    return DbUtils.lowerBoundString(range) + DbUtils.upperBoundString(range);
  }

  public static String lowerBoundString(final Range<?> range) {
    if (!range.hasLowerBound()) {
      return "(";
    }
    switch (range.lowerBoundType()) {
      case CLOSED:
        return "[";
      case OPEN:
        return "(";
    }
    throw new AssertionError();
  }

  public static String upperBoundString(final Range<?> upper) {
    if (!upper.hasUpperBound()) {
      return ")";
    }
    switch (upper.upperBoundType()) {
      case CLOSED:
        return "]";
      case OPEN:
        return ")";
    }
    throw new AssertionError();
  }

  public static String rangeToString(final Range<Comparable<? extends Number>> range) {

    if (range == null) {
      return "(,)";
    }

    final var sb = new StringBuilder();

    sb.append(DbUtils.lowerBoundString(range));

    if (range.hasLowerBound()) {
      sb.append(range.lowerEndpoint());
    }

    sb.append(",");

    if (range.hasUpperBound()) {
      sb.append(range.upperEndpoint());
    }

    sb.append(DbUtils.upperBoundString(range));

    return sb.toString();

  }

  public static String toInt4Range(final Range<Comparable<? extends Number>> range) {

    final var sb = new StringBuilder();

    sb.append(DbUtils.lowerBoundString(range));

    if (range.hasLowerBound()) {
      sb.append(range.lowerEndpoint());
    }

    sb.append(",");

    if (range.hasUpperBound()) {
      sb.append(range.upperEndpoint());
    }

    sb.append(DbUtils.upperBoundString(range));

    return sb.toString();

  }

  public static boolean isClosedOpen(final Range<?> range) {
    return range.hasLowerBound() && range.hasUpperBound() && (range.lowerBoundType() == BoundType.CLOSED) && (range.upperBoundType() == BoundType.OPEN);
  }

}
