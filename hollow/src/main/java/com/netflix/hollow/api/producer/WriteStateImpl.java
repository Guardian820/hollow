/*
 *
 *  Copyright 2017 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.api.producer;

import static java.lang.String.format;

import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;

/**
 * Beta API subject to change.
 *
 * @author Tim Taylor {@literal<tim@toolbear.io>}
 */
final class WriteStateImpl implements HollowProducer.WriteState {
    private static final String LATE_MODIFICATION_MESSAGE =
            "attempt to modify state after populate stage complete; version=%d";
    private final long version;
    private HollowObjectMapper objectMapper;
    private HollowProducer.ReadState priorReadState;
    private volatile boolean sealed = false;

    protected WriteStateImpl(long version, HollowObjectMapper objectMapper, HollowProducer.ReadState priorReadState) {
        this.version = version;
        this.objectMapper = objectMapper;
        this.priorReadState = priorReadState;
    }

    @Override
    public int add(Object o) {
        if (sealed)
            throw new IllegalStateException(format(LATE_MODIFICATION_MESSAGE, version));
        return objectMapper.add(o);
    }

    @Override
    public HollowObjectMapper getObjectMapper() {
        if (sealed)
            throw new IllegalStateException(format(LATE_MODIFICATION_MESSAGE, version));
        return objectMapper;
    }

    @Override
    public HollowWriteStateEngine getStateEngine() {
        return objectMapper.getStateEngine();
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public HollowProducer.ReadState getPriorState() {
        return priorReadState;
    }

    void seal() {
        sealed = true;
    }
}
