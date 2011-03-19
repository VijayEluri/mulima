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
package org.mulima.library;

import java.util.List;

/**
 * A library manager ties all of the copies of your
 * music collection together.  It also provides methods
 * to update the libraries.
 */
public interface LibraryManager {
	/**
	 * Gets a list of all reference libraries in this manager.
	 * @return the reference libraries
	 */
	List<ReferenceLibrary> getRefLibs();
	
	/**
	 * Sets a list of all reference libraries in this manager.
	 * @param refLibs the reference libraries
	 */
	void setRefLibs(List<ReferenceLibrary> refLibs);
	
	/**
	 * Gets a list of all destination libraries in this manager.
	 * These libraries will be updated with changes in the 
	 * reference libraries via the other methods.
	 * @return the destination libraries
	 */
	List<Library> getDestLibs();
	
	/**
	 * Sets a list of all destination libraries in this manager.
	 * These libraries will be updated with changes in the 
	 * reference libraries via other methods.
	 * @param destLibs the destionation libraries
	 */
	void setDestLibs(List<Library> destLibs);
	
	/**
	 * Processes all new albums in the reference libraries.  These
	 * albums will have album.xml files generated for them.
	 */
	void processNew();
	
	/**
	 * Scans all libraries to update their contents.
	 */
	void scanAll();
	
	/**
	 * Updates all destination libraries with the changes in the
	 * reference libraries.
	 */
	void updateAll();
	
	/**
	 * Updates a specific destination library with the changes in the
	 * reference libraries.
	 * @param lib the library to update
	 */
	void updateLib(Library lib);
}
