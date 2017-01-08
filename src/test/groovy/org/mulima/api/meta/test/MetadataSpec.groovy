/*
 * Copyright 2010-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mulima.api.meta.test

import org.mulima.api.meta.GenericTag
import org.mulima.api.meta.Metadata

import spock.lang.Shared
import spock.lang.Specification

abstract class MetadataSpec<T extends Metadata> extends Specification {
  @Shared MetadataFactory factory = new MetadataFactory()
  T meta

  def setup() {
    meta = factory.newInstance(Metadata)
  }

  def 'isSet returns true only if tag is set'() {
    given:
    meta.add(GenericTag.ALBUM, 'Testing')
    meta.add(GenericTag.PERFORMER, 'Test Person')
    expect:
    meta.isSet(GenericTag.ALBUM)
    meta.isSet(GenericTag.PERFORMER)
    !meta.isSet(GenericTag.ARTIST)
    !meta.isSet(GenericTag.DISC_NUMBER)
  }

  def 'isSet should return false for a null tag'() {
    expect:
    !meta.isSet(null)
  }

  def 'add inserts the value to the proper tag'() {
    when:
    meta.add(tag, value)
    then:
    meta.getAll(tag) == [value]
    where:
    tag					| value
    GenericTag.ALBUM	| 'Trespass'
    GenericTag.COMPOSER	| 'Peter Gabriel'
    GenericTag.DATE		| '1970'
  }

  def 'add is no-op if tag is null'() {
    when:
    meta.add(null, 'Test')
    then:
    meta.map.isEmpty()
    notThrown(NullPointerException)
  }

  def 'add is no-op if value is null'() {
    when:
    meta.add(GenericTag.ALBUM, null)
    then:
    meta.map.isEmpty()
    notThrown(NullPointerException)
  }

  def 'addAll inserts all values to the proper tag'() {
    when:
    meta.addAll(tag, value)
    then:
    meta.getAll(tag) == value
    where:
    tag					| value
    GenericTag.ALBUM	| ['Space Groovy', 'Vector Patrol']
    GenericTag.COMPOSER	| ['Adrian Belew', 'Trey Gunn', 'Robert Fripp']
  }

  def 'addAll is no-op if tag is null'() {
    when:
    meta.addAll(null, ['Test'])
    then:
    meta.map.isEmpty()
    notThrown(NullPointerException)
  }

  def 'addAll is no-op if list is null'() {
    when:
    meta.addAll(GenericTag.ARTIST, null)
    then:
    meta.map.isEmpty()
    notThrown(NullPointerException)
  }

  def 'addAll is no-op if list is empty'() {
    when:
    meta.addAll(GenericTag.ARTIST, [])
    then:
    meta.map.isEmpty()
    notThrown(NullPointerException)
  }

  def 'getAll returns full list of values for the tag'() {
    given:
    def list1 = ['Peter Gabriel', 'Tony Banks', 'Mike Rutherford']
    def list2 = ['Phil Collins', 'Steve Hackett']
    def all = list1 + list2
    meta.addAll(GenericTag.COMPOSER, list1)
    meta.addAll(GenericTag.COMPOSER, list2)
    expect:
    meta.getAll(GenericTag.COMPOSER) == all
  }

  def 'getAll returns empty list if there are no values'() {
    expect:
    meta.getAll(GenericTag.ARTIST) == []
  }

  def 'getAll returns empty list if tag is null'() {
    expect:
    meta.getAll(null) == []
  }

  def 'getFirst only gets the first value for the tag'() {
    given:
    meta.addAll(GenericTag.ALBUM, ['Test1', 'Test2'])
    expect:
    meta.getFirst(GenericTag.ALBUM) == 'Test1'
  }

  def 'getFirst returns null if the tag is null'() {
    expect:
    meta.getFirst(null) == null
  }

  def 'getFlat returns all values assembled into one string'() {
    given:
    meta.addAll(GenericTag.CONDUCTOR, list)
    expect:
    meta.getFlat(GenericTag.CONDUCTOR) == flat
    where:
    list							| flat
    ['Emerson', 'Lake', 'Palmer']	| 'Emerson, Lake & Palmer'
    ['Fripp', 'Eno']				| 'Fripp & Eno'
    ['Genesis']						| 'Genesis'
  }

  def 'getFlat returns null if the tag is null'() {
    expect:
    meta.getFlat(null) == null
  }

  def 'getMap returns the full map of tags to values'() {
    given:
    Map map = [(GenericTag.ARTIST):['Karmakanic', 'Agents of Mercy'], (GenericTag.ALBUM):['The Power of Two']]
    map.each { tag, values ->
      meta.addAll(tag, values)
    }
    expect:
    meta.map == map
  }

  def 'getMap returns an empty map if no tags are set'() {
    expect:
    meta.map.isEmpty()
  }

  def 'remove removes only the values for the specified tag'() {
    given:
    meta.add(GenericTag.ALBUM, 'Selling England By The Pound')
    meta.addAll(GenericTag.GENRE, ['Progressive Rock', 'Art Rock'])
    when:
    meta.remove(GenericTag.GENRE)
    then:
    !meta.isSet(GenericTag.GENRE)
    meta.isSet(GenericTag.ALBUM)
  }

  def 'remove is no-op if the tag is null'() {
    given:
    meta.add(GenericTag.ALBUM, 'Selling England By The Pound')
    meta.addAll(GenericTag.GENRE, ['Progressive Rock', 'Art Rock'])
    when:
    meta.remove(null)
    then:
    meta.isSet(GenericTag.GENRE)
    meta.isSet(GenericTag.ALBUM)
  }

  def 'removeAll removes all values from the map'() {
    given:
    meta.add(GenericTag.ALBUM, 'Selling England By The Pound')
    meta.addAll(GenericTag.GENRE, ['Progressive Rock', 'Art Rock'])
    when:
    meta.removeAll()
    then:
    meta.map.isEmpty()
  }
}
