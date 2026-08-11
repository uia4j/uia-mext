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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.ortools.Loader;
import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;

/**
 * SOD Main Program
 *
 * @author Kyle K. Lin
 *
 */
public class ThinkingSOD {

    private static final Logger LOGGER = LogManager.getLogger(ThinkingSOD.class);

    private static final long SHORTAGE_WEIGHT = 10L;

    private static final long EXCESS_WEIGHT = 1L;

    /**
     * Solve the problem.
     *
     * @param plans The plans
     * @param batches The batches
     * @param workerCount The worker number.
     *
     * @return The result.
     */
    public SolvedType solve(List<PlanType> plans, List<BatchType> batches, int workerCount) {
        validate(plans, batches);
        Loader.loadNativeLibraries();
        CpModel model = new CpModel();
        Map<String, Integer> planIndexById = new HashMap<>(plans.size());
        for (int p = 0; p < plans.size(); p++) {
            planIndexById.put(plans.get(p).id, Integer.valueOf(p));
        }

        /**
         * assignment variable, x[b,d] = 1
         * true: assigned
         * false: not assigned
         */
        Map<AssignmentKeyType, IntVar> assignVars = new LinkedHashMap<>();

        /**
         * plan-day variable
         * available assignments of plan-day.
         */
        Map<Integer, List<AssignmentKeyType>> planDayAsnKeys = new HashMap<>();

        int maxDay = -1;
        Map<String, Integer> used = new TreeMap<>();
        for (int bIdx = 0; bIdx < batches.size(); bIdx++) {
            //List<Literal> bVar = new ArrayList<>();
            //batchVars.add(bVar);

            BatchType batch = batches.get(bIdx);
            // history
            if (batch.endDay < 0) {
                continue;
            }

            int firstDay = Math.max(0, batch.startDay);
            int lastDay = Math.max(0, batch.endDay);

            // invalid
            if (firstDay > lastDay) {
                continue;
            }

            maxDay = Math.max(maxDay, lastDay);
            if (batch.assigned) {
                for (String pId : batch.allowedPlans) {
                    Integer pIndex = planIndexById.get(pId);
                    if (pIndex == null) {
                        continue;
                    }

                    String planDayKey = "p" + pId + "_d" + batch.startDay;
                    int q = used.getOrDefault(planDayKey, 0);
                    used.put(planDayKey, q + batch.quantity);
                }
            }
            else {
                // days applied to the batch.
                // List<Literal> oneDayVar = new ArrayList<>();
                LinearExprBuilder oneDayExpr = LinearExpr.newBuilder();
                for (int day = firstDay; day <= lastDay; day++) {
                    boolean planned = false;

                    // enable/disable flag
                    BoolVar bdVar = model.newBoolVar("x_b" + bIdx + "_d" + day);
                    for (String planId : batch.allowedPlans) {
                        Integer pIndex = planIndexById.get(planId);
                        if (pIndex == null) {
                            continue;
                        }

                        PlanType plan = plans.get(pIndex);
                        if (plan.startDay != null && day < plan.startDay) {
                            continue;
                        }
                        if (plan.endDay != null && day > plan.endDay) {
                            continue;
                        }

                        // applied days
                        AssignmentKeyType bpdKey = new AssignmentKeyType(bIdx, pIndex, day);
                        assignVars.put(bpdKey, bdVar);

                        // all assignments of one plan-day
                        int planDayKey = buildPlanDayKey(pIndex, day);
                        List<AssignmentKeyType> planDayValues = planDayAsnKeys.get(planDayKey);
                        if (planDayValues == null) {
                            planDayValues = new ArrayList<>();
                            planDayAsnKeys.put(planDayKey, planDayValues);
                        }
                        planDayValues.add(bpdKey);
                        planned = true;
                    }

                    if (planned) {
                        // oneDayVar.add(bdVar);
                        oneDayExpr.add(bdVar);
                    }
                }

                // batch constraint #1: most one, Σ(x[b,d]) <= 1
                // model.addAtMostOne(oneDayVar);
                model.addLessOrEqual(oneDayExpr, 1L);
            }
        }

        LOGGER.info("maxDay=" + maxDay);
        if (maxDay < 0) {
            Set<String> unusedBatchIds = new HashSet<String>();
            for (BatchType batch : batches) {
                if (batch.assigned) {
                    continue;
                }
                unusedBatchIds.add(batch.id);
            }
            return new SolvedType(
                    -1,
                    CpSolverStatus.OPTIMAL,
                    Double.NaN,
                    Collections.emptyList(),
                    Collections.emptyMap(),
                    unusedBatchIds);
        }

        IntVar[][] actual = new IntVar[plans.size()][maxDay + 1];
        IntVar[][] shortage = new IntVar[plans.size()][maxDay + 1];
        IntVar[][] excess = new IntVar[plans.size()][maxDay + 1];

        LinearExprBuilder objective = LinearExpr.newBuilder();
        for (int p = 0; p < plans.size(); p++) {
            PlanType plan = plans.get(p);
            for (int day = 0; day <= maxDay; day++) {
                List<AssignmentKeyType> eligibleKeys = getPlanDayKeys(planDayAsnKeys, p, day);
                if (eligibleKeys.isEmpty()) {
                    LOGGER.info(String.format("%-20s %2s EMPTY", plan.id, day));
                    actual[p][day] = model.newIntVar(0L, 0L, "actual_p" + p + "_d" + day);
                    shortage[p][day] = model.newIntVar(0L, 0L, "shortage_p" + p + "_d" + day);
                    excess[p][day] = model.newIntVar(0L, 0L, "excess_p" + p + "_d" + day);
                    continue;
                }

                int expected = plan.expectedQuantity - used.getOrDefault("p" + plan.id + "_d" + day, 0);
                if (expected <= 0) {
                    LOGGER.info(String.format("%-20s %2s FULL %4s", plan.id, day, plan.expectedQuantity));
                    actual[p][day] = model.newIntVar(0L, 0L, "actual_p" + p + "_d" + day);
                    shortage[p][day] = model.newIntVar(0L, 0L, "shortage_p" + p + "_d" + day);
                    excess[p][day] = model.newIntVar(0L, 0L, "excess_p" + p + "_d" + day);
                    continue;
                }

                // actual[p,d] = Σ(x[b,p,d] × quantity[b])
                int actualMax = 0;
                int excessMax = 0;
                LinearExprBuilder actualExpr = LinearExpr.newBuilder();
                for (AssignmentKeyType key : eligibleKeys) {
                    BatchType b = batches.get(key.batchIndex);
                    actualMax += b.quantity;
                    excessMax = Math.max(b.quantity, excessMax);
                    actualExpr.addTerm(assignVars.get(key), b.quantity);
                }
                LOGGER.info(String.format("%-20s %2s PLAN %4s/%4s", plan.id, day, actualMax, expected));

                actual[p][day] = model.newIntVar(0L, actualMax, "actual_p" + p + "_d" + day);
                shortage[p][day] = model.newIntVar(0L, expected, "shortage_p" + p + "_d" + day);
                excess[p][day] = model.newIntVar(0L, excessMax, "excess_p" + p + "_d" + day);

                /**
                 * plan-day constraint #1: actual = actualExpr
                 */
                model.addEquality(actual[p][day], actualExpr);

                /**
                 * plan-day constraint #2: expected = balanceExpr = actual + shortage - excess
                 */
                LinearExprBuilder balanceExpr = LinearExpr.newBuilder();
                balanceExpr.add(actual[p][day]);
                balanceExpr.add(shortage[p][day]);
                balanceExpr.addTerm(excess[p][day], -1);
                model.addEquality(balanceExpr, expected);

                /**
                 * objective
                 * 1. Avoid shortages as much as possible.
                 * 2. The smaller the deviation, the better, especially in the early stages.
                 */
                long w = 2L ^ (maxDay - day);
                objective.addTerm(shortage[p][day], SHORTAGE_WEIGHT * w);
                objective.addTerm(excess[p][day], EXCESS_WEIGHT * w);
            }
        }
        model.minimize(objective);

        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(60);
        if (workerCount > 0) {
            solver.getParameters().setNumSearchWorkers(workerCount);
        }

        /**
         * useful for debug
         */
        /**
        solver.getParameters()
                .setLogSearchProgress(true);
         */

        CpSolverStatus status = solver.solve(model);

        /**
         * status: Not Good
         */
        if (status != CpSolverStatus.OPTIMAL && status != CpSolverStatus.FEASIBLE) {
            Set<String> allBatchIds = new HashSet<>();
            for (BatchType batch : batches) {
                allBatchIds.add(batch.id);
            }
            return new SolvedType(
                    maxDay,
                    status,
                    Double.NaN,
                    Collections.emptyList(),
                    Collections.emptyMap(),
                    allBatchIds);
        }

        Map<String, Integer> usedBatches = new TreeMap<>();
        List<PlanDailyResultType> dailyResults = new ArrayList<>();
        for (int day = 0; day <= maxDay; day++) {
            for (int p = 0; p < plans.size(); p++) {
                PlanType plan = plans.get(p);

                List<AssignmentType> assignments = new ArrayList<>();
                List<AssignmentKeyType> eligibleKeys = getPlanDayKeys(planDayAsnKeys, p, day);
                for (AssignmentKeyType key : eligibleKeys) {
                    if (solver.value(assignVars.get(key)) == 1) {
                        BatchType batch = batches.get(key.batchIndex);
                        AssignmentType assignment = new AssignmentType(
                                day,
                                plan.id,
                                batch.id,
                                batch.quantity);
                        assignments.add(assignment);
                        usedBatches.put(batch.id, day);
                    }
                }
                Collections.sort(assignments, (left, right) -> left.batchId.compareTo(right.batchId));

                dailyResults.add(new PlanDailyResultType(
                        day,
                        plan.id,
                        plan.expectedQuantity,
                        solver.value(actual[p][day]),
                        assignments));
            }
        }

        Set<String> unusedBatchIds = new HashSet<String>();
        for (BatchType batch : batches) {
            if (batch.assigned) {
                continue;
            }
            if (!usedBatches.containsKey(batch.id)) {
                unusedBatchIds.add(batch.id);
            }
        }

        LOGGER.info("OP> Status: " + status + ", Objective: " + solver.objectiveValue());
        LOGGER.info("OP> Unused: " + unusedBatchIds.size());

        return new SolvedType(
                maxDay,
                status,
                solver.objectiveValue(),
                dailyResults,
                usedBatches,
                unusedBatchIds);
    }

    private List<AssignmentKeyType> getPlanDayKeys(
            Map<Integer, List<AssignmentKeyType>> keysByPlanDay,
            int planIndex,
            int day) {
        int key = buildPlanDayKey(planIndex, day);
        return keysByPlanDay.getOrDefault(key, Collections.emptyList());
    }

    /**
     * 1000 * planIndex + day
     */
    private int buildPlanDayKey(int planIndex, int day) {
        return 1000 * planIndex + day;
    }

    private void validate(List<PlanType> plans, List<BatchType> batches) {
        Set<String> planIds = new HashSet<String>();
        for (PlanType plan : plans) {
            if (!planIds.add(plan.id)) {
                throw new IllegalArgumentException("plan:" + plan.id + " is duplicated");
            }
        }

        Set<String> batchIds = new HashSet<String>();
        for (BatchType batch : batches) {
            if (!batchIds.add(batch.id)) {
                throw new IllegalArgumentException("batch:" + batch.id + " is duplicated");
            }
            for (String allowedPlanId : batch.allowedPlans) {
                if (!planIds.contains(allowedPlanId)) {
                    throw new IllegalArgumentException("batch:" + batch.id + " references an invalid plan:" + allowedPlanId);
                }
            }
        }
    }

    public static void print(SolvedType solved) {
        System.out.printf("%-13s", "Plan");
        for (int day = 0; day <= solved.maxDay; day++) {
            System.out.printf(" | %4s", day);
        }
        System.out.println();

        Map<String, List<PlanDailyResultType>> plans = solved.dailyResults.stream().collect(Collectors.groupingBy(p -> p.planId));
        plans.forEach((k, vs) -> {
            System.out.printf("%-13s", k);
            Map<Integer, PlanDailyResultType> days = vs.stream().collect(Collectors.toMap(a -> a.day, a -> a));
            for (int day = 0; day <= solved.maxDay; day++) {
                PlanDailyResultType r = days.get(day);
                System.out.printf(" | %4s", r == null ? 0 : r.actualQuantity);
            }
            System.out.println();
        });
    }

    public static void printSOD(SolvedType solved) {
        solved.schOfDates.forEach((k, v) -> {
            System.out.printf("%-12s %s\n", k, v);
        });
    }
}