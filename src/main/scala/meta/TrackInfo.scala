package org.ajoberstar.mulima
package meta

import java.nio.Path

class TrackInfo(
	val tags: Map[String, Set[String]],
	val coverFile: Path,
	val audioFile: Path,
	val startTime: CueTime,
	val endTime: CueTime
)
