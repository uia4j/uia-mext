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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The batch
 *
 * @author Kyle K. Lin
 *
 */
public final class BatchType {

    /**
     * The batch id
     */
    public final String id;

    /**
     * List of plans applicable to this batch
     */
    public final Set<String> allowedPlans;

    /**
     * The first day that can be delivered
     */
    public final int startDay;

    /**
     * The last day that can be delivered
     */
    public final int endDay;

    /**
     * The quantity
     */
    public final int quantity;

    /**
     * Assigned or not
     */
    public final boolean assigned;

    /**
     * The constructor.
     *
     * @param id The batch id
     * @param allowedPlans List of plans applicable to this batch
     * @param startDay The first day that can be delivered
     * @param endDay The last day that can be delivered
     * @param quantity The quantity
     */
    public BatchType(String id, Set<String> allowedPlans, int startDay, int endDay, int quantity) {
        this.id = id;
        this.allowedPlans = Collections.unmodifiableSet(new HashSet<String>(allowedPlans));
        this.startDay = startDay;
        this.endDay = endDay;
        this.quantity = quantity;
        this.assigned = false;
    }

    /**
     * The constructor.
     *
     * @param id The batch id
     * @param allowedPlans List of plans applicable to this batch
     * @param assignedDay The assigned day
     * @param quantity The quantity
     */
    public BatchType(String id, Set<String> allowedPlans, int assignedDay, int quantity) {
        this.id = id;
        this.allowedPlans = Collections.unmodifiableSet(new HashSet<String>(allowedPlans));
        this.startDay = assignedDay;
        this.endDay = assignedDay;
        this.quantity = quantity;
        this.assigned = true;
    }
}
