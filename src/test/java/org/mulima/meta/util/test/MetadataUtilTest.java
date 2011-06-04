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
package org.mulima.meta.util.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mulima.api.meta.Disc;
import org.mulima.api.meta.GenericTag;
import org.mulima.api.meta.Metadata;
import org.mulima.meta.util.MetadataUtil;

public class MetadataUtilTest {
	private Metadata meta1;
	private Metadata meta2;
	
	@Before
	public void initMeta() {
		meta1 = new Disc();
		meta1.add(GenericTag.ARTIST, "Genesis");
		meta1.add(GenericTag.ALBUM, "The Lamb Lies Down On Broadway (Disc 1)");
		
		meta2 = new Disc();
		meta2.add(GenericTag.ARTIST, "Genesis");
		meta2.add(GenericTag.ALBUM, "The Lamb Lies Down On Broadway (Disc 2)");
	}
	
	@Test
	public void tagDistance_same() {
		assertEquals(0, MetadataUtil.tagDistance(meta1, meta2, GenericTag.ARTIST));
	}
	
	@Test
	public void tagDistance_different() {
		assertEquals(1, MetadataUtil.tagDistance(meta1, meta2, GenericTag.ALBUM));
	}
	
	@Test
	public void discDistance_different() {
		assertEquals(1, MetadataUtil.discDistance(meta1, meta2));
	}
}
