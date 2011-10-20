package org.mulima.util.test

import org.mulima.api.meta.GenericTag
import org.mulima.api.meta.Metadata
import org.mulima.internal.meta.DefaultDisc
import org.mulima.util.MetadataUtil

import spock.lang.Shared
import spock.lang.Specification

class MetadataUtilSpec extends Specification {
	@Shared Metadata meta1
	@Shared Metadata meta2
	@Shared Metadata meta3
	
	def setupSpec() {
		meta1 = new DefaultDisc()
		meta1.addAll(GenericTag.ARTIST, 'Genesis')
		meta1.addAll(GenericTag.ALBUM, 'The Lamb Lies Down On Broadway (Disc 1)')
		meta1.addAll(GenericTag.DISC_NUMBER, '1')
		
		meta2 = new DefaultDisc()
		meta2.addAll(GenericTag.ARTIST, 'Genesis')
		meta2.addAll(GenericTag.ALBUM, 'The Lamb Lies Down On Broadway (Disc 2)')
		meta2.addAll(GenericTag.DISC_NUMBER, '2')
		
		meta3 = new DefaultDisc()
		meta3.addAll(GenericTag.ARTIST, 'Regenesis')
		meta3.addAll(GenericTag.ALBUM, 'A Tribute To The Lamb Lies Down On Broadway')
		meta3.addAll(GenericTag.DISC_NUMBER, '1')
	}
	
	def 'tagDistance shows levenshtein distance on a specific tag'() {
		expect:
		MetadataUtil.tagDistance(obj1, obj2, tag) == distance
		where:
		obj1	| obj2	| tag						| distance
		meta1	| meta2	| GenericTag.ARTIST			| 0
		meta1	| meta2	| GenericTag.ALBUM			| 1
		meta1	| meta2	| GenericTag.DISC_NUMBER	| 1
		meta2	| meta3	| GenericTag.ARTIST			| 3
		meta2	| meta3	| GenericTag.ALBUM			| 22
	}
	
	def 'discDistance shows levenshtein distance of ARTIST and ALBUM tags'() {
		expect:
		MetadataUtil.discDistance(obj1, obj2) == distance
		where:
		obj1	| obj2	| distance
		meta1	| meta2	| 1
		meta2	| meta3	| 25
	}
}
