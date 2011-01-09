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
	void write(File file, T meta) throws Exception;
	T read(File file) throws Exception;
	Callable<Void> writeLater(File file, T meta);
	Callable<T> readLater(File file);
}
