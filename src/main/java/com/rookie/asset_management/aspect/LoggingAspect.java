package com.rookie.asset_management.aspect;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/** LoggingAspect class for logging information, messages, and exceptions */
@Component
@Aspect
public class LoggingAspect extends BaseAspect {

  private final ObjectMapper objectMapper;

  public LoggingAspect() {
    super(Logger.getLogger(LoggingAspect.class.getName()));
    // Initialize ObjectMapper with pretty printing and JavaTimeModule for date/time serialization
    this.objectMapper =
        new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT) // Enable pretty printing
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS) // Do not fail on empty beans
            .registerModule(
                new JavaTimeModule()) // Register JavaTimeModule for date/time serialization
            .disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Use ISO-8601 format for dates
  }

  /** Pointcut for all methods in classes ending with ServiceImpl */
  @Pointcut("execution(* com.rookie.asset_management.service.impl..*ServiceImpl.*(..))")
  public void serviceImplPointcut() {}

  /** Combined pointcut for all logging scenarios */
  @Pointcut("serviceImplPointcut()")
  public void loggingPointcut() {}

  /**
   * Around advice to log method entry and exit
   *
   * @param joinPoint the join point
   * @return the result of the method execution
   * @throws Throwable if an error occurs during method execution
   */
  @Around("loggingPointcut()")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

    Map<String, String> information = getInformation(joinPoint);
    String className = information.get("class");
    String methodName = information.get("method");
    Object[] args = joinPoint.getArgs();
    // Log method entry
    if (logger.isLoggable(Level.FINE)) {
      logEntry(className, methodName, args);
    }
    // Execute the method
    long startTime = System.currentTimeMillis();
    Object result;
    result = joinPoint.proceed();
    long endTime = System.currentTimeMillis();

    if (logger.isLoggable(Level.INFO)) {
      logger.info("Class: " + className + ", Method: " + methodName);

      // Pretty print the result as JSON
      try {
        String jsonResult = objectMapper.writeValueAsString(result);
        logger.info("Result: \n" + jsonResult);
      } catch (Exception e) {
        // Fallback to toString() if JSON serialization fails
        logger.info("Result: " + result);
        logger.warning("Failed to serialize result to JSON: " + e.getMessage());
      }

      logger.info("Execution time: " + (endTime - startTime) + " ms");
    }

    return result;
  }

  /**
   * After throwing advice to log exceptions
   *
   * @param joinPoint the join point
   * @param exception the exception thrown
   */
  @AfterThrowing(pointcut = "loggingPointcut()", throwing = "exception")
  public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
    logEx(joinPoint, exception);
  }

  /**
   * Before advice to log method arguments for all controller methods
   *
   * @param joinPoint the join point listening to all methods in controller classes
   */
  @Before("execution(* com.rookie.asset_management.controller.*Controller.*(..))")
  public void loggingControllerArgs(JoinPoint joinPoint) {
    Map<String, String> information = getInformation(joinPoint);
    Object[] args = joinPoint.getArgs();
    if (logger.isLoggable(Level.FINE)) {
      logEntry(information.get("class"), information.get("method"), args);
    }
  }
}
