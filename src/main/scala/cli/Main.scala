package org.ajoberstar.mulima
package cli

import org.ajoberstar.mulima.model._

object Main extends App {
	List(
		"0:10:05",
		"100:04:70",
		"0:10.008",
		"0:07.049",
		"4:14.831"
	).foreach { original =>
		println("*****")
		println(original)
		val cue = CueTime(original)
		println(cue.toFrames)
		println(cue.toMillis)
	}
}
