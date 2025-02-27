/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.store.spark;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.store.table.FileStoreTable;
import org.apache.flink.table.store.table.FileStoreTableFactory;

import org.apache.spark.sql.sources.DataSourceRegister;
import org.apache.spark.sql.sources.v2.DataSourceOptions;
import org.apache.spark.sql.sources.v2.ReadSupport;
import org.apache.spark.sql.sources.v2.reader.DataSourceReader;
import org.apache.spark.sql.types.StructType;

/** The Spark source for table store. */
public class SparkSource implements DataSourceRegister, ReadSupport {

    @Override
    public String shortName() {
        // Not use 'table-store' here, the '-' is not allowed in SQL
        return "tablestore";
    }

    @Override
    public DataSourceReader createReader(StructType schema, DataSourceOptions options) {
        return createReader(options);
    }

    @Override
    public DataSourceReader createReader(DataSourceOptions options) {
        FileStoreTable table = FileStoreTableFactory.create(Configuration.fromMap(options.asMap()));
        return new SparkDataSourceReader(table);
    }
}
