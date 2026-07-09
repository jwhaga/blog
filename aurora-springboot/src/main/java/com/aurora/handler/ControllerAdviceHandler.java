package com.aurora.handler;

import com.aurora.enums.StatusCodeEnum;
import com.aurora.exception.BizException;
import com.aurora.model.vo.ResultVO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class ControllerAdviceHandler {

    @ExceptionHandler(BizException.class)
    public ResultVO<?> handleBizException(BizException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return ResultVO.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : StatusCodeEnum.VALID_ERROR.getDesc();
        log.warn("参数校验失败(MethodArgumentNotValid): {}", message);
        return ResultVO.fail(StatusCodeEnum.VALID_ERROR.getCode(), message);
    }

    @ExceptionHandler(BindException.class)
    public ResultVO<?> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : StatusCodeEnum.VALID_ERROR.getDesc();
        log.warn("参数绑定失败(BindException): {}", message);
        return ResultVO.fail(StatusCodeEnum.VALID_ERROR.getCode(), message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResultVO<?> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse(StatusCodeEnum.VALID_ERROR.getDesc());
        log.warn("参数校验失败(ConstraintViolation): {}", message);
        return ResultVO.fail(StatusCodeEnum.VALID_ERROR.getCode(), message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultVO<?> handleMissingParam(MissingServletRequestParameterException e) {
        String message = "缺少必填参数: " + e.getParameterName();
        log.warn("缺少必填参数: {}", e.getParameterName());
        return ResultVO.fail(StatusCodeEnum.VALID_ERROR.getCode(), message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultVO<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持: {} {}", e.getMethod(), e.getMessage());
        return ResultVO.fail(StatusCodeEnum.METHOD_NOT_ALLOWED.getCode(), StatusCodeEnum.METHOD_NOT_ALLOWED.getDesc());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResultVO<?> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        log.warn("文件上传超限: {}", e.getMessage());
        return ResultVO.fail(StatusCodeEnum.VALID_ERROR.getCode(), "文件大小超过限制");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResultVO<?> handleNoHandlerFound(NoHandlerFoundException e) {
        log.warn("资源不存在: {} {}", e.getHttpMethod(), e.getRequestURL());
        return ResultVO.fail(StatusCodeEnum.NOT_FOUND.getCode(), StatusCodeEnum.NOT_FOUND.getDesc());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResultVO<?> handleNoResourceFound(NoResourceFoundException e) {
        log.warn("静态资源不存在: {} {}", e.getHttpMethod(), e.getResourcePath());
        return ResultVO.fail(StatusCodeEnum.NOT_FOUND.getCode(), StatusCodeEnum.NOT_FOUND.getDesc());
    }

    @ExceptionHandler(Exception.class)
    public ResultVO<?> handleException(Exception e) {
        log.error("系统异常", e);
        return ResultVO.fail(StatusCodeEnum.SYSTEM_ERROR.getCode(), StatusCodeEnum.SYSTEM_ERROR.getDesc());
    }

}
