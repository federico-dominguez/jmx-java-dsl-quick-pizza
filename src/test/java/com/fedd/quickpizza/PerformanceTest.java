package com.fedd.quickpizza;

import static us.abstracta.jmeter.javadsl.JmeterDsl.boundaryExtractor;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpAuth;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpDefaults;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpHeaders;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpSampler;
import static us.abstracta.jmeter.javadsl.JmeterDsl.jsonExtractor;
import static us.abstracta.jmeter.javadsl.JmeterDsl.jsr223PostProcessor;
import static us.abstracta.jmeter.javadsl.JmeterDsl.resultsTreeVisualizer;
import static us.abstracta.jmeter.javadsl.JmeterDsl.testPlan;
import static us.abstracta.jmeter.javadsl.JmeterDsl.threadGroup;
import static us.abstracta.jmeter.javadsl.JmeterDsl.transaction;
import static us.abstracta.jmeter.javadsl.JmeterDsl.vars;

import java.io.IOException;

import org.apache.http.entity.ContentType;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.junit.jupiter.api.Test;

import us.abstracta.jmeter.javadsl.core.DslTestPlan;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslBoundaryExtractor.TargetField;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsonExtractor.JsonQueryLanguage;

public class PerformanceTest {

  @Test
  public DslTestPlan test() throws IOException {
    return testPlan()
        .tearDownOnlyAfterMainThreadsDone()
        .children(
          vars()
            .set("BASE_URL_1", "quickpizza.grafana.com"),
          httpHeaders()
            .header("sec-ch-ua", "\"Chromium\";v=\"142\", \"Google Chrome\";v=\"142\", \"Not_A Brand\";v=\"99\"")
            .header("sec-ch-ua-mobile", "?0")
            .header("sec-ch-ua-platform", "\"Windows\"")
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36"),
          httpDefaults()
            .downloadEmbeddedResources(),
          httpAuth(),
          threadGroup(1, 1,
            transaction("Homepage",
              httpSampler("https://quickpizza.grafana.com/", "https://${BASE_URL_1}/")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Sec-Fetch-Site", "none")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-User", "?1")
                .header("Sec-Fetch-Dest", "document"),
              httpSampler("https://quickpizza.grafana.com/api/config", "https://${BASE_URL_1}/api/config")
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty"),
              httpSampler("https://quickpizza.grafana.com/api/quotes", "https://${BASE_URL_1}/api/quotes")
                .header("traceparent", "00-29f485cea38ddbbc6c4ea8b52510cc08-2ef805fb799c0a2f-01")
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty"),
              httpSampler("https://quickpizza.grafana.com/api/tools", "https://${BASE_URL_1}/api/tools")
                .header("traceparent", "00-1983c335996633c49c8663fe89931f24-e4299bf6df0553c5-01")
                .header("Authorization", "Token ZscKL6EkWdx7IptS")
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
            ),
            transaction("Generate pizza",
              httpSampler("https://quickpizza.grafana.com/api/pizza", "https://${BASE_URL_1}/api/pizza")
                .post("{\"maxCaloriesPerSlice\":1000,\"mustBeVegetarian\":false,\"excludedIngredients\":[],\"excludedTools\":[],\"maxNumberOfToppings\":5,\"minNumberOfToppings\":2,\"customName\":\"\"}", ContentType.APPLICATION_JSON)
                .header("traceparent", "00-0facc7d00fe1f4a4b190388b038fd72a-ae8ef0bceefdaeb6-01")
                .header("Authorization", "Token ZscKL6EkWdx7IptS")
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty"),
              httpSampler("https://quickpizza.grafana.com/api/ratings", "https://${BASE_URL_1}/api/ratings")
                .post("{\"pizza_id\":753,\"stars\":5}", ContentType.APPLICATION_JSON)
                .header("traceparent", "00-f177686a6c6790eaf9233471f4707220-21156a0abe56e77a-01")
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
                .children(
                  jsr223PostProcessor("if (prev.getResponseCode().equals(\"401\")) {\n"
                    + "    prev.setSuccessful(true);\n"
                    + "}")
                )
            ),
            transaction("Log in",
              httpSampler("https://quickpizza.grafana.com/login", "https://${BASE_URL_1}/login")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-User", "?1")
                .header("Sec-Fetch-Dest", "document"),
              httpSampler("https://quickpizza.grafana.com/api/config", "https://${BASE_URL_1}/api/config")
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty"),
              httpSampler("https://quickpizza.grafana.com/api/csrf-token", "https://${BASE_URL_1}/api/csrf-token")
                .method(HTTPConstants.POST)
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
                .children(
                  boundaryExtractor("csrf_token", "csrf_token=", ";")
                    .fieldToCheck(TargetField.RESPONSE_HEADERS)
                    .defaultValue("csrf_token_not_found")
                ),
              httpSampler("https://quickpizza.grafana.com/api/users/token/login?set_cookie=true", "https://${BASE_URL_1}/api/users/token/login?set_cookie=true")
                .post("{\"username\":\"studio-user\",\"password\":\"k6studiorocks\",\"csrf\":\"${csrf_token}\"}", ContentType.APPLICATION_JSON)
                .header("traceparent", "00-d3f664bc5e2d6b03bba5b5008b3fca39-d14070fd53cdb752-01")
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty"),
              httpSampler("https://quickpizza.grafana.com/api/ratings", "https://${BASE_URL_1}/api/ratings")
                .header("traceparent", "00-7fe6ce13a1aaa0b3089bb1b6acd089d4-86b757a8e6354eeb-01")
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
            ),
            transaction("Generate and rate custom pizza",
              httpSampler("https://quickpizza.grafana.com/", "https://${BASE_URL_1}/")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-User", "?1")
                .header("Sec-Fetch-Dest", "document"),
              httpSampler("https://quickpizza.grafana.com/api/config", "https://${BASE_URL_1}/api/config")
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty"),
              httpSampler("https://quickpizza.grafana.com/api/quotes", "https://${BASE_URL_1}/api/quotes")
                .header("traceparent", "00-d5b4bd70efc4bfd204bb7c84574c8fbd-d8f58bc31849b2e2-01")
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty"),
              httpSampler("https://quickpizza.grafana.com/api/tools", "https://${BASE_URL_1}/api/tools")
                .header("traceparent", "00-f660576fe614cc105d13cd234ccbacb0-26e7a78524d3473b-01")
                .header("Authorization", "Token 1VVwOB2wjCxY36XQ")
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty"),
              httpSampler("https://quickpizza.grafana.com/api/pizza", "https://${BASE_URL_1}/api/pizza")
                .post("{\"maxCaloriesPerSlice\":1000,\"mustBeVegetarian\":false,\"excludedIngredients\":[],\"excludedTools\":[],\"maxNumberOfToppings\":5,\"minNumberOfToppings\":2,\"customName\":\"testedpizza\"}", ContentType.APPLICATION_JSON)
                .header("traceparent", "00-d3ba764d535cf4bb54801c389e4fc360-b5f4b0b89feb099d-01")
                .header("Authorization", "Token 1VVwOB2wjCxY36XQ")
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
                .children(
                  jsonExtractor("pizza_id", "pizza.id")
                    .queryLanguage(JsonQueryLanguage.JSON_PATH)
                    .defaultValue("pizza_id_not_found")
                ),
              httpSampler("https://quickpizza.grafana.com/api/ratings", "https://${BASE_URL_1}/api/ratings")
                .post("{\"pizza_id\":${pizza_id},\"stars\":5}", ContentType.APPLICATION_JSON)
                .header("traceparent", "00-450eb4614871279bcd37f6ddf69e9f8f-798dbe7e1089ddf9-01")
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
            ),
            resultsTreeVisualizer()
          )
        );
  }
}