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

package io.druid.query.filter;

import com.metamx.collections.bitmap.BitmapFactory;
import com.metamx.collections.bitmap.ImmutableBitmap;
import com.metamx.collections.spatial.ImmutableRTree;
import io.druid.segment.data.Indexed;

/**
 */
public interface BitmapIndexSelector
{
  public Indexed<String> getDimensionValues(String dimension);
  public int getNumRows();
  public BitmapFactory getBitmapFactory();
  public ImmutableBitmap getBitmapIndex(String dimension, String value);
  public ImmutableBitmap getBitmapIndex(String dimension, int idx);
  public ImmutableRTree getSpatialIndex(String dimension);
}
