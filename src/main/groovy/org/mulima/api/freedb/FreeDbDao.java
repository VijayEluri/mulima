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
package org.mulima.api.freedb;

import java.util.List;

import org.mulima.api.meta.Disc;


/**
 * Defines operations to access FreeDB information
 * from a source.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface FreeDbDao {
	/**
	 * Gets a list of discs by their CDDB ID.
	 * @param cddbId the ID to search for
	 * @return list of discs with the specified CDDB ID
	 */
	List<Disc> getDiscsById(String cddbId);
	
	/**
	 * Gets a list of discs by their CDDB ID.
	 * @param cddbIds list of IDs to search for
	 * @return list of discs with any of the specified CDDB IDs
	 */
	List<Disc> getDiscsById(List<String> cddbIds);
	
	/**
	 * Gets all discs from the source.
	 * @return list of all discs in the source
	 */
	List<Disc> getAllDiscs();
	
	/**
	 * Gets <code>numToRead</code> discs from the source starting
	 * with <code>startNum</code>. 
	 * @param startNum number of the disc to start with
	 * @param numToRead the number of discs to read
	 * @return list of discs from the source
	 */
	List<Disc> getAllDiscsFromOffset(int startNum, int numToRead);
	
	/**
	 * Adds a disc to the source.
	 * @param disc the disc to add
	 */
	void addDisc(Disc disc);
	
	/**
	 * Adds discs to the source.
	 * @param discs the discs to add
	 */
	void addAllDiscs(List<Disc> discs);
}
