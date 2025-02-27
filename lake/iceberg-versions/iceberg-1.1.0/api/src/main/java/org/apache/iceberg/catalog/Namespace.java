/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iceberg.catalog;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.apache.iceberg.relocated.com.google.common.base.Joiner;
import org.apache.iceberg.relocated.com.google.common.base.Preconditions;

/** A namespace in a {@link Catalog}. */
public class Namespace {
  private static final Namespace EMPTY_NAMESPACE = new Namespace(new String[] {});
  private static final Joiner DOT = Joiner.on('.');
  private static final Predicate<String> CONTAINS_NULL_CHARACTER =
      Pattern.compile("\u0000", Pattern.UNICODE_CHARACTER_CLASS).asPredicate();

  public static Namespace empty() {
    return EMPTY_NAMESPACE;
  }

  public static Namespace of(String... levels) {
    Preconditions.checkArgument(null != levels, "Cannot create Namespace from null array");
    if (levels.length == 0) {
      return empty();
    }

    for (String level : levels) {
      Preconditions.checkNotNull(level, "Cannot create a namespace with a null level");
      Preconditions.checkArgument(
          !CONTAINS_NULL_CHARACTER.test(level),
          "Cannot create a namespace with the null-byte character");
    }

    return new Namespace(levels);
  }

  private final String[] levels;

  private Namespace(String[] levels) {
    this.levels = levels;
  }

  public String[] levels() {
    return levels;
  }

  public String level(int pos) {
    return levels[pos];
  }

  public boolean isEmpty() {
    return levels.length == 0;
  }

  public int length() {
    return levels.length;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    Namespace namespace = (Namespace) other;
    return Arrays.equals(levels, namespace.levels);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(levels);
  }

  @Override
  public String toString() {
    return DOT.join(levels);
  }
}
