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
 * The assignment information
 *
 * @author Kyle K. Lin
 *
 */
public final class AssignmentType {

    /**
     * The delivery day
     */
    public final int day;

    /**
     * The plan id
     */
    public final String planId;

    /**
     * The batch id
     */
    public final String batchId;

    /**
     * The quantity which this day deliveries
     */
    public final long quantity;

    /**
     * The constructor.
     *
     * @param day The delivery day
     * @param planId The plan id
     * @param batchId The batch id
     * @param quantity The quantity which this day deliveries
     */
    public AssignmentType(int day, String planId, String batchId, long quantity) {
        this.day = day;
        this.planId = planId;
        this.batchId = batchId;
        this.quantity = quantity;
    }
}
