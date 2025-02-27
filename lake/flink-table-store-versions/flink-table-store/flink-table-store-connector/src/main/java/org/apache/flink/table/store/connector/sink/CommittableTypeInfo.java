/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.store.connector.sink;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.core.io.SimpleVersionedSerializerTypeSerializerProxy;

/** Type information of {@link Committable}. */
public class CommittableTypeInfo extends TypeInformation<Committable> {

    @Override
    public boolean isBasicType() {
        return false;
    }

    @Override
    public boolean isTupleType() {
        return false;
    }

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public int getTotalFields() {
        return 1;
    }

    @Override
    public Class<Committable> getTypeClass() {
        return Committable.class;
    }

    @Override
    public boolean isKeyType() {
        return false;
    }

    @Override
    public TypeSerializer<Committable> createSerializer(ExecutionConfig config) {
        // no copy, so that data from writer is directly going into committer while chaining
        return new SimpleVersionedSerializerTypeSerializerProxy<Committable>(
                () -> new CommittableSerializer(new FileCommittableSerializer())) {
            @Override
            public Committable copy(Committable from) {
                return from;
            }

            @Override
            public Committable copy(Committable from, Committable reuse) {
                return from;
            }
        };
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean canEqual(Object obj) {
        return obj instanceof CommittableTypeInfo;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CommittableTypeInfo;
    }

    @Override
    public String toString() {
        return "CommittableTypeInfo";
    }
}
