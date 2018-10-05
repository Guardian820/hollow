package com.netflix.hollow.api.producer;

import static java.lang.String.format;

public class SealedWriteStateException extends IllegalArgumentException {
    private static final String LATE_MODIFICATION_MESSAGE =
            "attempt to modify state after populate stage complete; version=%d";

    SealedWriteStateException(SealableWriteState state) {
        super(format(LATE_MODIFICATION_MESSAGE, state.getVersion()));
    }
}
