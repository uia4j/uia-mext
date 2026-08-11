package uia.mext.tsod;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import uia.mext.tsod.BatchType;
import uia.mext.tsod.PlanType;
import uia.mext.tsod.SolvedType;
import uia.mext.tsod.ThinkingSOD;

public class ThinkingSODTestPrint {

    @Test
    public void test1() {
        List<PlanType> plans = Arrays.asList(
                plan("P1", 50),
                plan("P2", 60, 2, 3));

        List<BatchType> batches = Arrays.asList(
                batch("B1", 0, 5, 40, "P1", "P2"));

        int availableProcessors = Runtime.getRuntime().availableProcessors();

        ThinkingSOD sod = new ThinkingSOD();
        SolvedType pr = sod.solve(plans, batches, availableProcessors / 4);
        ThinkingSOD.print(pr);
    }

    @Test
    public void test2() {
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
                batch("B5", 0, 3, 40, "ALL", "Y", "Z"),
                batch("B6", 1, 3, 20, "ALL", "Y", "Z"),
                batch("B7", 6, 9, 20, "ALL", "Y", "Z"),
                batch("B8", 3, 5, 10, "ALL", "Y", "Z"),
                batch("B9", 3, 8, 10, "ALL", "Y", "Z"));

        int availableProcessors = Runtime.getRuntime().availableProcessors();

        ThinkingSOD sod = new ThinkingSOD();
        SolvedType pr = sod.solve(plans, batches, availableProcessors / 4);
        ThinkingSOD.print(pr);
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
}
