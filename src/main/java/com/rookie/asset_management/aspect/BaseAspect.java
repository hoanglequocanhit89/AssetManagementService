package com.rookie.asset_management.aspect;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public abstract class BaseAspect {
  /**
   * Logger instance for logging information, messages, and exceptions This attribute is protected
   * to allow subclasses to access it
   */
  protected final Logger logger;

  protected BaseAspect(Logger logger) {
    this.logger = logger;
  }

  /**
   * Get class name and method name from the join point
   *
   * @param joinPoint the join point
   * @return a map containing class name and method name
   */
  protected Map<String, String> getInformation(JoinPoint joinPoint) {
    // Extract class name, method name
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String methodName = signature.getMethod().getName();
    String className = joinPoint.getTarget().getClass().getName();
    return Map.of("class", className, "method", methodName);
  }

  /**
   * Log class name, method name, and method arguments
   *
   * @param className the name of the class
   * @param methodName the name of the method
   * @param args the arguments of the method
   */
  protected void logEntry(String className, String methodName, Object[] args) {
    if (logger.isLoggable(Level.FINE)) {
      logger.fine(String.format("Class: %s, Method: %s", className, methodName));
      Arrays.stream(args).forEach(arg -> logger.fine(String.format("Method arguments: %s", arg)));
    }
  }

  /**
   * Log an exception
   *
   * @param joinPoint the join point
   * @param throwable the exception to log
   */
  protected void logEx(JoinPoint joinPoint, Throwable throwable) {
    Map<String, String> information = getInformation(joinPoint);
    String className = information.get("class");
    String methodName = information.get("method");

    if (logger.isLoggable(Level.SEVERE)) {
      logger.severe(
          String.format(
              "Class: %s, Method: %s, Message: %s", className, methodName, throwable.getMessage()));
      // Log the stack trace of the exception
      try {
        // format the stack trace
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        logger.severe("Exception stack trace:\n" + sw);
      } catch (Exception e) {
        logger.warning("Failed to print stack trace: " + e.getMessage());
      }
    }
  }
}
