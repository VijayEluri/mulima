import static ch.qos.logback.classic.Level.*;
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder

def appenderList = ["CONSOLE", "FILE"]

appender("CONSOLE", ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
	}
}

appender("FILE", FileAppender) {
	file = "C:\\Users\\Andy\\Desktop\\out.txt"
	append = false
	encoder(PatternLayoutEncoder) {
		pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
	}
}

root(INFO, appenderList)
logger('org.springframework', WARN)
logger('com.andrewoberstar.library.meta.dao.impl.FreeDbJdbcDaoImpl', DEBUG)