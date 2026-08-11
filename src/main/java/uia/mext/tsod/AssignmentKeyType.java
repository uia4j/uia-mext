/*******************************************************************************
 * Copyright 2026 UIA
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package uia.mext.tsod;

/**
 * The assignment key.
 *
 * x[batchIndex, planIndex, day]
 *
 * @author Kyle K. Lin
 *
 */
public final class AssignmentKeyType {

    /**
     * The batch index
     */
    public final int batchIndex;

    /**
     * The plan index
     */
    public final int planIndex;

    /**
     * The day index
     */
    public final int day;

    /**
     * The constructor
     *
     * @param batchIndex The batch index
     * @param planIndex The plan index
     * @param day The day index
     */
    public AssignmentKeyType(int batchIndex, int planIndex, int day) {
        this.batchIndex = batchIndex;
        this.planIndex = planIndex;
        this.day = day;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof AssignmentKeyType)) {
            return false;
        }

        AssignmentKeyType other = (AssignmentKeyType) obj;

        return this.batchIndex == other.batchIndex
                && this.planIndex == other.planIndex
                && this.day == other.day;
    }

    @Override
    public int hashCode() {
        int result = this.batchIndex;
        result = 31 * result + this.planIndex;
        result = 31 * result + this.day;
        return result;
    }
}
