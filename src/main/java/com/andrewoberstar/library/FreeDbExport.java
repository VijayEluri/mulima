package com.andrewoberstar.library;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.andrewoberstar.library.meta.Disc;
import com.andrewoberstar.library.ui.UICallback;
import com.andrewoberstar.library.ui.cli.ChooseDiscCallback;

public class FreeDbExport implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(FreeDbExport.class);
	private LibraryManager manager;
	
	public void init() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		manager = context.getBean("libManager", LibraryManager.class);
	}
	
	@Override
	public void run() {
		UICallback<Disc> chooser = new ChooseDiscCallback();
		manager.getRefLib().findAlbums();
		manager.getRefLib().processNewAlbums(chooser);
	}
	
	public static void main(String[] args) {
		FreeDbExport driver = new FreeDbExport();
		driver.init();
		driver.run();
	}
}
