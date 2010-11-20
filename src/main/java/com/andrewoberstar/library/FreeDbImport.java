package com.andrewoberstar.library;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.andrewoberstar.library.meta.Disc;
import com.andrewoberstar.library.meta.dao.FreeDbDao;

public class FreeDbImport implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(FreeDbImport.class);
	private FreeDbDao jdbcDao;
	private FreeDbDao tarDao;
	
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
	
	public static void main(String[] args) {
		FreeDbImport driver = new FreeDbImport();
		driver.init();
		driver.run();
	}
}
