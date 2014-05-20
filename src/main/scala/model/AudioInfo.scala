package org.ajoberstar.mulima
package model

import java.nio.file.Path

class AudioInfo(
	val audioFile: Path,
	val cuePointPre: CueTime,
	val cuePointStart: CueTime,
	val cuePointEnd: CueTime,
	val artFile: Option[Path],
	val tags: Map[String, Seq[String]]
)
