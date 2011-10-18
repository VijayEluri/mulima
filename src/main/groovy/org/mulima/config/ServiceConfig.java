package org.mulima.config;

import org.mulima.api.audio.tool.ToolService;
import org.mulima.api.file.DigestService;
import org.mulima.api.file.TempDir;
import org.mulima.api.job.AlbumConversionService;
import org.mulima.api.library.LibraryManager;
import org.mulima.api.library.LibraryService;
import org.mulima.api.service.MulimaService;
import org.mulima.internal.audio.tool.DefaultToolService;
import org.mulima.internal.file.DefaultDigestService;
import org.mulima.internal.file.DefaultFileService;
import org.mulima.internal.job.DefaultAlbumConversionService;
import org.mulima.internal.service.DefaultMulimaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {
	@Bean public MulimaService service() {
		DefaultMulimaService service = new DefaultMulimaService();
		service.setDigestService(digestService());
		service.setFileService(new DefaultFileService());
		service.setLibraryService(libraryService());
		service.setTempDir(new TempDir().newChild(".mulima"));
		service.setToolService(toolService());
		return service;
	}
	
	@Bean public AlbumConversionService conversionService() {
		return new DefaultAlbumConversionService(service());
	}
	
	@Bean public ToolService toolService() {
		return new DefaultToolService();
	}
	
	@Bean public DigestService digestService() {
		return new DefaultDigestService();
	}
	
	@Bean public LibraryService libraryService() {
		digestService();
	}
	
	@Bean public LibraryManager libraryManager() {
		conversionService();
		libraryService();
	}
}
