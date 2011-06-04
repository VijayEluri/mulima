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
package org.mulima.meta.dao.impl

import groovy.xml.MarkupBuilder

import java.util.concurrent.Callable

import org.mulima.api.meta.Album
import org.mulima.api.meta.CuePoint
import org.mulima.api.meta.CueSheet
import org.mulima.api.meta.Disc
import org.mulima.api.meta.GenericTag;
import org.mulima.api.meta.Metadata
import org.mulima.api.meta.Track
import org.mulima.meta.dao.MetadataFileDao
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * DAO that will read and write album.xml files.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
class AlbumXmlDaoImpl implements MetadataFileDao<Album> {	
	private final Logger logger = LoggerFactory.getLogger(getClass())
	
	/**
	 * Writes an album.xml file representing the specified
	 * album.
	 * @param file the file to write to
	 * @param album the album to write
	 */
	void write(File file, Album album) {
		album.tidy()
		def writer = new PrintWriter(file, 'UTF-8')
		def indenter = new IndentPrinter(writer, '\t')
		def xml = new MarkupBuilder(indenter)
		
		xml.album {
			writeTags(xml, album)
			writeDiscs(xml, album.discs)
		}
	}
	
	/**
	 * Writes an album.xml file representing the specified
	 * album.
	 * @param file the file to write to
	 * @param album the album to write
	 * @return a Callable that will write the file
	 */
	Callable<Void> writeLater(File file, Album album) {
		return new Callable<Void>() {
			Void call() {
				write(file, album)
			}
		}
	}
	
	/**
	 * Helper to write tags.
	 * @param xml a builder to write with
	 * @param meta the metadata to write
	 */
	private void writeTags(MarkupBuilder xml, Metadata meta) {
		meta.map.each { key, values ->
			values.each { value ->
				xml.tag(name:key.camelCase(), value:value)
			}
		}
	}
	
	/**
	 * Helper to write discs.
	 * @param xml a builder to write with
	 * @param discs the discs to write
	 */
	private void writeDiscs(MarkupBuilder xml, SortedSet<Disc> discs) {
		discs.each { albumDisc ->
			xml.disc {
				writeTags(xml, albumDisc)
				writeTracks(xml, albumDisc.tracks)
			}
		}
	}
	
	/**
	 * Helper to write tracks.
	 * @param xml a builder to write with
	 * @param tracks the tracks to write
	 */
	private void writeTracks(MarkupBuilder xml, SortedSet<Track> tracks) {
		tracks.each { albumTrack ->
			xml.track {
				writeTags(xml, albumTrack)
				//if (albumTrack.cueRef != null) {
				//	cueRef(cueNum:albumTrack.cueRef.cueNum, startNum:albumTrack.cueRef.startNum, endNum:albumTrack.cueRef.endNum)
				//}
			}
		}
	}
	
	/**
	 * Parses an album.xml file.
	 * @param file the file to parse
	 * @return an Album representing the file contents
	 */
	Album read(File file) {
		def xml
		try {
			xml = new XmlParser().parse(file)
		} catch (e) {
			logger.error "Problem reading file: ${file.canonicalPath},", e
			throw e
		}
		def album = new Album()
		
		readTags(xml.tag, album)
		readDiscs(xml.disc, album.discs)
		
		album.tidy()
		return album
	}
	
	/**
	 * Parses an album.xml file.
	 * @param file the file to parse
	 * @return a Callable that will parse the file
	 */
	Callable<Album> readLater(File file) {
		return new Callable<Album>() {
			Album call() {
				return read(file)
			}
		}
	}
	
	/**
	 * Helper to read tags.
	 * @param xml a node list to read from
	 * @param meta the metadata to add tags to
	 */
	private void readTags(NodeList xml, Metadata meta) {
		xml.each { tagNode ->
			meta.add(GenericTag.valueOfCamelCase(tagNode.'@name'), tagNode.'@value')
		}
	}
	
	/**
	 * Helper to read discs.
	 * @param xml a node list to read from
	 * @param discs the set to add discs to
	 */
	private void readDiscs(NodeList xml, SortedSet<Disc> discs) {
		xml.each { discNode ->
			def disc = new Disc()
			readTags(discNode.tag, disc)
			readTracks(discNode.track, disc.tracks)
			discs.add(disc)
		}
	}
	
	/**
	 * Helper to read tracks.
	 * @param xml a node list to read from
	 * @param tracks the set to add tracks to
	 */
	private void readTracks(NodeList xml, SortedSet<Track> tracks) {
		xml.each { trackNode ->
			def track = new Track()
			readTags(trackNode.tag, track)
			
//			trackNode.startPoint.with {
//				track.startPoint = new CuePoint(
//					Integer.parseInt(it.'@track'),
//					Integer.parseInt(it.'@index'),
//					it.'@time'
//				)
//			}
//			
//			trackNode.endPoint.with {
//				track.endPoint = new CuePoint(
//					Integer.parseInt(it.'@track'),
//					Integer.parseInt(it.'@index'),
//					it.'@time'
//				)
//			}
			
			tracks.add(track)
		}
	}
}