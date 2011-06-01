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
package org.mulima.api.library.impl;

import java.util.ArrayList;
import java.util.List;

import org.mulima.api.library.LibraryAlbum;
import org.mulima.api.library.ReferenceLibrary;

/**
 * Basic version of a reference library.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class ReferenceLibraryImpl extends LibraryImpl implements ReferenceLibrary {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LibraryAlbum> getNew() {
		List<LibraryAlbum> newAlbums = new ArrayList<LibraryAlbum>();
		for (LibraryAlbum libAlbum : getAll()) {
			if (libAlbum.getAlbum() == null) {
				newAlbums.add(libAlbum);
			}
		}
		return newAlbums;
	}
}
