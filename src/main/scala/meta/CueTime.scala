package org.ajoberstar.mulima
package meta

import scala.util.matching.Regex
import spire.math.Rational

object CueTime {
	private val MILLIS_REGEX = """^(\d+):(\d{2}).(\d{3})$""".r
	private val FRAME_REGEX = """^(\d+):(\d{2}):(\d{2})$""".r

	def apply(value: String): CueTime = {
		def cueTime(denominator: Int)(result: Regex.Match): CueTime = {
			result.subgroups.map(_.toInt) match {
				case list => new CueTime(list(0), list(1), Rational(list(2), denominator))
			}
		}

		FRAME_REGEX.findFirstMatchIn(value).map(cueTime(75)).orElse {
			MILLIS_REGEX.findFirstMatchIn(value).map(cueTime(1000))
		}.getOrElse {
			new CueTime(0, 0, 0)
		}
	}
}

class CueTime(
	val minutes: Int,
	val seconds: Int,
	val fraction: Rational
) extends Ordered[CueTime] {
		require(minutes >= 0, s"Minutes cannot be negative: $minutes")
		require(seconds >= 0 && seconds < 60, s"Seconds must be between 0 and 59 (inclusive): $seconds")
		require(fraction >= 0 && fraction < 1, s"Fraction must be between 0 and 1 (inclusive): $fraction")

		def toFrames: String = {
			val frames = (fraction * 75).intValue
			f"$minutes:$seconds%02d:$frames%02d"
		}

		def toMillis: String = {
			val millis = (fraction * 1000).intValue
			f"$minutes:$seconds%02d.$millis%03d"
		}

		override def compare(that: CueTime): Int = {
			Ordering[Tuple3[Int, Int, Rational]].on[CueTime](x => (x.minutes, x.seconds, x.fraction)).compare(this, that)
		}

		override def equals(other: Any): Boolean = other match {
			case that: CueTime => compare(that) == 0
			case _ => false
		}

		override def hashCode: Int = {
			// TODO figure out better hashCode approach
			var result = 0
			result = result * 31 + minutes.hashCode
			result = result * 31 + seconds.hashCode
			result = result * 31 + fraction.hashCode
			result
		}

		override def toString: String = toMillis
}
