package com.netflix.hollow.api.producer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class WriteStateTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();
    @Mock
    private HollowWriteStateEngine writeStateEngine;
    @Mock
    private HollowObjectMapper objectMapper;

    private WriteStateImpl subject;

    @Before
    public void before() {
        when(objectMapper.getStateEngine()).thenReturn(writeStateEngine);

        subject = new WriteStateImpl(13L, objectMapper, null);
    }

    @Test
    public void add_delegatesToObjectMapper() {
        subject.add("Yes!");
        verify(objectMapper).add("Yes!");
    }

    @Test
    public void getObjectMapper() {
        assertEquals(objectMapper, subject.getObjectMapper());
    }

    @Test
    public void getStateEngine_delegatesToObjectMapper() throws Exception {
        assertEquals(writeStateEngine, subject.getStateEngine());
    }

    @Test
    public void add_whenSealed() throws Exception {
        subject.seal();

        try {
            subject.add("No!");
            fail("should throw");
        } catch (IllegalStateException e) {
            assertEquals("attempt to modify state after populate stage complete; version=13", e.getMessage());
        }
    }

    @Test
    public void getObjectMapper_whenSealed() {
        subject.seal();

        try {
            subject.getObjectMapper();
            fail("should throw");
        } catch (IllegalStateException e) {
            assertEquals("attempt to modify state after populate stage complete; version=13", e.getMessage());
        }
    }

    @Test
    public void getStateEngine_whenSealed() {
        subject.seal();
        assertEquals(writeStateEngine, subject.getStateEngine());
    }
}
