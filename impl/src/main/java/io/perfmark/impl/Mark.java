/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.perfmark.impl;

import java.util.Arrays;
import javax.annotation.Nullable;

public final class Mark {
  // TODO: make sure these match the values in Impl
  public static final String NO_TAG_NAME = "";
  public static final long NO_TAG_ID = Long.MIN_VALUE;
  public static final long NO_LINK_ID = Long.MIN_VALUE;

  public static final long NO_NANOTIME = 0;

  @Nullable private final String taskName;
  @Nullable private final Marker marker;
  @Nullable private final String tagName;
  private final long tagId;
  private final long linkId;
  private final long nanoTime;
  private final long generation;
  private final Operation operation;

  public static Mark create(
      String taskName,
      Marker marker,
      @Nullable String tagName,
      long tagId,
      long nanoTime,
      long generation,
      Operation operation) {
    return new Mark(taskName, marker, tagName, tagId, nanoTime, generation, operation);
  }

  private Mark(
      String taskName,
      Marker marker,
      @Nullable String tagName,
      long tagId,
      long nanoTime,
      long generation,
      Operation operation) {
    this.operation = checkNotNull(operation, "operation");
    if (operation == Operation.NONE) {
      throw new IllegalArgumentException("bad operation");
    }
    markerCheck:
    {
      switch (operation) {
        case TASK_START_M: // fall-through
        case TASK_START_TM: // fall-through
        case TASK_END_M: // fall-through
        case TASK_END_TM: // fall-through
        case EVENT_M: // fall-through
        case EVENT_TM: // fall-through
        case LINK_M:
          this.marker = marker;
          break markerCheck;
        case TASK_START: // fall-through
        case TASK_START_T: // fall-through
        case TASK_END: // fall-through
        case TASK_END_T: // fall-through
        case EVENT: // fall-through
        case EVENT_T: // fall-through
        case LINK: // fall-through
        case ATTACH_TAG:
          this.marker = Marker.NONE;
          break markerCheck;
        case NONE:
          // fall-through
      }
      throw new AssertionError(String.valueOf(operation));
    }

    tagCheck:
    {
      switch (operation) {
        case TASK_START_T: // fall-through
        case TASK_START_TM: // fall-through
        case TASK_END_T: // fall-through
        case TASK_END_TM: // fall-through
        case EVENT_T:
        case EVENT_TM:
        case ATTACH_TAG:
          this.tagName = tagName;
          this.tagId = tagId;
          break tagCheck;
        case TASK_START: // fall-through
        case TASK_START_M: // fall-through
        case TASK_END: // fall-through
        case TASK_END_M: // fall-through
        case EVENT: // fall-through
        case EVENT_M: // fall-through
        case LINK: // fall-through
        case LINK_M:
          this.tagName = NO_TAG_NAME;
          this.tagId = NO_TAG_ID;
          break tagCheck;
        case NONE:
          // fall-through
      }
      throw new AssertionError(String.valueOf(operation));
    }
    if (operation == Operation.LINK || operation == Operation.LINK_M) {
      this.taskName = null;
      this.nanoTime = NO_NANOTIME;
      this.linkId = tagId;
    } else if (operation == Operation.ATTACH_TAG) {
      this.taskName = null;
      this.nanoTime = NO_NANOTIME;
      this.linkId = NO_LINK_ID;
    } else {
      this.taskName = taskName;
      this.nanoTime = nanoTime;
      this.linkId = NO_LINK_ID;
    }
    this.generation = generation;
  }

  public enum Operation {
    NONE,
    TASK_START,
    TASK_START_T,
    TASK_START_M,
    TASK_START_TM,
    TASK_END,
    TASK_END_T,
    TASK_END_M,
    TASK_END_TM,
    EVENT,
    EVENT_T,
    EVENT_M,
    EVENT_TM,
    LINK,
    LINK_M,
    ATTACH_TAG,
    ;

    private static final Operation[] values = Operation.values();

    static {
      assert values.length <= (1 << Generator.GEN_OFFSET);
    }

    public static Operation valueOf(int ordinal) {
      return values[ordinal];
    }
  }

  public long getNanoTime() {
    return nanoTime;
  }

  public long getGeneration() {
    return generation;
  }

  public Operation getOperation() {
    return operation;
  }

  @Nullable
  public String getTagName() {
    return tagName;
  }

  public long getTagId() {
    return tagId;
  }

  @Nullable
  public Marker getMarker() {
    return marker;
  }

  @Nullable
  public String getTaskName() {
    return taskName;
  }

  public long getLinkId() {
    return linkId;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Mark)) {
      return false;
    }
    Mark that = (Mark) obj;
    return equal(this.taskName, that.taskName)
        && equal(this.marker, that.marker)
        && equal(this.tagName, that.tagName)
        && this.tagId == that.tagId
        && this.linkId == that.linkId
        && this.nanoTime == that.nanoTime
        && this.operation == that.operation;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(
        new Object[] {taskName, tagName, tagId, linkId, marker, nanoTime, operation});
  }

  @Override
  public String toString() {
    return "Mark{"
        + "taskName="
        + taskName
        + ", "
        + "tagName="
        + tagName
        + ", "
        + "tagId="
        + tagId
        + ", "
        + "linkId="
        + linkId
        + ", "
        + "marker="
        + marker
        + ", "
        + "nanoTime="
        + nanoTime
        + ", "
        + "generation="
        + generation
        + ", "
        + "operation="
        + operation
        + "}";
  }

  private static <T> T checkNotNull(T t, String name) {
    if (t == null) {
      throw new NullPointerException(name + " is null");
    }
    return t;
  }

  static <T> boolean equal(T a, T b) {
    return a == b || a.equals(b);
  }
}
