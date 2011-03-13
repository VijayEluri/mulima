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
 * A reference library is a special case of a normal library.
 * It contains the reference copies of your music.  This will
 * often be a lossless format.
 */
public interface ReferenceLibrary extends Library {
	/**
	 * Gets all libraries that do not have album metadata
	 * generated for them yet.
	 * @return list of new albums
	 */
	List<LibraryAlbum> getNew();
}
