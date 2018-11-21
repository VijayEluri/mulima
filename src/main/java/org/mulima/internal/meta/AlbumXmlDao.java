package org.mulima.internal.meta;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulima.api.file.FileComposer;
import org.mulima.api.file.FileParser;
import org.mulima.api.meta.Album;

/**
 * Default parser and composer implementation for an Album. Formats into XML.
 * 
 * @author Andrew Oberstar
 * @since 0.1.0
 */
public class AlbumXmlDao implements FileParser<Album>, FileComposer<Album> {
  private final Logger logger = LogManager.getLogger(AlbumXmlDao.class);

  /**
   * Parses an album.xml file.
   * 
   * @param file the file to parse
   * @return an Album representing the file contents
   */
  public Album parse(File file) {
    // try {
    // def xml = new XmlParser().parse(file);
    // def album = new DefaultAlbum();
    // parseTags(xml.tag, album);
    // parseDiscs(xml.disc, album);
    // album.tidy();
    // return album;
    // } catch (e) {
    // logger.info "Problem reading file: ${file.canonicalPath},", e;
    // return null;
    // }
    // FIXME implement this
    return null;
  }

  // /**
  // * Helper to read tags.
  // * @param xml a node list to read from
  // * @param meta the metadata to add tags to
  // */
  // private void parseTags(NodeList xml, Metadata meta) {
  // xml.each { tagNode ->
  // meta.add(GenericTag.valueOfCamelCase(tagNode.'@name'), tagNode.'@value');
  // }
  // }

  // /**
  // * Helper to read discs.
  // * @param xml a node list to read from
  // * @param discs the set to add discs to
  // */
  // private void parseDiscs(NodeList xml, Album album) {
  // xml.each { discNode ->
  // def disc = new DefaultDisc(album);
  // parseTags(discNode.tag, disc);
  // parseTracks(discNode.track, disc);
  // album.discs.add(disc);
  // }
  // }

  // /**
  // * Helper to read tracks.
  // * @param xml a node list to read from
  // * @param tracks the set to add tracks to
  // */
  // private void parseTracks(NodeList xml, Disc disc) {
  // xml.each { trackNode ->
  // def track = new DefaultTrack(disc);
  // parseTags(trackNode.tag, track);

  // if (trackNode.startPoint) {
  // trackNode.startPoint[0].with {
  // track.startPoint = new DefaultCuePoint(
  // Integer.parseInt(it.'@track'),
  // Integer.parseInt(it.'@index'),
  // it.'@time'
  // );
  // }
  // }

  // // trackNode.endPoint.with {
  // // track.endPoint = new CuePoint(
  // // Integer.parseInt(it.'@track'),
  // // Integer.parseInt(it.'@index'),
  // // it.'@time'
  // // )
  // // }

  // disc.tracks.add(track);
  // }
  // }


  /**
   * Writes an album.xml file representing the specified album.
   * 
   * @param file the file to write to
   * @param album the album to write
   */
  public void compose(File file, Album album) {
    // album.tidy()
    // def writer = new PrintWriter(file, 'UTF-8');
    // def indenter = new IndentPrinter(writer, '\t');
    // def xml = new MarkupBuilder(indenter);

    // xml.album {
    // composeTags(xml, album);
    // composeDiscs(xml, album.discs);
    // }
  }

  // /**
  // * Helper to write tags.
  // * @param xml a builder to write with
  // * @param meta the metadata to write
  // */
  // private void composeTags(MarkupBuilder xml, Metadata meta) {
  // meta.map.each { key, values ->
  // values.each { value ->
  // xml.tag(name:key.camelCase(), value:value);
  // }
  // }
  // }

  // /**
  // * Helper to write discs.
  // * @param xml a builder to write with
  // * @param discs the discs to write
  // */
  // private void composeDiscs(MarkupBuilder xml, SortedSet<Disc> discs) {
  // discs.each { albumDisc ->
  // xml.disc {
  // composeTags(xml, albumDisc)
  // composeTracks(xml, albumDisc.tracks)
  // }
  // }
  // }

  // /**
  // * Helper to write tracks.
  // * @param xml a builder to write with
  // * @param tracks the tracks to write
  // */
  // private void composeTracks(MarkupBuilder xml, SortedSet<Track> tracks) {
  // tracks.each { Track albumTrack ->
  // xml.track {
  // composeTags(xml, albumTrack)
  // CuePoint point = albumTrack.startPoint
  // if (point) {
  // startPoint(track:point.track, index:point.index, time:point.time)
  // }
  // }
  // }
  // }
}