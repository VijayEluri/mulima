package com.andrewoberstar.library.meta.dao.test

import groovy.xml.MarkupBuilder

import com.andrewoberstar.library.meta.Album
import com.andrewoberstar.library.meta.Disc
import com.andrewoberstar.library.meta.TagSupportFactory;
import com.andrewoberstar.library.meta.Track

class AlbumXmlHelper {
	static Album getExampleAlbum() {
		def album
		def disc
		
		album = new Album()
		album.tags = TagSupportFactory.fromStringString([ARTIST:'Genesis', ALBUM:'Foxtrot', GENRE:'Progressive Rock', DATE:'1972', CDDB_ID:'520C0506'])
		
		def cue = CueSheetHelper.getExampleCue()
		cue.tags = TagSupportFactory.fromStringString([:])
		cue.tracks.each { it.tags = TagSupportFactory.fromStringString([:]) }
		
		album.cues.add(cue)
		
		disc = new Disc()
		disc.tags = TagSupportFactory.fromStringString([DISC_NUMBER:'1'])
		
		disc.tracks.add(createTrack([TRACK_NUMBER:'1', TITLE:'Watcher of the Skies']))
		disc.tracks.add(createTrack([TRACK_NUMBER:'2', TITLE:'Time Table']))
		disc.tracks.add(createTrack([TRACK_NUMBER:'3', TITLE:"Get 'Em Out By Friday"]))
		disc.tracks.add(createTrack([TRACK_NUMBER:'4', TITLE:'Can-Utility and the Coastliners']))
		disc.tracks.add(createTrack([TRACK_NUMBER:'5', TITLE:'Horizons']))
		disc.tracks.add(createTrack([TRACK_NUMBER:'6', TITLE:"Supper's Ready"]))
		
		album.discs.add(disc)
		
		return album
	}
	
	static def createTrack(def tags) {
		def track = new Track()
		track.tags = TagSupportFactory.fromStringString(tags)
		return track
	}
	
	static void writeExampleFile(File exampleFile) {
		def writer = new PrintWriter(exampleFile)
		def indenter = new IndentPrinter(writer)
		def xml = new MarkupBuilder(indenter)
		
		xml.album {
			cue(num:'1') {
				track(num:'1') {
					index(num:'0', time:'00:00:00')
					index(num:'1', time:'00:01:00')
				}
				track(num:'2') {
					index(num:'0', time:'07:24:12')
					index(num:'1', time:'07:24:16')
				}
				track(num:'3') {
					index(num:'0', time:'12:10:40')
					index(num:'1', time:'12:10:43')
				}
				track(num:'4') {
					index(num:'0', time:'20:46:12')
					index(num:'1', time:'20:46:16')
				}
				track(num:'5') {
					index(num:'0', time:'26:31:09')
					index(num:'1', time:'26:31:13')
				}
				track(num:'6') {
					index(num:'0', time:'28:12:21')
					index(num:'1', time:'28:12:25')
				}
			}
			
			tag(name:'artist', value:'Genesis')
			tag(name:'album', value:'Foxtrot')
			tag(name:'genre', value:'Progressive Rock')
			tag(name:'date', value:'1972')
			tag(name:'cddbId', value:'520C0506')
			
			disc {
				tag(name:'discNumber', value:'1')
				track {
					tag(name:'trackNumber', value:'1')
					tag(name:'title', value:'Watcher of the Skies')
				}
				track {
					tag(name:'trackNumber', value:'2')
					tag(name:'title', value:'Time Table')
				}
				track {
					tag(name:'trackNumber', value:'3')
					tag(name:'title', value:"Get 'Em Out By Friday")
				}
				track {
					tag(name:'trackNumber', value:'4')
					tag(name:'title', value:'Can-Utility and the Coastliners')
				}
				track {
					tag(name:'trackNumber', value:'5')
					tag(name:'title', value:'Horizons')
				}
				track {
					tag(name:'trackNumber', value:'6')
					tag(name:'title', value:"Supper's Ready")
				}
			}
		}
		writer.close()
	}
}