package com.andrewoberstar.library.meta.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.andrewoberstar.library.meta.Disc;
import com.andrewoberstar.library.meta.GenericTag;

public class DiscTest {
	@Test
	public void getNum() {
		Disc disc = new Disc();
		disc.getTags().add(GenericTag.DISC_NUMBER, Integer.toString(1));
		assertEquals(1, disc.getNum());
	}
}
