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

package io.druid.query;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.metamx.common.guava.Sequence;
import com.metamx.common.guava.Sequences;
import com.metamx.common.logger.Logger;
import io.druid.granularity.QueryGranularity;
import io.druid.query.aggregation.Aggregator;
import io.druid.query.aggregation.AggregatorFactory;
import io.druid.query.filter.Filter;
import io.druid.segment.Cursor;
import io.druid.segment.StorageAdapter;
import org.joda.time.Interval;

import javax.annotation.Nullable;
import java.util.List;

/**
 */
public class QueryRunnerHelper
{
  private static final Logger log = new Logger(QueryRunnerHelper.class);

  public static Aggregator[] makeAggregators(Cursor cursor, List<AggregatorFactory> aggregatorSpecs)
  {
    Aggregator[] aggregators = new Aggregator[aggregatorSpecs.size()];
    int aggregatorIndex = 0;
    for (AggregatorFactory spec : aggregatorSpecs) {
      aggregators[aggregatorIndex] = spec.factorize(cursor);
      ++aggregatorIndex;
    }
    return aggregators;
  }

  public static <T> Sequence<Result<T>> makeCursorBasedQuery(
      final StorageAdapter adapter,
      List<Interval> queryIntervals,
      Filter filter,
      QueryGranularity granularity,
      final Function<Cursor, Result<T>> mapFn
  )
  {
    Preconditions.checkArgument(
        queryIntervals.size() == 1, "Can only handle a single interval, got[%s]", queryIntervals
    );

    return Sequences.filter(
        Sequences.map(
            adapter.makeCursors(filter, queryIntervals.get(0), granularity),
            new Function<Cursor, Result<T>>()
            {
              @Override
              public Result<T> apply(Cursor input)
              {
                log.debug("Running over cursor[%s]", adapter.getInterval(), input.getTime());
                return mapFn.apply(input);
              }
            }
        ),
        Predicates.<Result<T>>notNull()
    );
  }
}
