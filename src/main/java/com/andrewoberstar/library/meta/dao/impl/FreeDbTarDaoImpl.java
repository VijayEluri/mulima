package com.andrewoberstar.library.meta.dao.impl;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.andrewoberstar.library.exception.FatalIOException;
import com.andrewoberstar.library.meta.Disc;
import com.andrewoberstar.library.meta.FreeDbTag;
import com.andrewoberstar.library.meta.GenericTag;
import com.andrewoberstar.library.meta.Tag;
import com.andrewoberstar.library.meta.Track;
import com.andrewoberstar.library.meta.dao.FreeDbDao;
import com.andrewoberstar.library.util.ProgressBar;

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

	public void init() {
		logger.trace("Entering init");
		if (tarArchive == null) {
			if (bz2Archive == null)
				throw new IllegalStateException("Either tarArchive or bz2Archive must be set.");
			
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
			} catch(IOException e) {
				logger.error("Problem extracting bzip2 archive.", e);
				throw new FatalIOException("Problem extracting bzip2 archive", e);
			} finally {
				try {
					if (fout != null)
						fout.close();
					if (bzin != null)
						bzin.close();
					else if (bfin != null)
						bfin.close();
					else if (fin != null)
						fin.close();
				} catch (IOException e) {
					logger.error("Problem closing streams.", e);
					throw new FatalIOException("Problem closing streams.", e);
				}
			}
		}
		logger.trace("Exiting init");
	}

	public void destroy() {
		logger.trace("Entering destroy");
		if (tempArchive != null) {
			if (!tempArchive.delete())
				logger.warn("Failed to delete temporary archive.");
		}
		logger.trace("Exiting destroy");
	}

	@Override
	public List<Disc> getDiscsById(String cddbId) {
		throw new UnsupportedOperationException("This is not implemented by this DAO.");
	}
	
	@Override
	public List<Disc> getDiscsById(List<String> cddbId) {
		throw new UnsupportedOperationException("This is not implemented by this DAO.");
	}

	@Override
	public List<Disc> getAllDiscs() {
		return getAllDiscsFromOffset(0, -1);
	}

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
			ProgressBar progress = new ProgressBar("TAR getDiscs", numToRead);
			while (entry != null && (numToRead < 0 || currentNum < startNum + numToRead)) {
				if (!entry.isDirectory() && currentNum >= startNum) {
					logger.debug("Loading: " + entry.getName());
					int offset = 0;
					byte[] content = new byte[(int) entry.getSize()];
					while(offset < content.length) {
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
			
			if (entry == null)
				progress.done();
		} catch (IOException e) {
			logger.error("Problem reading tar archive.", e);
			throw new FatalIOException("Problem reading tar archive.", e);
		} finally {
			try {
				if (tin != null)
					tin.close();
				else if (bfin != null)
					bfin.close();
				else if (fin != null)
					fin.close();
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
						for (String value : matcher.group(3).split(","))	
							disc.getTags().add(tag, value);
					} else if ("DTITLE".equals(matcher.group(1))) {
							String[] values = matcher.group(3).split(" / ");
							if (values.length == 1) {
								disc.getTags().add(tag, values[0]);
							} else {
								disc.getTags().add(tag, values[1]);
								disc.getTags().add(FreeDbTag.DARTIST, values[0]);
							}
					} else {
						disc.getTags().add(tag, matcher.group(3));
					}
				} else {
					Track track = new Track();
					track.getTags().add(GenericTag.TRACK_NUMBER, matcher.group(2));
					track.getTags().add(tag, matcher.group(3));
					disc.getTracks().add(track);
				}
			}
		}
		if (disc.getTags().getAll(FreeDbTag.DISCID) == null)
			return null;
		else
			return disc;
	}

	@Override
	public void addDisc(Disc disc) {
		throw new UnsupportedOperationException("This is not implemented by this DAO.");
	}

	@Override
	public void addAllDiscs(List<Disc> discs) {
		throw new UnsupportedOperationException("This is not implemented by this DAO.");
	}
}
