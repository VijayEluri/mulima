module org.ajoberstar.mulima {
  // java
  requires java.base;
  requires java.net.http;
  requires java.sql; // spring needs for some stupid reason
  requires java.xml;

  // java fx
  requires javafx.controls;
  requires javafx.fxml;

  // spring
  requires spring.context;

  // logging
  requires org.apache.logging.log4j;

  // util
  requires org.apache.commons.lang3;
  requires org.apache.commons.codec;

  opens org.ajoberstar.mulima.init to spring.core, spring.beans, spring.context;
  exports org.ajoberstar.mulima;
}
