package de.renatius.poc.springboot.data.tracing;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * Creates spans for {@link WithSpan} annotated methods and for all Spring Data repository calls.
 * Parameters annotated with {@link SpanAttribute} are recorded as span attributes.
 */
@Aspect
@Component
public class WithSpanAspect {

  private static final String REPOSITORY_POINTCUT =
      "execution(* de.renatius.poc.springboot.data.repository.*.*(..))";

  private final Tracer tracer;

  public WithSpanAspect(ObjectProvider<OpenTelemetry> openTelemetry) {
    this.tracer = openTelemetry.getIfAvailable(OpenTelemetry::noop).getTracer("de.renatius.poc.springboot");
  }

  @Around("@annotation(io.opentelemetry.instrumentation.annotations.WithSpan)")
  public Object annotated(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    WithSpan withSpan = method.getAnnotation(WithSpan.class);
    String name =
        withSpan != null && !withSpan.value().isEmpty()
            ? withSpan.value()
            : signature.getDeclaringType().getSimpleName() + "." + method.getName();
    SpanKind kind = withSpan != null ? withSpan.kind() : SpanKind.INTERNAL;
    return trace(joinPoint, name, kind, method);
  }

  @Around(REPOSITORY_POINTCUT)
  public Object repository(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String name = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
    return trace(joinPoint, name, SpanKind.CLIENT, signature.getMethod());
  }

  private Object trace(ProceedingJoinPoint joinPoint, String name, SpanKind kind, Method method)
      throws Throwable {
    Span span = tracer.spanBuilder(name).setSpanKind(kind).startSpan();
    long start = System.nanoTime();
    Parameter[] parameters = method.getParameters();
    Object[] args = joinPoint.getArgs();
    for (int i = 0; i < parameters.length && i < args.length; i++) {
      SpanAttribute attribute = parameters[i].getAnnotation(SpanAttribute.class);
      if (attribute != null && args[i] != null) {
        String key = attribute.value().isEmpty() ? parameters[i].getName() : attribute.value();
        span.setAttribute("app." + key, String.valueOf(args[i]));
      }
    }
    try (Scope ignored = span.makeCurrent()) {
      return joinPoint.proceed();
    } catch (Throwable t) {
      span.recordException(t);
      span.setStatus(StatusCode.ERROR, String.valueOf(t.getMessage()));
      throw t;
    } finally {
      span.setAttribute("app.duration_ms", (System.nanoTime() - start) / 1_000_000.0);
      span.end();
    }
  }
}
