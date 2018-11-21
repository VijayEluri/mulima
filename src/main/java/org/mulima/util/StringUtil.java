package org.mulima.util;

import java.util.Objects;

/** Contains helper methods for <code>String</code> operations. */
public final class StringUtil {
  /**
   * This class should never be instantiated.
   *
   * @throws AssertionError always
   */
  private StringUtil() {
    throw new AssertionError("Cannot instantiate this class.");
  }

  /**
   * Computes the Levenshtein distance between to {@code String}s. This is computed as described here:
   * {@link http://en.wikipedia.org/wiki/Levenshtein_distance}.
   *
   * @param arg0 the first {@code String}
   * @param arg1 the second {@code String}
   * @return the Levenshtein distance between the two {@code String}s
   */
  public static int levenshteinDistance(String arg0, String arg1) {
    if (Objects.equals(arg0, arg1)) {
      return 0;
    } else if (arg0 == null) {
      return arg1.length();
    } else if (arg1 == null) {
      return arg0.length();
    }
    int[][] dist = new int[arg0.length() + 1][arg1.length() + 1];

    for (int i = 0; i < dist.length; i++) {
      dist[i] = new int[arg1.length() + 1];
      dist[i][0] = i;
    }

    for (int j = 0; j < dist[0].length; j++) {
      dist[0][j] = j;
    }

    for (int j = 1; j < dist[0].length; j++) {
      for (int i = 1; i < dist.length; i++) {
        if (arg0.charAt(i - 1) == arg1.charAt(j - 1)) {
          dist[i][j] = dist[i - 1][j - 1];
        } else {
          int deletion = dist[i - 1][j] + 1;
          int insertion = dist[i][j - 1] + 1;
          int substitution = dist[i - 1][j - 1] + 1;

          if (deletion < insertion) {
            if (deletion < substitution) {
              dist[i][j] = deletion;
            } else {
              dist[i][j] = substitution;
            }
          } else {
            if (insertion < substitution) {
              dist[i][j] = insertion;
            } else {
              dist[i][j] = substitution;
            }
          }
        }
      }
    }

    return dist[arg0.length()][arg1.length()];
  }

  /**
   * Makes a {@code String} safe for use in a file system path. The following characters will be
   * replaced with underscores:
   *
   * <ul>
   * <li>\
   * <li>/
   * <li>:
   * <li>*
   * <li>?
   * <li>"
   * <li>{@literal <}
   * <li>{@literal >}
   * <li>|
   * </ul>
   *
   * @param arg0 the {@code String} to make safe
   * @return the {@code String} with offending characters replaced with underscores
   */
  public static String makeSafe(String arg0) {
    String woIllegals = arg0.trim().replaceAll("[\\\\/:\\*\\?\"<>\\|]+", "_");
    return woIllegals.replaceAll("^\\.+|\\.+$", "");
  }

  /**
   * Converts a {@code String} to camel case. The {@code String} must be in all uppercase letters,
   * with underscores separating words.
   *
   * @param value the {@code String} to convert
   * @return the camel case version of {@code value}
   */
  public static String toCamelCase(String value) {
    StringBuilder builder = new StringBuilder();
    boolean underscorePrev = false;

    for (char ch : value.toCharArray()) {
      if (ch == '_') {
        underscorePrev = true;
      } else {
        builder.append(underscorePrev ? ch : Character.toLowerCase(ch));
        underscorePrev = false;
      }
    }
    return builder.toString();
  }

  /**
   * Converts a camel case {@code String} to uppercase. The {@code String} must be in all lowercase,
   * with uppercase first letters in each word after the first.
   *
   * @param value the camel case {@code String} to convert
   * @return the uppercase version of {@code value}
   */
  public static String fromCamelCase(String value) {
    StringBuilder builder = new StringBuilder();

    for (char ch : value.toCharArray()) {
      if (Character.isUpperCase(ch)) {
        builder.append("_" + ch);
      } else {
        builder.append(Character.toUpperCase(ch));
      }
    }
    return builder.toString();
  }

  /**
   * Returns the common portion of the two {@code String}s.
   *
   * @param arg0 the first string
   * @param arg1 the second string
   * @return a substring (beginning at index 0) that is common to both strings
   */
  public static String commonString(String arg0, String arg1) {
    for (int i = 0; i < arg0.length(); i++) {
      if (arg0.charAt(i) != arg1.charAt(i)) {
        return arg0.substring(0, i);
      }
    }
    return "";
  }
}
