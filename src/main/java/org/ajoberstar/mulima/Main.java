package org.ajoberstar.mulima;

import org.ajoberstar.mulima.init.SpringConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class Main {
  private Main() {
    // do not instantiate
  }

  public static void main(String[] args) {
    try (var context = new AnnotationConfigApplicationContext(SpringConfig.class)) {
      // do stuff
    }
  }
}
