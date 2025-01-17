/*
 * Copyright (C) 2022 Adam Powell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.slack.circuit.backstack

import androidx.compose.runtime.Stable
import com.slack.circuit.backstack.BackStack.Record
import com.slack.circuit.runtime.screen.Screen

/**
 * A caller-supplied stack of [Record]s for presentation with a `Navigator`. Iteration order is
 * top-first (first element is the top of the stack).
 */
@Stable
public interface BackStack<R : Record> : Iterable<R> {
  /** The number of records contained in this [BackStack] that will be seen by an iterator. */
  public val size: Int

  /** The top-most record in the [BackStack], or `null` if the [BackStack] is empty. */
  public val topRecord: R?

  /** Push a new [Record] onto the back stack. The new record will become the top of the stack. */
  public fun push(record: R)

  /**
   * Push a new [Screen] onto the back stack. This will be enveloped in a [Record] and the new
   * record will become the top of the stack.
   */
  public fun push(screen: Screen)

  /**
   * Attempt to pop the top item off of the back stack, returning the popped [Record] if popping was
   * successful or `null` if no entry was popped.
   */
  public fun pop(): R?

  /**
   * Pop records off the top of the backstack until one is found that matches the given predicate.
   */
  public fun popUntil(predicate: (R) -> Boolean) {
    while (topRecord?.let(predicate) == false) pop()
  }

  public interface Record {
    /**
     * A value that identifies this record uniquely, even if it shares the same [screen] with
     * another record. This key may be used by [BackStackRecordLocalProvider]s to associate
     * presentation data with a record across composition recreation.
     *
     * [key] MUST NOT change for the life of the record.
     */
    public val key: String

    /** The [Screen] that should present this record. */
    public val screen: Screen
  }
}

/** `true` if the [BackStack] contains no records. [BackStack.firstOrNull] will return `null`. */
public val BackStack<out Record>.isEmpty: Boolean
  get() = size == 0

/** `true` if the [BackStack] contains exactly one record. */
public val BackStack<out Record>.isAtRoot: Boolean
  get() = size == 1
