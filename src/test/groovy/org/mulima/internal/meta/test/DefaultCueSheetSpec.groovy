package org.mulima.internal.meta.test

import org.mulima.api.meta.CueSheet
import org.mulima.api.meta.Metadata
import org.mulima.api.meta.test.CueSheetSpec
import org.mulima.internal.meta.DefaultCueSheet

class DefaultCueSheetSpec extends CueSheetSpec {
	def setupSpec() {
		factory.with {
			registerImplementation Metadata, DefaultCueSheet
			registerImplementation CueSheet, DefaultCueSheet
		}
	}
}
