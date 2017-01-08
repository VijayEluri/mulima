import ch.qos.logback.classic.Level
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder

appender('CONSOLE', ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = '%-5level %msg%n'
  }
}

root(Level.valueOf(System.properties['log.level'] ?: 'INFO'), ['CONSOLE'])
logger('org.springframework', Level.WARN)
