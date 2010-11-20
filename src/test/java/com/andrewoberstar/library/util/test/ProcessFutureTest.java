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

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.andrewoberstar.library.util.ProcessFuture;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcessFutureTest {
	@Mock private Process proc;
	private InputStream outIs;
	private PipedOutputStream outOs;
	private InputStream errIs;
	private PipedOutputStream errOs;
	private ProcessFuture future;
	
	@Before
	public void before() throws IOException {
		outOs = new PipedOutputStream();
		outIs = new PipedInputStream(outOs);
		errOs = new PipedOutputStream();
		errIs = new PipedInputStream(errOs);
		when(proc.getInputStream()).thenReturn(outIs);
		when(proc.getErrorStream()).thenReturn(errIs);
		future = new ProcessFuture(proc);
	}
	
	@Test
	public void cancelAlreadyComplete() {
		assertFalse(future.cancel(false));
	}
	
	@Test
	public void cancelNoInterrupt() {
		when(proc.exitValue()).thenThrow(new IllegalThreadStateException());
		assertFalse(future.cancel(false));
	}
	
	@Test
	public void cancelMayInterrupt() {
		when(proc.exitValue()).thenThrow(new IllegalThreadStateException());
		assertTrue(future.cancel(true));
	}
	
	@Test
	public void isCancelledFalse() {
		assertFalse(future.isCancelled());
	}
	
	@Test
	public void isCancelledTrue() {
		when(proc.exitValue()).thenThrow(new IllegalThreadStateException());
		future.cancel(true);
		assertTrue(future.isCancelled());
	}
	
	@Test
	public void isDoneFalse() {
		when(proc.exitValue()).thenThrow(new IllegalThreadStateException());
		assertFalse(future.isDone());
	}
	
	@Test
	public void isDoneCancelledTrue() {
		when(proc.exitValue()).thenThrow(new IllegalThreadStateException());
		future.cancel(true);
		assertTrue(future.isDone());
	}
	
	@Test
	public void isDoneCompleteTrue() {
		assertTrue(future.isDone());
	}
	
	@Test (expected = CancellationException.class)
	public void getCancelled() throws InterruptedException {
		when(proc.exitValue()).thenThrow(new IllegalThreadStateException());
		future.cancel(true);
		future.get();
	}
	
	@Test
	public void getSuccess() throws InterruptedException {
		assertEquals(Integer.valueOf(0), future.get());
	}
	
	@Test (expected = TimeoutException.class)
	public void getTimeout() throws InterruptedException, TimeoutException {
		when(proc.exitValue()).thenThrow(new IllegalThreadStateException());
		future.get(0, TimeUnit.MILLISECONDS);
	}
	
	@Test
	public void getOutputSuccess() throws IOException, InterruptedException, ExecutionException {
		String expected = "this worked\n";
		outOs.write(expected.getBytes());
		outOs.close();
		assertEquals(expected, future.getOutput());
	}
	
	@Test (expected = CancellationException.class) 
	public void getOutputCancelled() throws InterruptedException, ExecutionException {
		when(proc.exitValue()).thenThrow(new IllegalThreadStateException());
		future.cancel(true);
		future.getOutput();
	}
	
	@Test
	public void getErrorSuccess() throws IOException, InterruptedException, ExecutionException {
		String expected = "this worked\n";
		errOs.write(expected.getBytes());
		errOs.close();
		assertEquals(expected, future.getError());
	}
	
	@Test (expected = CancellationException.class) 
	public void getErrorCancelled() throws InterruptedException, ExecutionException {
		when(proc.exitValue()).thenThrow(new IllegalThreadStateException());
		future.cancel(true);
		future.getError();
	}
	
	@After
	public void after() throws IOException {
		outIs.close();
		errIs.close();
		outOs.close();
		errOs.close();
		future.cancel(true);
	}
}
