/*
 * Druid - a distributed column store.
 * Copyright 2012 - 2015 Metamarkets Group Inc.
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

package io.druid.query.metadata.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.Interval;

import java.util.List;
import java.util.Map;

public class SegmentAnalysis
{
  private final String id;
  private final List<Interval> interval;
  private final Map<String, ColumnAnalysis> columns;
  private final long size;

  @JsonCreator
  public SegmentAnalysis(
      @JsonProperty("id") String id,
      @JsonProperty("intervals") List<Interval> interval,
      @JsonProperty("columns") Map<String, ColumnAnalysis> columns,
      @JsonProperty("size") long size

  )
  {
    this.id = id;
    this.interval = interval;
    this.columns = columns;
    this.size = size;
  }

  @JsonProperty
  public String getId()
  {
    return id;
  }

  @JsonProperty
  public List<Interval> getIntervals()
  {
    return interval;
  }

  @JsonProperty
  public Map<String, ColumnAnalysis> getColumns()
  {
    return columns;
  }

  @JsonProperty
  public long getSize()
  {
    return size;
  }

  public String toDetailedString()
  {
    return "SegmentAnalysis{" +
           "id='" + id + '\'' +
           ", interval=" + interval +
           ", columns=" + columns +
           ", size=" + size +
           '}';
  }

  @Override
  public String toString()
  {
    return "SegmentAnalysis{" +
           "id='" + id + '\'' +
           ", interval=" + interval +
           ", size=" + size +
           '}';
  }
}
