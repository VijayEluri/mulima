package org.mulima.internal.meta

import groovy.xml.MarkupBuilder

import org.mulima.api.file.FileComposer
import org.mulima.api.file.FileParser
import org.mulima.api.meta.Album
import org.mulima.api.meta.CuePoint
import org.mulima.api.meta.Disc
import org.mulima.api.meta.GenericTag
import org.mulima.api.meta.Metadata
import org.mulima.api.meta.Track
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Default parser and composer implementation for an Album.  Formats into
 * XML.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
class AlbumXmlDao implements FileParser<Album>, FileComposer<Album> {
	private final Logger logger = LoggerFactory.getLogger(AlbumXmlDao)
	
	/**
	 * Parses an album.xml file.
	 * @param file the file to parse
	 * @return an Album representing the file contents
	 */
	Album parse(File file) {
		def xml
		try {
			xml = new XmlParser().parse(file)
		} catch (e) {
			logger.error "Problem reading file: ${file.canonicalPath},", e
			throw e
		}
		def album = new DefaultAlbum()
		parseTags(xml.tag, album)
		parseDiscs(xml.disc, album.discs)
		
		album.tidy()
		return album
	}
	
	/**
	 * Helper to read tags.
	 * @param xml a node list to read from
	 * @param meta the metadata to add tags to
	 */
	private void parseTags(NodeList xml, Metadata meta) {
		xml.each { tagNode ->
			meta.add(GenericTag.valueOfCamelCase(tagNode.'@name'), tagNode.'@value')
		}
	}
	
	/**
	 * Helper to read discs.
	 * @param xml a node list to read from
	 * @param discs the set to add discs to
	 */
	private void parseDiscs(NodeList xml, SortedSet<Disc> discs) {
		xml.each { discNode ->
			def disc = new DefaultDisc()
			parseTags(discNode.tag, disc)
			parseTracks(discNode.track, disc.tracks)
			discs.add(disc)
		}
	}
	
	/**
	 * Helper to read tracks.
	 * @param xml a node list to read from
	 * @param tracks the set to add tracks to
	 */
	private void parseTracks(NodeList xml, SortedSet<Track> tracks) {
		xml.each { trackNode ->
			def track = new DefaultTrack()
			parseTags(trackNode.tag, track)
			
			if (trackNode.startPoint) {
				trackNode.startPoint[0].with {
					track.startPoint = new DefaultCuePoint(
						Integer.parseInt(it.'@track'),
						Integer.parseInt(it.'@index'),
						it.'@time'
					)
				}
			}

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
	
	
	/**
	 * Writes an album.xml file representing the specified
	 * album.
	 * @param file the file to write to
	 * @param album the album to write
	 */
	void compose(File file, Album album) {
		album.tidy()
		def writer = new PrintWriter(file, 'UTF-8')
		def indenter = new IndentPrinter(writer, '\t')
		def xml = new MarkupBuilder(indenter)
		
		xml.album {
			composeTags(xml, album)
			composeDiscs(xml, album.discs)
		}
	}
	
	/**
	 * Helper to write tags.
	 * @param xml a builder to write with
	 * @param meta the metadata to write
	 */
	private void composeTags(MarkupBuilder xml, Metadata meta) {
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
	private void composeDiscs(MarkupBuilder xml, SortedSet<Disc> discs) {
		discs.each { albumDisc ->
			xml.disc {
				composeTags(xml, albumDisc)
				composeTracks(xml, albumDisc.tracks)
			}
		}
	}
	
	/**
	 * Helper to write tracks.
	 * @param xml a builder to write with
	 * @param tracks the tracks to write
	 */
	private void composeTracks(MarkupBuilder xml, SortedSet<Track> tracks) {
		tracks.each { Track albumTrack ->
			xml.track {
				composeTags(xml, albumTrack)
				CuePoint point = albumTrack.startPoint
				if (point) {
					startPoint(track:point.track, index:point.index, time:point.time)
				}
			}
		}
	}
}
