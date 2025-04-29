package kr.co.triphos.common.aspect;

import kr.co.triphos.common.exception.CustomException;
import kr.co.triphos.common.exception.UnExpectedException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

// 에러 핸들러 관점
@Aspect
public class ExceptionHandlerAspect {

    // 예상치 못한 에러는 UnExpectedException으로 Wrapping
    /*@Around("execution(* kr.co.triphos..*(..))")*/
    /*@Around("execution(* kr.co.triphos..*Service.*(..))")*/
    @Around("execution(* kr.co.triphos..*Service.*(..)) || execution(* kr.co.triphos..*Controller.*(..)) || execution(* kr.co.triphos..*Repository.*(..))")
    public Object wrapException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new UnExpectedException(e);
        }
    }
}
