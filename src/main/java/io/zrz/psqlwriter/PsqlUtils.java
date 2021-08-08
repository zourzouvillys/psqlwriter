package io.zrz.psqlwriter;

import java.io.IOException;
import java.sql.SQLException;

final class PsqlUtils {

  /**
   * Escape the given literal {@code value} and append it to the string builder {@code sbuf}. If
   * {@code sbuf} is {@code null}, a new StringBuilder will be returned. The argument
   * {@code standardConformingStrings} defines whether the backend expects standard-conforming
   * string literals or allows backslash escape sequences.
   *
   * @param sbuf
   *          the string builder to append to; or {@code null}
   * @param value
   *          the string value
   * @param standardConformingStrings
   *          if standard conforming strings should be used
   * @return the sbuf argument; or a new string builder for sbuf == null
   * @throws SQLException
   *           if the string contains a {@code \0} character
   */
  public static StringBuilder escapeLiteral(
      StringBuilder sbuf,
      final String value,
      final boolean standardConformingStrings)
          throws SQLException {
    if (sbuf == null) {
      sbuf = new StringBuilder(((value.length() + 10) / 10) * 11); // Add 10% for escaping.
    }
    PsqlUtils.doAppendEscapedLiteral(sbuf, value, standardConformingStrings);
    return sbuf;
  }

  /**
   * Common part for {@link #escapeLiteral(StringBuilder, String, boolean)}.
   *
   * @param sbuf
   *          Either StringBuffer or StringBuilder as we do not expect any IOException to be thrown
   * @param value
   *          value to append
   * @param standardConformingStrings
   *          if standard conforming strings should be used
   */
  private static void doAppendEscapedLiteral(
      final Appendable sbuf,
      final String value,
      final boolean standardConformingStrings)
          throws SQLException {
    try {
      if (standardConformingStrings) {
        // With standard_conforming_strings on, escape only single-quotes.
        for (var i = 0; i < value.length(); ++i) {
          final var ch = value.charAt(i);
          if (ch == '\0') {
            throw new SQLException(
              "Zero bytes may not occur in string parameters.",
                "07006"); // PSQLState.INVALID_PARAMETER_VALUE);
          }
          if (ch == '\'') {
            sbuf.append('\'');
          }
          sbuf.append(ch);
        }
      }
      else {
        // With standard_conforming_string off, escape backslashes and
        // single-quotes, but still escape single-quotes by doubling, to
        // avoid a security hazard if the reported value of
        // standard_conforming_strings is incorrect, or an error if
        // backslash_quote is off.
        for (var i = 0; i < value.length(); ++i) {
          final var ch = value.charAt(i);
          if (ch == '\0') {
            throw new SQLException(
              "Zero bytes may not occur in string parameters.",
                "07006"); // PSQLState.INVALID_PARAMETER_VALUE);
          }
          if ((ch == '\\') || (ch == '\'')) {
            sbuf.append(ch);
          }
          sbuf.append(ch);
        }
      }
    }
    catch (final IOException e) {
      throw new SQLException(
        "No IOException expected from StringBuffer or StringBuilder",
        "99999", // PSQLState.UNEXPECTED_ERROR,
        e);
    }
  }

  /**
   * Escape the given identifier {@code value} and append it to the string builder {@code sbuf}. If
   * {@code sbuf} is {@code null}, a new StringBuilder will be returned. This method is different
   * from appendEscapedLiteral in that it includes the quoting required for the identifier while
   * {@link #escapeLiteral(StringBuilder, String, boolean)} does not.
   *
   * @param sbuf
   *          the string builder to append to; or {@code null}
   * @param value
   *          the string value
   * @return the sbuf argument; or a new string builder for sbuf == null
   * @throws SQLException
   *           if the string contains a {@code \0} character
   */
  public static StringBuilder escapeIdentifier(StringBuilder sbuf, final String value)
      throws SQLException {
    if (sbuf == null) {
      sbuf = new StringBuilder(2 + (((value.length() + 10) / 10) * 11)); // Add 10% for escaping.
    }
    PsqlUtils.doAppendEscapedIdentifier(sbuf, value);
    return sbuf;
  }

  /**
   * Common part for appendEscapedIdentifier.
   *
   * @param sbuf
   *          Either StringBuffer or StringBuilder as we do not expect any IOException to be thrown.
   * @param value
   *          value to append
   */
  private static void doAppendEscapedIdentifier(final Appendable sbuf, final String value) throws SQLException {
    try {
      sbuf.append('"');

      for (var i = 0; i < value.length(); ++i) {
        final var ch = value.charAt(i);
        if (ch == '\0') {
          throw new SQLException(
            "Zero bytes may not occur in identifiers.",
              "07006"); // PSQLState.INVALID_PARAMETER_VALUE);
        }
        if (ch == '"') {
          sbuf.append(ch);
        }
        sbuf.append(ch);
      }

      sbuf.append('"');
    }
    catch (final IOException e) {
      throw new SQLException(
        "No IOException expected from StringBuffer or StringBuilder",
        "99999", // PSQLState.UNEXPECTED_ERROR,
        e);
    }
  }
}
