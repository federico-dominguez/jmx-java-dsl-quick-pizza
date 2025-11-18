package com.fedd.salesforce;

import us.abstracta.jmeter.javadsl.core.DslTestPlan;

public class AppTest {
    public static void main(String[] args) {
        PerformanceTest performanceTest = new PerformanceTest();
        try {
            DslTestPlan testPlan = performanceTest.test();
            testPlan.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
