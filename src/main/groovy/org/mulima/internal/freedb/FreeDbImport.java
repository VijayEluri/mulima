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
package org.mulima.internal.freedb;

import java.util.List;

import org.mulima.api.freedb.FreeDbDao;
import org.mulima.api.meta.Disc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Imports FreeDB information into database from TAR.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class FreeDbImport implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private FreeDbDao jdbcDao;
	private FreeDbDao tarDao;
	
	/**
	 * Initializes the app.
	 */
	public void init() {
		AbstractApplicationContext context = new ClassPathXmlApplicationContext("importContext.xml");
		context.registerShutdownHook();
		jdbcDao = context.getBean("jdbcDao", FreeDbDao.class);
		tarDao = context.getBean("tarDao", FreeDbDao.class);
	}
	
	@Override
	public void run() {
		int startNum = 0;
		int numToRead = 5000;
		int totalRead = 0;
		
		List<Disc> discs = tarDao.getAllDiscsFromOffset(startNum, numToRead);
		while (discs.size() > 0) {
			startNum += numToRead;
			jdbcDao.addAllDiscs(discs);
			totalRead += discs.size();
			logger.info("Total discs imported: " + totalRead);
			discs = tarDao.getAllDiscsFromOffset(startNum, numToRead);
		}
	}
	
	/**
	 * Executes the app.
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		FreeDbImport driver = new FreeDbImport();
		driver.init();
		driver.run();
	}
}
