package com.provectus.kafka.ui.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.provectus.kafka.ui.exception.UnprocessableEntityException;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JsonNodeUtil {
  private final static String NOT_OBJECT_EXCEPTION_MESSAGE = "JsonNode isn't Object";
  private final static String NOT_ARRAY_EXCEPTION_MESSAGE = "JsonNode isn't Array";

  public static Map<String, String> toMap(JsonNode node) {
    if (node.isObject()) {
      List<String> keys = getJsonObjectKeys(node);
      List<String> values = getJsonObjectValues(node);
      return IntStream.range(0, keys.size()).boxed()
              .collect(Collectors.toMap(keys::get, values::get));
    }
    throw new UnprocessableEntityException(NOT_OBJECT_EXCEPTION_MESSAGE);
  }

  public static List<String> toList(JsonNode node) {
    if (node.isArray()) {
      return getStreamForJsonArray(node).map(JsonNode::toString).collect(Collectors.toList());
    }
    throw new UnprocessableEntityException(NOT_OBJECT_EXCEPTION_MESSAGE);
  }



  public static List<String> getJsonObjectKeys(JsonNode node) {
    if (node.isObject()) {
      return StreamSupport.stream(
          Spliterators.spliteratorUnknownSize(node.fieldNames(), Spliterator.ORDERED), false
      ).collect(Collectors.toList());
    }
    throw new UnprocessableEntityException(NOT_OBJECT_EXCEPTION_MESSAGE);
  }

  public static List<String> getJsonObjectValues(JsonNode node) {
    if (node.isObject()) {
      return getJsonObjectKeys(node).stream().map(key -> node.get(key).asText())
          .collect(Collectors.toList());
    }
    throw new UnprocessableEntityException(NOT_OBJECT_EXCEPTION_MESSAGE);
  }

  public static <T> T getJsonNodeValue(JsonNode node) {
    if (node.isObject()) {
      return (T) toMap(node);
    } else if (node.isArray()) {
      return (T) toList(node);
    }
    return (T) node.toString();
  }

  public static Stream<JsonNode> getStreamForJsonArray(JsonNode node) {
    if (node.isArray() && node.size() > 0) {
      return StreamSupport.stream(node.spliterator(), false);
    }
    throw new UnprocessableEntityException(NOT_ARRAY_EXCEPTION_MESSAGE);
  }
}
