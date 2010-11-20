/*  
 *  Copyright (C) 2010  Andrew Oberstar.  All rights reserved.
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

package com.andrewoberstar.library.util.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.Test;

import com.andrewoberstar.library.util.StreamDumper;

public class StreamDumperTest {
	@Test
	public void call() throws IOException {
		PipedOutputStream os = new PipedOutputStream();
		InputStream is = new PipedInputStream(os);
		
		os.write("test\n".getBytes());
		os.write("heythisisworking\n".getBytes());
		os.write("yeahright\n".getBytes());
		os.close();
		
		StreamDumper dumper = new StreamDumper(is);
		assertEquals("test\nheythisisworking\nyeahright\n", dumper.call());
		is.close();
	}
}
