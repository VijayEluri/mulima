package org.mulima.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mulima {
  private static final Logger logger = LogManager.getLogger(Mulima.class);

  public static void main(String[] args) {
    try {


    } catch (Throwable e) {
      logger.error("Mulima failed.", e);
      System.exit(1);
    }
  }
}
