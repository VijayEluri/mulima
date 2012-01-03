import ch.qos.logback.classic.Level
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder

appender('CONSOLE', ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		//pattern = '%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n'
		pattern = '%-5level %msg%n'
	}
}

root(Level.valueOf(System.properties['log.level']) ?: Level.INFO, ['CONSOLE'])
logger('org.springframework', Level.WARN)
