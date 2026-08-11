package uia.mext.tsod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.google.ortools.sat.CpSolverStatus;

public class ThinkingSODTest {

    @Test
    public void test1() {
        List<PlanType> plans = Arrays.asList(
                plan("P1", 50),
                plan("P2", 60, 0, 0));

        List<BatchType> batches = Arrays.asList(
                batch("B1", 0, 4, 46, "P1", "P2"),
                batch("B2", 0, 5, 45, "P1", "P2"),
                batch("B3", 1, 43, "P1", "P2"));

        ThinkingSOD sod = new ThinkingSOD();
        SolvedType solved = sod.solve(plans, batches, 2);
        ThinkingSOD.print(solved);
        Assert.assertEquals(CpSolverStatus.OPTIMAL, solved.status);
        Assert.assertEquals(0, solved.unusedBatches.size());
        Assert.assertEquals(5, solved.maxDay);
        Assert.assertEquals(12, solved.dailyResults.size());
    }

    @Test
    public void test2() {
        List<PlanType> plans = Arrays.asList(
                plan("P1", 50),
                plan("P2", 60, 0, 0));

        List<BatchType> batches = Arrays.asList(
                batch("B1", -3, -1, 46, "P1", "P2"),    // -3 ~ -1: ignore
                batch("B2", 3, 1, 45, "P1", "P2"));     //  3 ~  1: invalid

        ThinkingSOD sod = new ThinkingSOD();
        SolvedType solved = sod.solve(plans, batches, 2);
        Assert.assertEquals(CpSolverStatus.OPTIMAL, solved.status);
        Assert.assertEquals(2, solved.unusedBatches.size());
        Assert.assertEquals(-1, solved.maxDay);
        Assert.assertEquals(0, solved.dailyResults.size());
    }

    @Test
    public void test3() {
        List<PlanType> plans = Arrays.asList(
                plan("P1", 50),
                plan("P2", 60, 0, 0));

        List<BatchType> batches = Arrays.asList(
                batch("B1", 0, 4, 46, "P1", "P2"),
                batch("B2", 0, 5, 45, "P1", "P2", "P3"));   // no P3

        try {
            new ThinkingSOD().solve(plans, batches, 2);
            Assert.assertTrue(false);
        }
        catch (Exception ex) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void test4() {
        List<PlanType> plans = Arrays.asList(
                plan("P1", 50),
                plan("P1", 60, 0, 0));  // P1 still

        try {
            new ThinkingSOD().solve(plans, Collections.emptyList(), 2);
            Assert.assertTrue(false);
        }
        catch (Exception ex) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void test5() {
        List<PlanType> plans = Arrays.asList(
                plan("P1", 50),
                plan("P2", 60, 0, 0));

        List<BatchType> batches = Arrays.asList(
                batch("B1", 0, 4, 46, "P1", "P2"),
                batch("B1", 0, 5, 45, "P1", "P2")); // B1 still

        try {
            SolvedType solved = new ThinkingSOD().solve(plans, batches, 2);
            Assert.assertEquals(CpSolverStatus.OPTIMAL, solved.status);
            Assert.assertTrue(false);
        }
        catch (Exception ex) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void test6() {
        List<PlanType> plans = Arrays.asList(
                plan("ALL", 200),
                plan("X", 50),
                plan("Y", 50),
                plan("Z", 50, 1, 2));

        List<BatchType> batches = Arrays.asList(
                batch("B1", 0, 2, 60, "ALL", "X", "Z"),
                batch("B2", 1, 4, 10, "ALL", "X", "Y"),
                batch("B3", 1, 4, 20, "ALL", "X", "Y", "Z"),
                batch("B4", 0, 4, 20, "ALL", "Y", "Z"),
                batch("B5", 1, 4, 10, "ALL", "Y", "Z"),
                batch("B6", 0, 3, 40, "ALL", "Y", "Z"),
                batch("B7", 1, 3, 20, "ALL", "Y", "Z"),
                batch("B8", 6, 9, 20, "ALL", "Y", "Z"),
                batch("B9", 3, 5, 10, "ALL", "Y", "Z"));

        int availableProcessors = Runtime.getRuntime().availableProcessors();

        ThinkingSOD sod = new ThinkingSOD();
        SolvedType solved = sod.solve(plans, batches, availableProcessors / 4);
        Assert.assertEquals(CpSolverStatus.OPTIMAL, solved.status);
    }

    private PlanType plan(String id, int expectedQuantity) {
        return plan(id, expectedQuantity, null, null);
    }

    private PlanType plan(String id, int expectedQuantity, Integer startDay, Integer endDay) {
        return new PlanType(id, expectedQuantity, startDay, endDay);
    }

    private BatchType batch(String id, int startDay, int endDay, int qty, String... plans) {
        Set<String> allowed = new TreeSet<>();
        for (String plan : plans) {
            allowed.add(plan);
        }
        return new BatchType(id, allowed, startDay, endDay, qty);
    }

    private BatchType batch(String id, int assignedDay, int qty, String... plans) {
        Set<String> allowed = new TreeSet<>();
        for (String plan : plans) {
            allowed.add(plan);
        }
        return new BatchType(id, allowed, assignedDay, qty);
    }
}
