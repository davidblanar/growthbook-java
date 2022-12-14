package io.github.davidblanar.growthbook;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Pattern;

public class GBConditionEvaluator {
    private static final String OR = "$or";
    private static final String NOR = "$nor";
    private static final String AND = "$and";
    private static final String NOT = "$not";
    private static final String TYPE = "$type";
    private static final String EXISTS = "$exists";
    private static final String IN = "$in";
    private static final String NIN = "$nin";
    private static final String ALL = "$all";
    private static final String ELEM_MATCH = "$elemMatch";
    private static final String SIZE = "$size";
    private static final String EQ = "$eq";
    private static final String NE = "$ne";
    private static final String LT = "$lt";
    private static final String LTE = "$lte";
    private static final String GT = "$gt";
    private static final String GTE = "$gte";
    private static final String REGEX = "$regex";
    private static final String STRING = "string";
    private static final String NUMBER = "number";
    private static final String BOOLEAN = "boolean";
    private static final String ARRAY = "array";
    private static final String OBJECT = "object";
    private static final String NULL = "null";
    private static final String UNKNOWN = "unknown";

    public static boolean evalCondition(JsonElement attributes, JsonElement condition) {
        if (condition.isJsonArray()) {
            return false;
        }
        var cond = condition.getAsJsonObject();
        if (cond.has(OR)) {
            return evalOr(attributes, cond.get(OR).getAsJsonArray());
        }
        if (cond.has(NOR)) {
            return !evalOr(attributes, cond.get(NOR).getAsJsonArray());
        }
        if (cond.has(AND)) {
            return evalAnd(attributes, cond.get(AND).getAsJsonArray());
        }
        if (cond.has(NOT)) {
            return !evalCondition(attributes, cond.get(NOT));
        }
        for (var key: condition.getAsJsonObject().keySet()) {
            var element = getPath(attributes, key);
            var value = condition.getAsJsonObject().get(key);
            if (value != null) {
                if (!evalConditionValue(value, element)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean evalOr(JsonElement attributes, JsonArray conditions) {
        if (conditions.size() == 0) {
            return true;
        }
        for (var c: conditions) {
            if (evalCondition(attributes, c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean evalAnd(JsonElement attributes, JsonArray conditions) {
        for (var c: conditions) {
            if (!evalCondition(attributes, c)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isOperatorObject(JsonElement obj) {
        if (!obj.isJsonObject()) {
            return false;
        }
        for (var key: obj.getAsJsonObject().keySet()) {
            if (!key.startsWith("$")) {
                return false;
            }
        }
        return true;
    }

    private static String getType(JsonElement obj) {
        if (obj.isJsonNull()) {
            return NULL;
        }
        if (obj.isJsonPrimitive()) {
            var value = obj.getAsJsonPrimitive();
            if (value.isString()) {
                return STRING;
            }
            if (value.isBoolean()) {
                return BOOLEAN;
            }
            if (value.isNumber()) {
                return NUMBER;
            }
        }
        if (obj.isJsonArray()) {
            return ARRAY;
        }
        if (obj.isJsonObject()) {
            return OBJECT;
        }
        return UNKNOWN;
    }

    private static JsonElement getPath(JsonElement attributes, String key) {
        var paths = new ArrayList<String>();
        if (key.contains(".")) {
            Collections.addAll(paths, key.split("\\."));
        } else {
            paths.add(key);
        }
        var element = attributes;
        for (var p: paths) {
            if (element == null || element.isJsonArray()) {
                return null;
            }
            if (element.isJsonObject()) {
                element = element.getAsJsonObject().get(p);
            } else {
                return null;
            }
        }
        return element;
    }

    private static boolean evalConditionValue(JsonElement conditionValue, JsonElement attributeValue) {
        if (attributeValue != null && conditionValue.isJsonPrimitive() && attributeValue.isJsonPrimitive()) {
            return Objects.equals(conditionValue.getAsJsonPrimitive(), attributeValue.getAsJsonPrimitive());
        }
        if (conditionValue.isJsonPrimitive() && attributeValue == null) {
            return false;
        }
        if (conditionValue.isJsonArray()) {
            if (attributeValue != null && attributeValue.isJsonArray()) {
                var condArray = conditionValue.getAsJsonArray();
                var attrArray = attributeValue.getAsJsonArray();
                if (condArray.size() == attrArray.size()) {
                    return condArray.equals(attrArray);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        if (conditionValue.isJsonObject()) {
            if (isOperatorObject(conditionValue)) {
                var cond = conditionValue.getAsJsonObject();
                for (var key: cond.keySet()) {
                    if (!evalOperatorCondition(key, attributeValue, cond.get(key))) {
                        return false;
                    }
                }
            } else if (attributeValue != null) {
                return Objects.equals(conditionValue, attributeValue);
            } else {
                return false;
            }
        }
        return true;
    }

    private static boolean elemMatch(JsonElement attributeValue, JsonElement condition) {
        if (attributeValue.isJsonArray()) {
            var attrs = attributeValue.getAsJsonArray();
            for (var item: attrs) {
                if (isOperatorObject(condition)) {
                    if (evalConditionValue(condition, item)) {
                        return true;
                    }
                } else if (evalCondition(item, condition)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean evalOperatorCondition(String operator, JsonElement attributeValue, JsonElement conditionValue) {
        if (Objects.equals(operator, TYPE)) {
            return Objects.equals(getType(attributeValue), conditionValue.getAsJsonPrimitive().getAsString());
        }
        if (Objects.equals(operator, NOT)) {
            return !evalConditionValue(conditionValue, attributeValue);
        }
        if (Objects.equals(operator, EXISTS)) {
            var targetPrimitiveValue = conditionValue.getAsJsonPrimitive().getAsBoolean();
            if (!targetPrimitiveValue && attributeValue == null) {
                return true;
            } else if (targetPrimitiveValue && attributeValue != null) {
                return true;
            }
        }
        if (conditionValue.isJsonArray()) {
            var condArr = conditionValue.getAsJsonArray();
            switch (operator) {
                case IN:
                    return condArr.contains(attributeValue);
                case NIN:
                    return !condArr.contains(attributeValue);
                case ALL:
                    if (attributeValue != null && attributeValue.isJsonArray()) {
                        for (var c: condArr) {
                            var result = false;
                            for (var attr: attributeValue.getAsJsonArray()) {
                                if (evalConditionValue(c, attr)) {
                                    result = true;
                                }
                            }
                            if (!result) {
                                return result;
                            }
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
        } else if (attributeValue != null && attributeValue.isJsonArray()) {
            switch (operator) {
                case ELEM_MATCH:
                    return elemMatch(attributeValue, conditionValue);
                case SIZE:
                    return evalConditionValue(conditionValue, new JsonPrimitive(attributeValue.getAsJsonArray().size()));
            }
        } else if (attributeValue != null && attributeValue.isJsonPrimitive() && conditionValue.isJsonPrimitive()) {
            var targetPrimitiveValue = conditionValue.getAsJsonPrimitive();
            var sourcePrimitiveValue = attributeValue.getAsJsonPrimitive();
            switch (operator) {
                case EQ:
                    return Objects.equals(sourcePrimitiveValue, targetPrimitiveValue);
                case NE:
                    return !Objects.equals(sourcePrimitiveValue, targetPrimitiveValue);
                case LT:
                case LTE:
                case GT:
                case GTE:
                    return compare(operator, sourcePrimitiveValue, targetPrimitiveValue);
                case REGEX:
                    try {
                        var pattern = Pattern.compile(targetPrimitiveValue.getAsString());
                        var matcher = pattern.matcher(sourcePrimitiveValue.getAsString());
                        return matcher.find();
                    } catch (Exception e) {
                        return false;
                    }
            }
        }
        return false;
    }

    private static boolean compare(String operator, JsonPrimitive attributeValue, JsonPrimitive conditionValue) {
        if (attributeValue.isNumber() && conditionValue.isNumber()) {
            var x = attributeValue.getAsDouble();
            var y = conditionValue.getAsDouble();
            switch (operator) {
                case LT:
                    return x < y;
                case LTE:
                    return x <= y;
                case GT:
                    return x > y;
                case GTE:
                    return x >= y;
            }
        }
        if (attributeValue.isString() && conditionValue.isString()) {
            var x = attributeValue.getAsString();
            var y = conditionValue.getAsString();
            switch (operator) {
                case LT:
                    return x.compareTo(y) < 0;
                case LTE:
                    return x.compareTo(y) <= 0;
                case GT:
                    return x.compareTo(y) > 0;
                case GTE:
                    return x.compareTo(y) >= 0;
            }
        }
        return false;
    }
}
