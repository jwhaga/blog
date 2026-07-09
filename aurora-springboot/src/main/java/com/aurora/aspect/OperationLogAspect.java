package com.aurora.aspect;

import com.alibaba.fastjson.JSON;
import com.aurora.annotation.OptLog;
import com.aurora.entity.OperationLog;
import com.aurora.event.OperationLogEvent;
import com.aurora.model.dto.UserDetailsDTO;
import com.aurora.util.IpUtil;
import com.aurora.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
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

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private ApplicationContext applicationContext;

    @Pointcut("@annotation(com.aurora.annotation.OptLog)")
    public void operationLogPointCut() {
    }

    @AfterReturning(value = "operationLogPointCut()", returning = "keys")
    public void saveOperationLog(JoinPoint joinPoint, Object keys) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        if (request == null) {
            return;
        }
        OperationLog operationLog = new OperationLog();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Tag tag = (Tag) signature.getDeclaringType().getAnnotation(Tag.class);
        Operation operation = method.getAnnotation(Operation.class);
        OptLog optLog = method.getAnnotation(OptLog.class);
        operationLog.setOptModule(tag != null ? tag.name() : "");
        operationLog.setOptType(optLog.optType());
        operationLog.setOptDesc(operation != null ? operation.summary() : "");
        operationLog.setOptMethod(buildFullMethodName(joinPoint));
        operationLog.setRequestMethod(request.getMethod());
        operationLog.setRequestParam(getRequestArgs(joinPoint));
        operationLog.setResponseData(JSON.toJSONString(keys));
        UserDetailsDTO userDetailsDTO = UserUtil.getUserDetailsDTO();
        if (userDetailsDTO != null) {
            operationLog.setUserId(userDetailsDTO.getId());
            operationLog.setNickname(userDetailsDTO.getNickname());
        }
        String ipAddress = getIpAddress(request);
        operationLog.setIpAddress(ipAddress);
        operationLog.setIpSource(IpUtil.getIpSource(ipAddress));
        operationLog.setOptUri(request.getRequestURI());
        applicationContext.publishEvent(new OperationLogEvent(operationLog));
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
