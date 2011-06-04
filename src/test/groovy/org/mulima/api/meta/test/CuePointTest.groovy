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
package org.mulima.api.meta.test

import org.junit.Test
import org.mulima.api.meta.CuePoint

class CuePointTest {
	@Test
	void construct_ValidTime_Success() {
		assert new CuePoint(1, 1, '23:12:12')
	}
	
//	@Test(expected=IllegalArgumentException.class)
//	void construct_InvalidTime_Throw() {
//		new CuePoint(1, 1, '12:99:12')
//	}
}
