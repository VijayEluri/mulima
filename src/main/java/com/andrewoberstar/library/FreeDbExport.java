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
