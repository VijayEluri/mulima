package org.mulima.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {
//	@Bean public MulimaService service() {
//		DefaultMulimaService service = new DefaultMulimaService();
//		service.setDigestService(digestService());
//		service.setFileService(new DefaultFileService());
//		service.setLibraryService(libraryService());
//		service.setTempDir(new TempDir().newChild(".mulima"));
//		service.setToolService(new DefaultToolService());
//		return service;
//	}
//	
//	@Bean public DigestService digestService() {
//		return new DefaultDigestService();
//	}
//	
//	@Bean public LibraryService libraryService() {
//		digestService();
//	}
//	
//	@Bean public LibraryManager libraryManager() {
//		new DefaultAlbumConversionService(service());
//		libraryService();
//	}
}
