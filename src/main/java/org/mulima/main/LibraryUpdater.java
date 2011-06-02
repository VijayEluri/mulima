/*  
 *  Copyright (C) 2011  Andrew Oberstar.  All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mulima.main;

import java.io.IOException;

import org.mulima.api.library.LibraryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Will update libraries by processing new albums and copying the changes.
 */
public class LibraryUpdater {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private LibraryManager manager;
	
	/**
	 * Initializes the app.
	 */
	public void init() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		manager = context.getBean("libManager", LibraryManager.class);
	}
	
	/**
	 * Intializes the app.
	 * @param file the application context file
	 */
	public void init(String file) {
		ApplicationContext context = new FileSystemXmlApplicationContext(file);
		manager = context.getBean("libManager", LibraryManager.class);
	}
	
	public void update() throws IOException {
		logger.info("Beginning update.");
		manager.scanAll();
		manager.processNew();
		manager.updateAll();
		logger.info("Update complete.");
	}
	
	/**
	 * Executes the app.
	 * @param args the arguments
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		LibraryUpdater driver = new LibraryUpdater();
		if (args.length == 0) {
			driver.init();
		} else {
			driver.init(args[0]);
		}
		driver.update();
	}
}
