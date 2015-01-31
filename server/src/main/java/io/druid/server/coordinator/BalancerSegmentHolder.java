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

package io.druid.server.coordinator;

import io.druid.client.ImmutableDruidServer;
import io.druid.timeline.DataSegment;

/**
 */
public class BalancerSegmentHolder
{
  private final ImmutableDruidServer fromServer;
  private final DataSegment segment;

  // This is a pretty fugly hard coding of the maximum lifetime
  private volatile int lifetime = 15;

  public BalancerSegmentHolder(
      ImmutableDruidServer fromServer,
      DataSegment segment
  )
  {
    this.fromServer = fromServer;
    this.segment = segment;
  }

  public ImmutableDruidServer getFromServer()
  {
    return fromServer;
  }

  public DataSegment getSegment()
  {
    return segment;
  }

  public int getLifetime()
  {
    return lifetime;
  }

  public void reduceLifetime()
  {
    lifetime--;
  }
}
