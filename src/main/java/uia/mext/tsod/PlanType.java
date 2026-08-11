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
* The plan.
*
* @author Kyle K. Lin
*
*/
public final class PlanType {

    /**
     * The plan id
     */
    public final String id;

    /**
     * Excepted quantity
     */
    public final int expectedQuantity;

    /**
     * First deliverable day
     */
    public final Integer startDay;

    /**
     * Last deliverable day
     */
    public final Integer endDay;

    /**
     * The constructor.
     *
     * @param id The plan id
     * @param expectedQuantity Expected quantity
     */
    public PlanType(String id, int expectedQuantity) {
        this.id = id;
        this.expectedQuantity = expectedQuantity;
        this.startDay = null;
        this.endDay = null;
    }

    /**
     * The constructor.
     *
     * @param id The plan id
     * @param expectedQuantity Expected quantity
     * @param startDay First deliverable day
     * @param endDay Last deliver day
     */
    public PlanType(String id, int expectedQuantity, Integer startDay, Integer endDay) {
        this.id = id;
        this.expectedQuantity = expectedQuantity;
        this.startDay = startDay;
        this.endDay = endDay;
    }
}
