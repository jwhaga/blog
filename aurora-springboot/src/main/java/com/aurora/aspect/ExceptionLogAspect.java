package com.aurora.aspect;

import com.alibaba.fastjson.JSON;
import com.aurora.entity.ExceptionLog;
import com.aurora.event.ExceptionLogEvent;
import com.aurora.util.ExceptionUtil;
import com.aurora.util.IpUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
public class ExceptionLogAspect {

    @Autowired
    private ApplicationContext applicationContext;

    @Pointcut("execution(* com.aurora.controller..*.*(..))")
    public void exceptionLogPointcut() {
    }

    @AfterThrowing(value = "exceptionLogPointcut()", throwing = "e")
    public void saveExceptionLog(JoinPoint joinPoint, Exception e) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        if (request == null) {
            return;
        }
        ExceptionLog exceptionLog = new ExceptionLog();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Operation operation = method.getAnnotation(Operation.class);
        exceptionLog.setOptUri(request.getRequestURI());
        exceptionLog.setOptMethod(buildFullMethodName(joinPoint));
        exceptionLog.setRequestMethod(request.getMethod());
        exceptionLog.setRequestParam(getRequestArgs(joinPoint));
        if (Objects.nonNull(operation)) {
            exceptionLog.setOptDesc(operation.summary());
        } else {
            exceptionLog.setOptDesc("");
        }
        exceptionLog.setExceptionInfo(ExceptionUtil.getTrace(e));
        String ipAddress = getIpAddress(request);
        exceptionLog.setIpAddress(ipAddress);
        exceptionLog.setIpSource(IpUtil.getIpSource(ipAddress));
        applicationContext.publishEvent(new ExceptionLogEvent(exceptionLog));
    }

    private String buildFullMethodName(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        return className + "." + methodName;
    }

    private String getRequestArgs(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) {
            return null;
        }
        for (Object arg : args) {
            if (arg instanceof MultipartFile) {
                return "file";
            }
        }
        return JSON.toJSONString(args);
    }

    private String getIpAddress(HttpServletRequest request) {
        return IpUtil.getIpAddress(request);
    }

}
