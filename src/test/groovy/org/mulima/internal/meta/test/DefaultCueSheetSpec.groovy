package org.mulima.internal.meta.test

import org.mulima.api.meta.test.MetadataSpec
import org.mulima.internal.meta.DefaultCueSheet

class DefaultCueSheetSpec extends MetadataSpec {
	def setup() {
		meta = new DefaultCueSheet()
	}
}
