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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.ortools.sat.CpSolverStatus;

/**
 * The result of problem.
 *
 * @author Kyle K. Lin
 *
 */
public final class SolvedType {

    /**
     * The max day index
     */
    public final int maxDay;

    /**
     * The status of the result.
     */
    public final CpSolverStatus status;

    /**
     * The object values
     */
    public final double objectiveValue;

    /**
     * Results of all plan-day
     */
    public final List<PlanDailyResultType> dailyResults;

    /**
     * The scheduled date of batches
     */
    public final Map<String, Integer> schOfDates;

    /**
     * Unscheduled batches.
     */
    public final Set<String> unusedBatches;

    /**
     * The constructor.
     *
     * @param maxDay The max assigned date.
     * @param status The status.
     * @param objectiveValue The objective value.
     * @param dailyResults
     * @param schOfDates The scheduled of date of batches
     * @param unusedBatches The unscheduled batches
     */
    public SolvedType(int maxDay, CpSolverStatus status, double objectiveValue, List<PlanDailyResultType> dailyResults, Map<String, Integer> schOfDates, Set<String> unusedBatches) {
        this.maxDay = maxDay;
        this.status = status;
        this.objectiveValue = objectiveValue;
        this.dailyResults = Collections.unmodifiableList(new ArrayList<PlanDailyResultType>(dailyResults));
        this.schOfDates = Collections.unmodifiableMap(schOfDates);
        this.unusedBatches = Collections.unmodifiableSet(new HashSet<String>(unusedBatches));
    }
}
