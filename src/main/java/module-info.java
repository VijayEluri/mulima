module org.ajoberstar.mulima {
  // java
  requires java.base;
  requires java.net.http;
  requires java.sql; // spring needs for some stupid reason
  requires java.xml;

  // java fx
  requires javafx.controls;
  requires javafx.web;

  // spring
  requires spring.core;
  requires spring.beans;
  requires spring.context;

  // logging
  requires org.apache.logging.log4j;

  // util
  requires org.apache.commons.lang3;
  requires org.apache.commons.codec;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.yaml;

  // exports
  opens org.ajoberstar.mulima.init to spring.core, spring.beans, spring.context;
  opens org.ajoberstar.mulima.meta to javafx.base, org.apache.commons.lang3;
  exports org.ajoberstar.mulima;
}
