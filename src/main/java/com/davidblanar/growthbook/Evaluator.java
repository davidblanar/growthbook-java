package com.davidblanar.growthbook;

import java.util.Map;

public class Evaluator {
    private final String OR = "$or";
    private final String NOR = "$nor";
    private final String AND = "$and";
    private final String NOT = "$not";

    public boolean evalCondition(Map<String, Object> attributes, Map<String, Object> condition) {
        if (condition.containsKey(OR)) {

        }
        if (condition.containsKey(NOR)) {

        }
        if (condition.containsKey(AND)) {

        }
        if (condition.containsKey(NOT)) {

        }
        return true;
    }
//    private boolean evalOr(Map<String, Object> attributes, conditions: Condition[]) {
//
//    }
//    private evalAnd(attributes: Attributes, conditions: Condition[]): boolean
//    private isOperatorObject(obj): boolean
//    private getType(attributeValue): string
//    private getPath(attributes: Attributes, path: string): any
//    private evalConditionValue(conditionValue, attributeValue): boolean
//    private elemMatch(condition, attributeValue): boolean
//    private evalOperatorCondition(operator, attributeValue, conditionValue)
}
