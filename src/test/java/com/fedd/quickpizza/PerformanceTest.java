package com.fedd.quickpizza;

import static us.abstracta.jmeter.javadsl.JmeterDsl.boundaryExtractor;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpAuth;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpDefaults;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpHeaders;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpSampler;
import static us.abstracta.jmeter.javadsl.JmeterDsl.jsonExtractor;
import static us.abstracta.jmeter.javadsl.JmeterDsl.jsr223PostProcessor;
import static us.abstracta.jmeter.javadsl.JmeterDsl.jsr223PreProcessor;
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
            httpAuth(),
            threadGroup(1, 1,
                transaction("Homepage",
                    jsr223PreProcessor(
                        "def len = 16\n" +
                            "def chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')\n" +
                            "def rnd = new java.security.SecureRandom()\n" +
                            "def sb = new StringBuilder()\n" +
                            "(1..len).each { sb.append(chars[rnd.nextInt(chars.size())]) }\n" +
                            "vars.put('AUTH_TOKEN', sb.toString())"),
                    httpSampler("https://quickpizza.grafana.com/", "https://${BASE_URL_1}/"),
                    httpSampler("https://quickpizza.grafana.com/api/config", "https://${BASE_URL_1}/api/config"),
                    httpSampler("https://quickpizza.grafana.com/api/quotes", "https://${BASE_URL_1}/api/quotes"),
                    httpSampler("https://quickpizza.grafana.com/api/tools", "https://${BASE_URL_1}/api/tools")
                        .header("Authorization", "Token ${AUTH_TOKEN}")),
                transaction("Generate pizza",
                    httpSampler("https://quickpizza.grafana.com/api/pizza", "https://${BASE_URL_1}/api/pizza")
                        .post(
                            "{\"maxCaloriesPerSlice\":1000,\"mustBeVegetarian\":false,\"excludedIngredients\":[],\"excludedTools\":[],\"maxNumberOfToppings\":5,\"minNumberOfToppings\":2,\"customName\":\"\"}",
                            ContentType.APPLICATION_JSON)
                        .header("Authorization", "Token ${AUTH_TOKEN}"),
                    httpSampler("https://quickpizza.grafana.com/api/ratings", "https://${BASE_URL_1}/api/ratings")
                        .post("{\"pizza_id\":753,\"stars\":5}", ContentType.APPLICATION_JSON)
                        .children(
                            jsr223PostProcessor("if (prev.getResponseCode().equals(\"401\")) {\n"
                                + "    prev.setSuccessful(true);\n"
                                + "}"))),
                transaction("Log in",
                    httpSampler("https://quickpizza.grafana.com/login", "https://${BASE_URL_1}/login"),
                    httpSampler("https://quickpizza.grafana.com/api/config", "https://${BASE_URL_1}/api/config"),
                    httpSampler("https://quickpizza.grafana.com/api/csrf-token", "https://${BASE_URL_1}/api/csrf-token")
                        .method(HTTPConstants.POST)
                        .children(
                            boundaryExtractor("csrf_token", "csrf_token=", ";")
                                .fieldToCheck(TargetField.RESPONSE_HEADERS)
                                .defaultValue("csrf_token_not_found")),
                    httpSampler("https://quickpizza.grafana.com/api/users/token/login?set_cookie=true",
                        "https://${BASE_URL_1}/api/users/token/login?set_cookie=true")
                        .post(
                            "{\"username\":\"studio-user\",\"password\":\"k6studiorocks\",\"csrf\":\"${csrf_token}\"}",
                            ContentType.APPLICATION_JSON)
                          .children(jsonExtractor("token", "token")),
                    httpSampler("https://quickpizza.grafana.com/api/ratings", "https://${BASE_URL_1}/api/ratings")),
                transaction("Generate and rate custom pizza",
                    httpSampler("https://quickpizza.grafana.com/", "https://${BASE_URL_1}/"),
                    httpSampler("https://quickpizza.grafana.com/api/config", "https://${BASE_URL_1}/api/config"),
                    httpSampler("https://quickpizza.grafana.com/api/quotes", "https://${BASE_URL_1}/api/quotes"),
                    httpSampler("https://quickpizza.grafana.com/api/tools", "https://${BASE_URL_1}/api/tools")
                        .header("Authorization", "Token ${token}"),
                    httpSampler("https://quickpizza.grafana.com/api/pizza", "https://${BASE_URL_1}/api/pizza")
                        .post(
                            "{\"maxCaloriesPerSlice\":1000,\"mustBeVegetarian\":false,\"excludedIngredients\":[],\"excludedTools\":[],\"maxNumberOfToppings\":5,\"minNumberOfToppings\":2,\"customName\":\"testedpizza\"}",
                            ContentType.APPLICATION_JSON)
                        .header("Authorization", "Token ${token}")
                        .children(
                            jsonExtractor("pizza_id", "pizza.id")
                                .queryLanguage(JsonQueryLanguage.JSON_PATH)
                                .defaultValue("pizza_id_not_found")),
                    httpSampler("https://quickpizza.grafana.com/api/ratings", "https://${BASE_URL_1}/api/ratings")
                        .post("{\"pizza_id\":${pizza_id},\"stars\":5}", ContentType.APPLICATION_JSON)),
                resultsTreeVisualizer()));
  }
}