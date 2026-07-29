package com.aqvp.platform.identity.support;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.annotation.MergedAnnotations;

/**
 * JUnit condition that enables a test only when Docker is available.
 */
public class DockerAvailableCondition implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext context) {
        if (!context.getElement().isPresent()) {
            return ConditionEvaluationResult.enabled("No annotated element");
        }
        final boolean hasAnnotation = MergedAnnotations.from(context.getElement().get())
            .isPresent(EnabledIfDocker.class);
        if (!hasAnnotation) {
            return ConditionEvaluationResult.enabled("Not applicable");
        }
        try {
            org.testcontainers.DockerClientFactory.instance().client();
            return ConditionEvaluationResult.enabled("Docker is available");
        } catch (Exception ex) {
            return ConditionEvaluationResult.disabled("Docker is not available: " + ex.getMessage());
        }
    }
}
