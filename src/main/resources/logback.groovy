import static ch.qos.logback.classic.Level.*
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder

appender('CONSOLE', ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		pattern = '%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n'
	}
}

root(INFO, ['CONSOLE'])
logger('org.springframework', WARN)
