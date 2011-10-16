package org.mulima.api.file;

public interface FileParserService {
	<T> FileParser<T> getParser(Class<T> type);
}
