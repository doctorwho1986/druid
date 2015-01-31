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

package io.druid.server.log;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.metamx.emitter.service.ServiceEmitter;

import javax.validation.constraints.NotNull;

/**
 */
@JsonTypeName("emitter")
public class EmittingRequestLoggerProvider implements RequestLoggerProvider
{
  @JsonProperty
  @NotNull
  private String feed = null;

  @JacksonInject
  @NotNull
  private ServiceEmitter emitter = null;

  @Inject
  public void injectMe(Injector injector)
  {
  }

  @Override
  public RequestLogger get()
  {
    return new EmittingRequestLogger(emitter, feed);
  }
}
