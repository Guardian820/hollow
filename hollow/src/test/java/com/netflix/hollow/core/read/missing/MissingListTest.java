/*
 *
 *  Copyright 2016 Netflix, Inc.
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
package com.netflix.hollow.core.read.missing;

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.generic.GenericHollowList;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import java.io.IOException;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;

public class MissingListTest extends AbstractStateEngineTest {

    @Test
    public void testCompletelyMissingList() throws IOException {
        roundTripSnapshot();
        readStateEngine.setMissingDataHandler(new FakeMissingDataHandler());


        GenericHollowList list = (GenericHollowList) GenericHollowRecordHelper.instantiate(readStateEngine, "MissingList", 0);

        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(new FakeMissingHollowRecord(new HollowObjectMissingDataAccess(readStateEngine, "MissingObject"), 2)));
        Assert.assertFalse(list.contains(new FakeMissingHollowRecord(new HollowObjectMissingDataAccess(readStateEngine, "MissingObject"), 0)));

        Iterator<HollowRecord> rec = list.iterator();

        Assert.assertTrue(rec.hasNext());
        HollowRecord next = rec.next();
        Assert.assertEquals(2, next.getOrdinal());
        Assert.assertEquals("MissingObject", next.getSchema().getName());
        Assert.assertTrue(rec.hasNext());
        next = rec.next();
        Assert.assertEquals(3, next.getOrdinal());
        Assert.assertEquals("MissingObject", next.getSchema().getName());
        Assert.assertFalse(rec.hasNext());
    }

    private class FakeMissingDataHandler extends DefaultMissingDataHandler {
        @Override
        public HollowSchema handleSchema(String type) {
            if("MissingList".equals(type))
                return new HollowListSchema("MissingList", "MissingObject");
            if("MissingObject".equals(type))
                return new HollowObjectSchema("MissingObject", 0);
            return null;
        }

        @Override
        public int handleListSize(String type, int ordinal) {
            return 2;
        }

        @Override
        public int handleListElementOrdinal(String type, int ordinal, int idx) {
            return idx + 2;
        }

        @Override
        public HollowOrdinalIterator handleListIterator(String type, int ordinal) {
            return new HollowOrdinalIterator() {
                private final int ordinals[] = {2, 3};
                private int currentOrdinal = 0;

                @Override
                public int next() {
                    if(currentOrdinal >= ordinals.length)
                        return NO_MORE_ORDINALS;
                    return ordinals[currentOrdinal++];
                }
            };
        }

    }


    @Override
    protected void initializeTypeStates() { }
}
