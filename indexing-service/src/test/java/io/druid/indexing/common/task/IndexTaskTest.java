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

package io.druid.indexing.common.task;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.metamx.common.Granularity;
import io.druid.data.input.impl.CSVParseSpec;
import io.druid.data.input.impl.DimensionsSpec;
import io.druid.data.input.impl.SpatialDimensionSchema;
import io.druid.data.input.impl.StringInputRowParser;
import io.druid.data.input.impl.TimestampSpec;
import io.druid.granularity.QueryGranularity;
import io.druid.indexing.common.TaskLock;
import io.druid.indexing.common.TaskToolbox;
import io.druid.indexing.common.actions.LockListAction;
import io.druid.indexing.common.actions.TaskAction;
import io.druid.indexing.common.actions.TaskActionClient;
import io.druid.indexing.common.actions.TaskActionClientFactory;
import io.druid.jackson.DefaultObjectMapper;
import io.druid.query.aggregation.AggregatorFactory;
import io.druid.query.aggregation.LongSumAggregatorFactory;
import io.druid.segment.indexing.DataSchema;
import io.druid.segment.indexing.granularity.UniformGranularitySpec;
import io.druid.segment.loading.DataSegmentPusher;
import io.druid.segment.realtime.firehose.LocalFirehoseFactory;
import io.druid.timeline.DataSegment;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class IndexTaskTest
{
  @Test
  public void testDeterminePartitions() throws Exception
  {
    File tmpDir = Files.createTempDir();
    tmpDir.deleteOnExit();

    File tmpFile = File.createTempFile("druid", "index", tmpDir);
    tmpFile.deleteOnExit();

    PrintWriter writer = new PrintWriter(tmpFile);
    writer.println("2014-01-01T00:00:10Z,a,1");
    writer.println("2014-01-01T01:00:20Z,b,1");
    writer.println("2014-01-01T02:00:30Z,c,1");
    writer.close();

    IndexTask indexTask = new IndexTask(
        null,
        new IndexTask.IndexIngestionSpec(
            new DataSchema(
                "test",
                new StringInputRowParser(
                    new CSVParseSpec(
                        new TimestampSpec(
                            "ts",
                            "auto"
                        ),
                        new DimensionsSpec(
                            Arrays.asList("ts"),
                            Lists.<String>newArrayList(),
                            Lists.<SpatialDimensionSchema>newArrayList()
                        ),
                        null,
                        Arrays.asList("ts", "dim", "val")
                    )
                ),
                new AggregatorFactory[]{
                    new LongSumAggregatorFactory("val", "val")
                },
                new UniformGranularitySpec(
                    Granularity.DAY,
                    QueryGranularity.MINUTE,
                    Arrays.asList(new Interval("2014/2015"))
                )
            ),
            new IndexTask.IndexIOConfig(
                new LocalFirehoseFactory(
                    tmpDir,
                    "druid*",
                    null
                )
            ),
            new IndexTask.IndexTuningConfig(
                2,
                0,
                null
            )
        ),
        new DefaultObjectMapper()
    );

    final List<DataSegment> segments = Lists.newArrayList();

    indexTask.run(
        new TaskToolbox(
            null, null, new TaskActionClientFactory()
        {
          @Override
          public TaskActionClient create(Task task)
          {
            return new TaskActionClient()
            {
              @Override
              public <RetType> RetType submit(TaskAction<RetType> taskAction) throws IOException
              {
                if (taskAction instanceof LockListAction) {
                  return (RetType) Arrays.asList(
                      new TaskLock(
                          "", "", null, new DateTime().toString()
                      )
                  );
                }
                return null;
              }
            };
          }
        }, null, new DataSegmentPusher()
        {
          @Override
          public String getPathForHadoop(String dataSource)
          {
            return null;
          }

          @Override
          public DataSegment push(File file, DataSegment segment) throws IOException
          {
            segments.add(segment);
            return segment;
          }
        }, null, null, null, null, null, null, null, null, null, null, null
        )
    );

    Assert.assertEquals(2, segments.size());
  }
}
