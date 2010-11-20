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
