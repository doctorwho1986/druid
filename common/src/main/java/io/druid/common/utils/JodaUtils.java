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

package io.druid.common.utils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.metamx.common.guava.Comparators;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 */
public class JodaUtils
{
  // joda limits years to [-292275054,292278993] that should be reasonable
  public static final long MAX_INSTANT = new DateTime("292278993").getMillis();
  public static final long MIN_INSTANT = new DateTime("-292275054").getMillis();

  public static ArrayList<Interval> condenseIntervals(Iterable<Interval> intervals)
  {
    ArrayList<Interval> retVal = Lists.newArrayList();

    TreeSet<Interval> sortedIntervals = Sets.newTreeSet(Comparators.intervalsByStartThenEnd());
    for (Interval interval : intervals) {
      sortedIntervals.add(interval);
    }

    if (sortedIntervals.isEmpty()) {
      return Lists.newArrayList();
    }

    Iterator<Interval> intervalsIter = sortedIntervals.iterator();
    Interval currInterval = intervalsIter.next();
    while (intervalsIter.hasNext()) {
      Interval next = intervalsIter.next();

      if (currInterval.overlaps(next) || currInterval.abuts(next)) {
        currInterval = new Interval(currInterval.getStart(), next.getEnd());
      } else {
        retVal.add(currInterval);
        currInterval = next;
      }
    }
    retVal.add(currInterval);

    return retVal;
  }

  public static Interval umbrellaInterval(Iterable<Interval> intervals)
  {
    ArrayList<DateTime> startDates = Lists.newArrayList();
    ArrayList<DateTime> endDates = Lists.newArrayList();

    for (Interval interval : intervals) {
      startDates.add(interval.getStart());
      endDates.add(interval.getEnd());
    }

    DateTime minStart = minDateTime(startDates.toArray(new DateTime[]{}));
    DateTime maxEnd = maxDateTime(endDates.toArray(new DateTime[]{}));

    if (minStart == null || maxEnd == null) {
      throw new IllegalArgumentException("Empty list of intervals");
    }
    return new Interval(minStart, maxEnd);
  }

  public static boolean overlaps(final Interval i, Iterable<Interval> intervals)
  {
    return Iterables.any(
        intervals, new Predicate<Interval>()
    {
      @Override
      public boolean apply(Interval input)
      {
        return input.overlaps(i);
      }
    }
    );

  }

  public static DateTime minDateTime(DateTime... times)
  {
    if (times == null) {
      return null;
    }

    switch (times.length) {
      case 0:
        return null;
      case 1:
        return times[0];
      default:
        DateTime min = times[0];
        for (int i = 1; i < times.length; ++i) {
          min = min.isBefore(times[i]) ? min : times[i];
        }
        return min;
    }
  }

  public static DateTime maxDateTime(DateTime... times)
  {
    if (times == null) {
      return null;
    }

    switch (times.length) {
      case 0:
        return null;
      case 1:
        return times[0];
      default:
        DateTime max = times[0];
        for (int i = 1; i < times.length; ++i) {
          max = max.isAfter(times[i]) ? max : times[i];
        }
        return max;
    }
  }
}
