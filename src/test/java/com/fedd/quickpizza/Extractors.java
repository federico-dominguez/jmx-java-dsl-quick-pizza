package com.fedd.quickpizza;

import static us.abstracta.jmeter.javadsl.JmeterDsl.boundaryExtractor;
import static us.abstracta.jmeter.javadsl.JmeterDsl.jsonExtractor;

import us.abstracta.jmeter.javadsl.core.postprocessors.DslBoundaryExtractor;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslBoundaryExtractor.TargetField;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsonExtractor;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsonExtractor.JsonQueryLanguage;

public final class Extractors {

  public static DslBoundaryExtractor csrfToken() {
    return boundaryExtractor("csrf_token", "csrf_token=", ";")
        .fieldToCheck(TargetField.RESPONSE_HEADERS)
        .defaultValue("csrf_token_not_found");
  }

  public static DslJsonExtractor token() {
    return jsonExtractor("token", "$.token")
        .queryLanguage(JsonQueryLanguage.JSON_PATH)
        .defaultValue("token_not_found");
  }

  public static DslJsonExtractor pizzaId() {
    return jsonExtractor("pizza_id", "pizza.id")
        .queryLanguage(JsonQueryLanguage.JSON_PATH)
        .defaultValue("pizza_id_not_found");
  }

  private Extractors() {}
}