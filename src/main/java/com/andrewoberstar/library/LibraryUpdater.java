/*  
 *  Copyright (C) 2010  Andrew Oberstar.  All rights reserved.
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

package com.andrewoberstar.library;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.andrewoberstar.library.meta.Disc;
import com.andrewoberstar.library.ui.UICallback;
import com.andrewoberstar.library.ui.cli.ChooseDiscCallback;

public class LibraryUpdater implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(FreeDbExport.class);
	private LibraryManager manager;
	
	public void init() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		manager = context.getBean("libManager", LibraryManager.class);
	}
	
	public void init(String file) {
		ApplicationContext context = new FileSystemXmlApplicationContext(file);
		manager = context.getBean("libManager", LibraryManager.class);
	}
	
	@Override
	public void run() {
		logger.info("Beginning update.");
		manager.getRefLib().findAlbums();
		UICallback<Disc> chooser = new ChooseDiscCallback();
		manager.getRefLib().processNewAlbums(chooser);
		manager.updateLibraries();
		logger.info("Update complete.");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LibraryUpdater driver = new LibraryUpdater();
		if (args.length == 0) {
			driver.init();
		} else {
			driver.init(args[0]);
		}
		driver.run();
	}
}