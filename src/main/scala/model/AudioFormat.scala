package org.ajoberstar.mulima
package model

import java.nio.file.Path

object AudioFormat {
	private val extensionsByFormat = Map(
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

	def isLossy(format: AudioFormat) = format match {
		case _: WAVE => false
		case _: FLAC => false
		case _ => true
	}
}

sealed trait AudioFormat
case object WAVE extends AudioFormat
case object FLAC extends AudioFormat
case object VORBIS extends AudioFormat
case object AAC extends AudioFormat
case object MP3 extends AudioFormat
