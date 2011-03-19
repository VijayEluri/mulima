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
package org.mulima.meta.dao.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.mulima.exception.FatalIOException;
import org.mulima.meta.Disc;
import org.mulima.meta.GenericTag;
import org.mulima.meta.Tag;
import org.mulima.meta.Track;
import org.mulima.meta.dao.FreeDbDao;
import org.mulima.meta.impl.FreeDbTag;
import org.mulima.util.ProgressBar;
import org.mulima.util.SLF4JProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides access to a * .tar or .tar.bz2 file containing FreeDb information.
 */
public class FreeDbTarDaoImpl implements FreeDbDao {
	private Logger logger = LoggerFactory.getLogger(FreeDbTarDaoImpl.class);
	private File bz2Archive;
	private File tarArchive;
	private File tempArchive;
	
	/**
	 * @param bz2Archive the bz2Archive to set
	 */
	public void setBz2Archive(File bz2Archive) {
		this.bz2Archive = bz2Archive;
	}

	/**
	 * @param tarArchive the tarArchive to set
	 */
	public void setTarArchive(File tarArchive) {
		this.tarArchive = tarArchive;
	}

	/**
	 * Extracts BZip2 archive, if it exists.  This leaves the instance
	 * with a Tar archive to manipulate.
	 */
	public void init() {
		logger.trace("Entering init");
		if (tarArchive == null) {
			if (bz2Archive == null) {
				throw new IllegalStateException("Either tarArchive or bz2Archive must be set.");
			}
			
			FileInputStream fin = null;
			BufferedInputStream bfin = null;
			BZip2CompressorInputStream bzin = null;
			FileOutputStream fout = null;
			try {
				this.tempArchive = File.createTempFile("freeDb", ".tar");
				this.tarArchive = tempArchive;
				fin = new FileInputStream(bz2Archive);
				bfin = new BufferedInputStream(fin);
				bzin = new BZip2CompressorInputStream(bfin);
				fout = new FileOutputStream(tempArchive);
				
				logger.info("Extracting BZip2 archive.");
				byte[] buffer = new byte[100000];
				int num = bzin.read(buffer);
				while (num != -1) {
					fout.write(buffer, 0, num);
					num = bzin.read(buffer);
				}
				logger.info("BZip2 Extraction complete.");
			} catch (IOException e) {
				logger.error("Problem extracting bzip2 archive.", e);
				throw new FatalIOException("Problem extracting bzip2 archive", e);
			} finally {
				try {
					if (fout != null) {
						fout.close();
					}
					if (bzin != null) {
						bzin.close();
					} else if (bfin != null) {
						bfin.close();
					} else if (fin != null) {
						fin.close();
					}
				} catch (IOException e) {
					logger.error("Problem closing streams.", e);
					throw new FatalIOException("Problem closing streams.", e);
				}
			}
		}
		logger.trace("Exiting init");
	}

	/**
	 * Cleans up the temporary archive, if it exists.
	 */
	public void destroy() {
		logger.trace("Entering destroy");
		if (tempArchive != null) {
			if (!tempArchive.delete()) {
				logger.warn("Failed to delete temporary archive.");
			}
		}
		logger.trace("Exiting destroy");
	}

	/**
	 * Not supported by this implementation.
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public List<Disc> getDiscsById(String cddbId) {
		throw new UnsupportedOperationException("This is not implemented by this DAO.");
	}
	
	/**
	 * Not supported by this implementation.
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public List<Disc> getDiscsById(List<String> cddbId) {
		throw new UnsupportedOperationException("This is not implemented by this DAO.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Disc> getAllDiscs() {
		return getAllDiscsFromOffset(0, -1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Disc> getAllDiscsFromOffset(int startNum, int numToRead) {
		FileInputStream fin = null;
		BufferedInputStream bfin = null;
		TarArchiveInputStream tin = null;
		List<Disc> discs = new ArrayList<Disc>();
		try {
			fin = new FileInputStream(tarArchive);
			bfin = new BufferedInputStream(fin);
			tin = new TarArchiveInputStream(bfin);
			
			int currentNum = 0;
			TarArchiveEntry entry = tin.getNextTarEntry();
			ProgressBar progress = new SLF4JProgressBar("TAR getDiscs", numToRead);
			while (entry != null && (numToRead < 0 || currentNum < startNum + numToRead)) {
				if (!entry.isDirectory() && currentNum >= startNum) {
					logger.debug("Loading: " + entry.getName());
					int offset = 0;
					byte[] content = new byte[(int) entry.getSize()];
					while (offset < content.length) {
						offset += tin.read(content, offset, content.length - offset);
					}
					Disc disc = bytesToDisc(content);
					if (disc == null) {
						logger.warn("Invalid file: " + entry.getName());
					} else {
						logger.debug(disc.toString());
						discs.add(disc);
					}
				}
				
				entry = tin.getNextTarEntry();
				currentNum++;
				progress.next();
			}
			
			if (entry == null) {
				progress.done();	
			}
		} catch (IOException e) {
			logger.error("Problem reading tar archive.", e);
			throw new FatalIOException("Problem reading tar archive.", e);
		} finally {
			try {
				if (tin != null) {
					tin.close();
				} else if (bfin != null) {
					bfin.close();
				} else if (fin != null) {
					fin.close();
				}
			} catch (IOException e) {
				logger.error("Problem closing streams.", e);
				throw new FatalIOException("Problem closing streams.", e);
			}
		}
		return discs;
	}
	
	private Disc bytesToDisc(byte[] content) {
		String[] lines = new String(content).split("\n");
		Disc disc = new Disc();
		
		Pattern regex = Pattern.compile("([A-Z]+)([0-9]+)?=(.*)");
		for (String line : lines) {
			Matcher matcher = regex.matcher(line);
			if (matcher.matches()) {
				Tag tag;
				try {
					tag = FreeDbTag.valueOf(matcher.group(1));
				} catch (IllegalArgumentException e) {
					continue;
				}
				if (matcher.group(2) == null || matcher.group(2) == "") {
					if ("DISCID".equals(matcher.group(1))) {
						for (String value : matcher.group(3).split(",")) {
							disc.add(tag, value);
						}
					} else if ("DTITLE".equals(matcher.group(1))) {
							String[] values = matcher.group(3).split(" / ");
							if (values.length == 1) {
								disc.add(tag, values[0]);
							} else {
								disc.add(tag, values[1]);
								disc.add(FreeDbTag.DARTIST, values[0]);
							}
					} else {
						disc.add(tag, matcher.group(3));
					}
				} else {
					Track track = new Track();
					track.add(GenericTag.TRACK_NUMBER, matcher.group(2));
					track.add(tag, matcher.group(3));
					disc.getTracks().add(track);
				}
			}
		}
		if (disc.getAll(FreeDbTag.DISCID) == null) {
			return null;
		} else {
			return disc;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addDisc(Disc disc) {
		throw new UnsupportedOperationException("This is not implemented by this DAO.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAllDiscs(List<Disc> discs) {
		throw new UnsupportedOperationException("This is not implemented by this DAO.");
	}
}
