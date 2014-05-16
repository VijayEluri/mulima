package org.ajoberstar.mulima
package meta

trait Metadata {
	def parent: T
	def isSet(tag: Tag): Boolean

}
