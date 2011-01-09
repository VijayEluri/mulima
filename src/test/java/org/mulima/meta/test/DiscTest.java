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

package org.mulima.meta.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mulima.meta.Disc;
import org.mulima.meta.GenericTag;

public class DiscTest {
	@Test
	public void getNum() {
		Disc disc = new Disc();
		disc.add(GenericTag.DISC_NUMBER, Integer.toString(1));
		assertEquals(1, disc.getNum());
	}
}