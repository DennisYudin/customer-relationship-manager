package dev.yudin.aspect;

import lombok.extern.log4j.Log4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Log4j
@Aspect
@Component
@Order(1)
public class LoggingAspect {

	@Before("dev.yudin.aspect.AopExpression.forAppFlow()")
	public void before(JoinPoint joinPoint) {
		String method = joinPoint.getSignature().toShortString();
		log.info("====>> in @Before advice: calling method: " + method);

		Object[] args = joinPoint.getArgs();
		for (var arg : args) {
			log.info("====> Income argument: " + arg);
		}
	}

	@AfterReturning(
			pointcut = "dev.yudin.aspect.AopExpression.forAppFlow()",
			returning = "result")
	public void afterReturning(JoinPoint joinPoint, Object result) {
		String method = joinPoint.getSignature().toShortString();
		log.info("====>> in @AfterReturning advice: calling method: " + method);

		log.info("====> return of method: " + result);
	}
}
