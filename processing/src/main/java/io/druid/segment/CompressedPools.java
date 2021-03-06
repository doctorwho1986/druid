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

package io.druid.segment;

import com.google.common.base.Supplier;
import com.metamx.common.logger.Logger;
import com.ning.compress.BufferRecycler;
import io.druid.collections.ResourceHolder;
import io.druid.collections.StupidPool;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicLong;

/**
 */
public class CompressedPools
{
  private static final Logger log = new Logger(CompressedPools.class);

  public static final int BUFFER_SIZE = 0x10000;
  private static final StupidPool<BufferRecycler> bufferRecyclerPool = new StupidPool<BufferRecycler>(
      new Supplier<BufferRecycler>()
      {
        private final AtomicLong counter = new AtomicLong(0);

        @Override
        public BufferRecycler get()
        {
          log.info("Allocating new bufferRecycler[%,d]", counter.incrementAndGet());
          return new BufferRecycler();
        }
      }
  );

  public static ResourceHolder<BufferRecycler> getBufferRecycler()
  {
    return bufferRecyclerPool.take();
  }

  private static final StupidPool<byte[]> outputBytesPool = new StupidPool<byte[]>(
      new Supplier<byte[]>()
      {
        private final AtomicLong counter = new AtomicLong(0);

        @Override
        public byte[] get()
        {
          log.info("Allocating new outputBytesPool[%,d]", counter.incrementAndGet());
          return new byte[BUFFER_SIZE];
        }
      }
  );

  public static ResourceHolder<byte[]> getOutputBytes()
  {
    return outputBytesPool.take();
  }

  private static final StupidPool<ByteBuffer> bigEndByteBufPool = new StupidPool<ByteBuffer>(
      new Supplier<ByteBuffer>()
      {
        private final AtomicLong counter = new AtomicLong(0);

        @Override
        public ByteBuffer get()
        {
          log.info("Allocating new bigEndByteBuf[%,d]", counter.incrementAndGet());
          return ByteBuffer.allocateDirect(BUFFER_SIZE).order(ByteOrder.BIG_ENDIAN);
        }
      }
  );

  private static final StupidPool<ByteBuffer> littleEndByteBufPool = new StupidPool<ByteBuffer>(
      new Supplier<ByteBuffer>()
      {
        private final AtomicLong counter = new AtomicLong(0);

        @Override
        public ByteBuffer get()
        {
          log.info("Allocating new littleEndByteBuf[%,d]", counter.incrementAndGet());
          return ByteBuffer.allocateDirect(BUFFER_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        }
      }
  );

  public static ResourceHolder<ByteBuffer> getByteBuf(ByteOrder order)
  {
    if (order == ByteOrder.LITTLE_ENDIAN) {
      return littleEndByteBufPool.take();
    }
    return bigEndByteBufPool.take();
  }
}
