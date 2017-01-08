/*
 * Copyright 2010-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class FreeDbImport implements Runnable {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private FreeDbDao jdbcDao;
  private FreeDbDao tarDao;

  /** Initializes the app. */
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
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    FreeDbImport driver = new FreeDbImport();
    driver.init();
    driver.run();
  }
}
