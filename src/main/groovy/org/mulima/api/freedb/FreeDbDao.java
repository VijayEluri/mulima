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
package org.mulima.api.freedb;

import java.util.List;

import org.mulima.api.meta.Disc;

/**
 * Defines operations to access FreeDB information from a source.
 *
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public interface FreeDbDao {
  /**
   * Gets a list of discs by their CDDB ID.
   *
   * @param cddbId the ID to search for
   * @return list of discs with the specified CDDB ID
   */
  List<Disc> getDiscsById(String cddbId);

  /**
   * Gets a list of discs by their CDDB ID.
   *
   * @param cddbIds list of IDs to search for
   * @return list of discs with any of the specified CDDB IDs
   */
  List<Disc> getDiscsById(List<String> cddbIds);

  /**
   * Gets all discs from the source.
   *
   * @return list of all discs in the source
   */
  List<Disc> getAllDiscs();

  /**
   * Gets <code>numToRead</code> discs from the source starting with <code>startNum</code>.
   *
   * @param startNum number of the disc to start with
   * @param numToRead the number of discs to read
   * @return list of discs from the source
   */
  List<Disc> getAllDiscsFromOffset(int startNum, int numToRead);

  /**
   * Adds a disc to the source.
   *
   * @param disc the disc to add
   */
  void addDisc(Disc disc);

  /**
   * Adds discs to the source.
   *
   * @param discs the discs to add
   */
  void addAllDiscs(List<Disc> discs);
}
