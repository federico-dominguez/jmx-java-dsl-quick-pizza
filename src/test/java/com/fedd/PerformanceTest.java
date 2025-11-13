package com.fedd;

import static us.abstracta.jmeter.javadsl.JmeterDsl.httpSampler;
import static us.abstracta.jmeter.javadsl.JmeterDsl.responseAssertion;
import static us.abstracta.jmeter.javadsl.JmeterDsl.testPlan;
import static us.abstracta.jmeter.javadsl.JmeterDsl.threadGroup;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import us.abstracta.jmeter.javadsl.core.DslTestPlan;
import us.abstracta.jmeter.javadsl.core.listeners.DslViewResultsTree;

public class PerformanceTest {
    @Test
    public DslTestPlan testPerformance() throws IOException {
    return testPlan(
        threadGroup(1, 1,
            httpSampler("https://quickpizza.grafana.com/"))
            .children(
                    responseAssertion().containsSubstrings("QuickPizza")
                ),
        new DslViewResultsTree()
    );
  }
}
