package dev.yudin.aspect;


import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AopExpression {

	@Pointcut("execution(* dev.yudin.controllers.*.*(..))")
	private void forControllerPackage(){}

	@Pointcut("execution(* dev.yudin.services.*.*(..))")
	private void forServicePackage(){}

	@Pointcut("execution(* dev.yudin.dao.*.*(..))")
	private void forDaoPackage(){}

	@Pointcut("forControllerPackage() ||  forServicePackage() || forDaoPackage()")
	public void forAppFlow() {}
}
