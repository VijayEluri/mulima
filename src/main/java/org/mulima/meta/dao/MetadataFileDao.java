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
package org.mulima.meta.dao;

import java.io.File;
import java.util.concurrent.Callable;

import org.mulima.meta.Metadata;

/**
 * Defines operations to read and write metadata to a file.
 */
public interface MetadataFileDao<T extends Metadata> {
	/**
	 * Write a metadata object to a file.
	 * @param file the file to write to
	 * @param meta the metadata to write
	 * @throws Exception if there is a problem writing
	 */
	void write(File file, T meta) throws Exception;
	
	/**
	 * Reads a metadata object from a file.
	 * @param file the file to read from
	 * @return the metadata object parsed from the file
	 * @throws Exception if there is a problem reading
	 */
	T read(File file) throws Exception;
	
	/**
	 * Prepares a callable instance that will write a
	 * metadata object to a file.
	 * @param file the file to write to
	 * @param meta the metadata object to write
	 * @return a callable that will write the metadata to the file
	 */
	Callable<Void> writeLater(File file, T meta);
	
	/**
	 * Prepares a callable instance that will read a
	 * metadata object from a file.
	 * @param file the file to read from
	 * @return a callable that will read metadata from the file
	 */
	Callable<T> readLater(File file);
}
