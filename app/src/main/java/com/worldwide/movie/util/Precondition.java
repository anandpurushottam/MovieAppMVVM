package com.worldwide.movie.util;

/**
 * Created by Anand on 18-03-2018.
 */

public class Precondition {
  public static <T> T checkNotNull(T reference) {
    if (reference == null) {
      throw new NullPointerException();
    }
    return reference;
  }
}
