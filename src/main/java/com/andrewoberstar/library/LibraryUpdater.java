package com.andrewoberstar.library;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LibraryUpdater implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(FreeDbExport.class);
	private LibraryManager manager;
	
	public void init() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		manager = context.getBean("libManager", LibraryManager.class);
	}
	
	@Override
	public void run() {
		manager.getRefLib().findAlbums();
		manager.updateLibraries();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LibraryUpdater driver = new LibraryUpdater();
		driver.init();
		driver.run();
	}
}
