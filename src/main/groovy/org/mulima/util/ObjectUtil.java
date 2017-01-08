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
package org.mulima.util;

/** General object utility methods. */
public class ObjectUtil {
  private ObjectUtil() {
    throw new AssertionError("Cannot instantiate this class.");
  }

  /**
   * Determines if two objects are equal. This method handles nulls safely (assuming the obj1 equals
   * method does as well).
   *
   * @param obj1 the first object
   * @param obj2 the second object
   * @return {@code true} if the two objects are equal
   * @deprecated use java.util.Objects#equals
   */
  @Deprecated
  public static boolean isEqual(Object obj1, Object obj2) {
    if (obj1 == null) {
      return obj2 == null;
    } else {
      return obj1.equals(obj2);
    }
  }
}
