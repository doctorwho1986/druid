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

import io.druid.query.aggregation.AggregatorFactory;
import io.druid.query.aggregation.CountAggregatorFactory;
import io.druid.query.aggregation.DoubleSumAggregatorFactory;
import io.druid.query.aggregation.PostAggregator;
import io.druid.query.aggregation.post.ArithmeticPostAggregator;
import io.druid.query.aggregation.post.ConstantPostAggregator;
import io.druid.query.aggregation.post.FieldAccessPostAggregator;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 */
public class QueriesTest
{
  @Test
  public void testVerifyAggregations() throws Exception
  {
    List<AggregatorFactory> aggFactories = Arrays.<AggregatorFactory>asList(
        new CountAggregatorFactory("count"),
        new DoubleSumAggregatorFactory("idx", "index"),
        new DoubleSumAggregatorFactory("rev", "revenue")
    );

    List<PostAggregator> postAggs = Arrays.<PostAggregator>asList(
        new ArithmeticPostAggregator(
            "addStuff",
            "+",
            Arrays.<PostAggregator>asList(
                new FieldAccessPostAggregator("idx", "idx"),
                new FieldAccessPostAggregator("count", "count")
            )
        )
    );

    boolean exceptionOccured = false;

    try {
      Queries.verifyAggregations(aggFactories, postAggs);
    }
    catch (IllegalArgumentException e) {
      exceptionOccured = true;
    }

    Assert.assertFalse(exceptionOccured);
  }

  @Test
  public void testVerifyAggregationsMissingVal() throws Exception
  {
    List<AggregatorFactory> aggFactories = Arrays.<AggregatorFactory>asList(
        new CountAggregatorFactory("count"),
        new DoubleSumAggregatorFactory("idx", "index"),
        new DoubleSumAggregatorFactory("rev", "revenue")
    );

    List<PostAggregator> postAggs = Arrays.<PostAggregator>asList(
        new ArithmeticPostAggregator(
            "addStuff",
            "+",
            Arrays.<PostAggregator>asList(
                new FieldAccessPostAggregator("idx", "idx2"),
                new FieldAccessPostAggregator("count", "count")
            )
        )
    );

    boolean exceptionOccured = false;

    try {
      Queries.verifyAggregations(aggFactories, postAggs);
    }
    catch (IllegalArgumentException e) {
      exceptionOccured = true;
    }

    Assert.assertTrue(exceptionOccured);
  }

  @Test
  public void testVerifyAggregationsMultiLevel() throws Exception
  {
    List<AggregatorFactory> aggFactories = Arrays.<AggregatorFactory>asList(
        new CountAggregatorFactory("count"),
        new DoubleSumAggregatorFactory("idx", "index"),
        new DoubleSumAggregatorFactory("rev", "revenue")
    );

    List<PostAggregator> postAggs = Arrays.<PostAggregator>asList(
        new ArithmeticPostAggregator(
            "divideStuff",
            "/",
            Arrays.<PostAggregator>asList(
                new ArithmeticPostAggregator(
                    "addStuff",
                    "+",
                    Arrays.asList(
                        new FieldAccessPostAggregator("idx", "idx"),
                        new ConstantPostAggregator("const", 1)
                    )
                ),
                new ArithmeticPostAggregator(
                    "subtractStuff",
                    "-",
                    Arrays.asList(
                        new FieldAccessPostAggregator("rev", "rev"),
                        new ConstantPostAggregator("const", 1)
                    )
                )
            )
        ),
        new ArithmeticPostAggregator(
            "addStuff",
            "+",
            Arrays.<PostAggregator>asList(
                new FieldAccessPostAggregator("divideStuff", "divideStuff"),
                new FieldAccessPostAggregator("count", "count")
            )
        )
    );

    boolean exceptionOccured = false;

    try {
      Queries.verifyAggregations(aggFactories, postAggs);
    }
    catch (IllegalArgumentException e) {
      exceptionOccured = true;
    }

    Assert.assertFalse(exceptionOccured);
  }

  @Test
  public void testVerifyAggregationsMultiLevelMissingVal() throws Exception
  {
    List<AggregatorFactory> aggFactories = Arrays.<AggregatorFactory>asList(
        new CountAggregatorFactory("count"),
        new DoubleSumAggregatorFactory("idx", "index"),
        new DoubleSumAggregatorFactory("rev", "revenue")
    );

    List<PostAggregator> postAggs = Arrays.<PostAggregator>asList(
        new ArithmeticPostAggregator(
            "divideStuff",
            "/",
            Arrays.<PostAggregator>asList(
                new ArithmeticPostAggregator(
                    "addStuff",
                    "+",
                    Arrays.asList(
                        new FieldAccessPostAggregator("idx", "idx"),
                        new ConstantPostAggregator("const", 1)
                    )
                ),
                new ArithmeticPostAggregator(
                    "subtractStuff",
                    "-",
                    Arrays.asList(
                        new FieldAccessPostAggregator("rev", "rev2"),
                        new ConstantPostAggregator("const", 1)
                    )
                )
            )
        ),
        new ArithmeticPostAggregator(
            "addStuff",
            "+",
            Arrays.<PostAggregator>asList(
                new FieldAccessPostAggregator("divideStuff", "divideStuff"),
                new FieldAccessPostAggregator("count", "count")
            )
        )
    );

    boolean exceptionOccured = false;

    try {
      Queries.verifyAggregations(aggFactories, postAggs);
    }
    catch (IllegalArgumentException e) {
      exceptionOccured = true;
    }

    Assert.assertTrue(exceptionOccured);
  }
}
