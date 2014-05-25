package org.ajoberstar.mulima
package model

import java.nio.file.Path

object AudioFormat {
	private val extensionsByFormat: Map[AudioFormat, Set[String]] = Map(
		WAVE -> Set("wav", "wave"),
		FLAC -> Set("flac"),
		VORBIS -> Set("ogg"),
		AAC -> Set("m4a"),
		MP3 -> Set("mp3")
	)

	def apply(extension: String): Option[AudioFormat] = {
		val lowerExtension = extension.toLowerCase
		extensionsByFormat.find { case (format, extensions) =>
			extensions.exists(_ == lowerExtension)
		}.map { case (format, extensions) =>
			format
		}
	}

	def primaryExtension(format: AudioFormat): String = extensionsByFormat(format).head

	def isLossy(format: AudioFormat): Boolean = format match {
		case WAVE => false
		case FLAC => false
		case _ => true
	}
}

sealed trait AudioFormat {
	def primaryExtension: String = AudioFormat.primaryExtension(this)
}
case object WAVE extends AudioFormat
case object FLAC extends AudioFormat
case object VORBIS extends AudioFormat
case object AAC extends AudioFormat
case object MP3 extends AudioFormat
