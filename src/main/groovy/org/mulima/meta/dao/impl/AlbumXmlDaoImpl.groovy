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

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import groovy.xml.MarkupBuilder
import java.util.concurrent.Callable
import org.mulima.meta.Album
import org.mulima.meta.CueSheet
import org.mulima.meta.Disc
import org.mulima.meta.GenericTag
import org.mulima.meta.Metadata
import org.mulima.meta.Track
import org.mulima.meta.dao.MetadataFileDao

class AlbumXmlDaoImpl implements MetadataFileDao<Album> {	
	private final Logger logger = LoggerFactory.getLogger(getClass())
	
	void write(File file, Album album) {
		album.tidy()
		def writer = new PrintWriter(file, 'UTF-8')
		def indenter = new IndentPrinter(writer, '\t')
		def xml = new MarkupBuilder(indenter)
		
		xml.album {
			writeCues(xml, album.cues)
			writeTags(xml, album)
			writeDiscs(xml, album.discs)
		}
	}
	
	Callable<Void> writeLater(File file, Album album) {
		return new Callable<Void>() {
			Void call() {
				write(file, album)
			}
		}
	}
	
	private void writeTags(MarkupBuilder xml, Metadata meta) {
		meta.map.each { key, values ->
			values.each { value ->
				xml.tag(name:key.camelCase(), value:value)
			}
		}
	}
	
	private void writeCues(MarkupBuilder xml, List<CueSheet> cues) {
		cues.each { cueSheet ->
			xml.cue(num:cueSheet.num) {
				cueSheet.tracks.each { cueTrack ->
					track(num:cueTrack.num) {
						cueTrack.indices.each { cueIndex ->
							index(num:cueIndex.num, time:cueIndex.time)
						}
					}
				}
			}
		}
	}
	
	private void writeDiscs(MarkupBuilder xml, List<Disc> discs) {
		discs.each { albumDisc ->
			xml.disc {
				writeTags(xml, albumDisc)
				writeTracks(xml, albumDisc.tracks)
			}
		}
	}
	
	private void writeTracks(MarkupBuilder xml, List<Track> tracks) {
		tracks.each { albumTrack ->
			xml.track {
				writeTags(xml, albumTrack)
				//if (albumTrack.cueRef != null) {
				//	cueRef(cueNum:albumTrack.cueRef.cueNum, startNum:albumTrack.cueRef.startNum, endNum:albumTrack.cueRef.endNum)
				//}
			}
		}
	}
	
	Album read(File file) {
		def xml
		try {
			xml = new XmlParser().parse(file)
		} catch (e) {
			logger.error "Problem reading file: ${file.canonicalPath},", e
			throw e
		}
		def album = new Album()
		
		readCues(xml.cue, album.cues)
		readTags(xml.tag, album)
		readDiscs(xml.disc, album.discs)
		
		album.tidy()
		return album
	}
	
	Callable<Album> readLater(File file) {
		return new Callable<Album>() {
			Album call() {
				return read(file)
			}
		}
	}
	
	private void readTags(MarkupBuilder xml, Metadata meta) {
		xml.each { tagNode ->
			meta.add(GenericTag.valueOfCamelCase(tagNode.'@name'), tagNode.'@value')
		}
	}
	
	private void readCues(MarkupBuilder xml, List<CueSheet> cues) {
		xml.each { cueNode ->
			def cue = new CueSheet()
			cue.num = Integer.parseInt(cueNode.'@num')
			
			cueNode.track.each { trackNode ->
				def track = new CueSheet.Track()
				track.num = Integer.parseInt(trackNode.'@num')
				
				trackNode.index.each { indexNode ->
					def index = new CueSheet.Index()
					index.num = Integer.parseInt(indexNode.'@num')
					index.time = indexNode.'@time'
					track.indices.add(index)
				}
					
				cue.tracks.add(track)
			}
			
			cues.add(cue)
		}
	}
	
	private void readDiscs(MarkupBuilder xml, List<Disc> discs) {
		xml.each { discNode ->
			def disc = new Disc()
			readTags(discNode.tag, disc)
			readTracks(discNode.track, disc.tracks)
			discs.add(disc)
		}
	}
	
	private void readTracks(MarkupBuilder xml, List<Track> tracks) {
		xml.each { trackNode ->
			def track = new Track()
			readTags(trackNode.tag, track)
			
			//trackNode.cueRef.each {
			//	def cueRef = new Track.CueRef()
			//	cueRef.cueNum = Integer.parseInt(it.'@cueNum')
			//	cueRef.startNum = Integer.parseInt(it.'@startNum')
			//	cueRef.endNum = Integer.parseInt(it.'@endNum')
			//	track.cueRef = cueRef
			//}
			
			tracks.add(track)
		}
	}
}