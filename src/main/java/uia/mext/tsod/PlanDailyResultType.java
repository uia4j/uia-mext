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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The daily result of the plan
 *
 * @author Kyle K. Lin
 *
 */
public final class PlanDailyResultType {

    /**
     * The day index
     */
    public final int day;

    /**
     * The plan id
     */
    public final String planId;

    /**
     * The expected quantity
     */
    public final long expectedQuantity;

    /**
     * The actual quantity
     */
    public final long actualQuantity;

    /**
     * The assignment result
     */
    public final List<AssignmentType> assignments;

    /**
     * The constructor
     *
     * @param day The day index
     * @param planId The plan id
     * @param expectedQuantity The expected quantity
     * @param actualQuantity The actual quantity
     * @param assignments The assignments result
     */
    public PlanDailyResultType(int day, String planId, long expectedQuantity, long actualQuantity, List<AssignmentType> assignments) {
        this.day = day;
        this.planId = planId;
        this.expectedQuantity = expectedQuantity;
        this.actualQuantity = actualQuantity;
        this.assignments = Collections.unmodifiableList(new ArrayList<AssignmentType>(assignments));
    }
}
