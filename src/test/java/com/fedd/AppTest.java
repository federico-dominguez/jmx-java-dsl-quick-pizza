package com.fedd;

import us.abstracta.jmeter.javadsl.core.DslTestPlan;

public class AppTest {
    public static void main(String[] args) {
        PerformanceTest performanceTest = new PerformanceTest();
        try {
            DslTestPlan testPlan = performanceTest.testPerformance();
            testPlan.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
