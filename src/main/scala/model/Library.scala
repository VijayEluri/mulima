package org.ajoberstar.mulima
package model

import java.nio.file.Path

trait Library {
	def rootDir: Path
}

class SourceLibrary(
	val rootDir: Path
) extends Library

class DestinationLibrary(
	val rootDir: Path,
	val preferredFormat: Option[AudioFormat],
	val acceptedFormts: Set[AudioFormat],
	val copyArtWithFileName: Option[String],
	val embedArt: Boolean,
	val resizeArtTo: Option[(Int, Int)]
) extends Library
