/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iceberg;

import org.apache.iceberg.expressions.Expression;
import org.apache.iceberg.expressions.Expressions;
import org.apache.iceberg.relocated.com.google.common.base.Preconditions;
import org.apache.iceberg.util.CharSequenceSet;

class BaseRowDelta extends MergingSnapshotProducer<RowDelta> implements RowDelta {
  private Long startingSnapshotId = null; // check all versions by default
  private final CharSequenceSet referencedDataFiles = CharSequenceSet.empty();
  private boolean validateDeletes = false;
  private Expression conflictDetectionFilter = Expressions.alwaysTrue();
  private boolean validateNewDataFiles = false;
  private boolean validateNewDeleteFiles = false;

  BaseRowDelta(String tableName, TableOperations ops) {
    super(tableName, ops);
  }

  @Override
  protected BaseRowDelta self() {
    return this;
  }

  @Override
  protected String operation() {
    return DataOperations.OVERWRITE;
  }

  @Override
  public RowDelta addRows(DataFile inserts) {
    add(inserts);
    return this;
  }

  @Override
  public RowDelta addDeletes(DeleteFile deletes) {
    add(deletes);
    return this;
  }

  @Override
  public RowDelta validateFromSnapshot(long snapshotId) {
    this.startingSnapshotId = snapshotId;
    return this;
  }

  @Override
  public RowDelta validateDeletedFiles() {
    this.validateDeletes = true;
    return this;
  }

  @Override
  public RowDelta validateDataFilesExist(Iterable<? extends CharSequence> referencedFiles) {
    referencedFiles.forEach(referencedDataFiles::add);
    return this;
  }

  @Override
  public RowDelta conflictDetectionFilter(Expression newConflictDetectionFilter) {
    Preconditions.checkArgument(
        newConflictDetectionFilter != null, "Conflict detection filter cannot be null");
    this.conflictDetectionFilter = newConflictDetectionFilter;
    return this;
  }

  @Override
  public RowDelta validateNoConflictingDataFiles() {
    this.validateNewDataFiles = true;
    return this;
  }

  @Override
  public RowDelta validateNoConflictingDeleteFiles() {
    this.validateNewDeleteFiles = true;
    return this;
  }

  @Override
  protected void validate(TableMetadata base, Snapshot snapshot) {
    if (base.currentSnapshot() != null) {
      if (!referencedDataFiles.isEmpty()) {
        validateDataFilesExist(
            base,
            startingSnapshotId,
            referencedDataFiles,
            !validateDeletes,
            conflictDetectionFilter);
      }

      if (validateNewDataFiles) {
        validateAddedDataFiles(base, startingSnapshotId, conflictDetectionFilter);
      }

      if (validateNewDeleteFiles) {
        validateNoNewDeleteFiles(base, startingSnapshotId, conflictDetectionFilter);
      }
    }
  }
}
