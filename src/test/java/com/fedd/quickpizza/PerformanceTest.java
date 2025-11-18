package com.fedd.quickpizza;

import static us.abstracta.jmeter.javadsl.JmeterDsl.httpSampler;
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
import us.abstracta.jmeter.javadsl.core.controllers.DslTransactionController;

public class PerformanceTest {

  @Test
  public DslTestPlan test() throws IOException {
    return testPlan(
        vars()
            .set("BASE_URL_1", "quickpizza.grafana.com"),
        threadGroup(1, 1,
            homePage(),
            generatePizza(),
            logIn(),
            generateAndRateCustomPizza(),
            resultsTreeVisualizer()));
  }

  private DslTransactionController homePage() {
    return transaction("Homepage",
        jsr223PreProcessor(
            "def len = 16\n" +
                "def chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')\n" +
                "def rnd = new java.security.SecureRandom()\n" +
                "def sb = new StringBuilder()\n" +
                "(1..len).each { sb.append(chars[rnd.nextInt(chars.size())]) }\n" +
                "vars.put('AUTH_TOKEN', sb.toString())"),
        httpSampler("Domain", "https://${BASE_URL_1}/"),
        httpSampler("Config", "https://${BASE_URL_1}/api/config"),
        httpSampler("Quotes", "https://${BASE_URL_1}/api/quotes"),
        httpSampler("Tools", "https://${BASE_URL_1}/api/tools")
            .header("Authorization", "Token ${AUTH_TOKEN}"))
        .generateParentSample();
  }

  private DslTransactionController generatePizza() {
    return transaction("Generate pizza",
        httpSampler("Pizza", "https://${BASE_URL_1}/api/pizza")
            .post(
                "{\"maxCaloriesPerSlice\":1000,\"mustBeVegetarian\":false,\"excludedIngredients\":[],\"excludedTools\":[],\"maxNumberOfToppings\":5,\"minNumberOfToppings\":2,\"customName\":\"\"}",
                ContentType.APPLICATION_JSON)
            .header("Authorization", "Token ${AUTH_TOKEN}"),
        httpSampler("Ratings", "https://${BASE_URL_1}/api/ratings")
            .post("{\"pizza_id\":753,\"stars\":5}", ContentType.APPLICATION_JSON)
            .children(
                jsr223PostProcessor("if (prev.responseCode == '401') { prev.successful = true }")))
        .generateParentSample();
  }

  private DslTransactionController logIn() {
    return transaction("Log in",
        httpSampler("Login", "https://${BASE_URL_1}/login"),
        httpSampler("Config", "https://${BASE_URL_1}/api/config"),
        httpSampler("Csrf-token", "https://${BASE_URL_1}/api/csrf-token")
            .method(HTTPConstants.POST)
            .children(
                Extractors.csrfToken()),
        httpSampler("Users_token_login",
            "https://${BASE_URL_1}/api/users/token/login?set_cookie=true")
            .post(
                "{\"username\":\"studio-user\",\"password\":\"k6studiorocks\",\"csrf\":\"${csrf_token}\"}",
                ContentType.APPLICATION_JSON)
            .children(Extractors.token()),
        httpSampler("Ratings", "https://${BASE_URL_1}/api/ratings")).generateParentSample();
  }

  private DslTransactionController generateAndRateCustomPizza() {
    return transaction("Generate and rate custom pizza",
        httpSampler("Domain", "https://${BASE_URL_1}/"),
        httpSampler("Config", "https://${BASE_URL_1}/api/config"),
        httpSampler("Quotes", "https://${BASE_URL_1}/api/quotes"),
        httpSampler("Tools", "https://${BASE_URL_1}/api/tools")
            .header("Authorization", "Token ${token}"),
        httpSampler("Pizza", "https://${BASE_URL_1}/api/pizza")
            .post(
                "{\"maxCaloriesPerSlice\":1000,\"mustBeVegetarian\":false,\"excludedIngredients\":[],\"excludedTools\":[],\"maxNumberOfToppings\":5,\"minNumberOfToppings\":2,\"customName\":\"testedpizza\"}",
                ContentType.APPLICATION_JSON)
            .header("Authorization", "Token ${token}")
            .children(
                Extractors.pizzaId()),
        httpSampler("Ratings", "https://${BASE_URL_1}/api/ratings")
            .post("{\"pizza_id\":${pizza_id},\"stars\":5}", ContentType.APPLICATION_JSON))
        .generateParentSample();
  }
}