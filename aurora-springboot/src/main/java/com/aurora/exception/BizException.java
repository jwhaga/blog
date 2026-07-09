package com.aurora.exception;

import com.aurora.enums.StatusCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务异常。
 * 继承 RuntimeException 以避免强制 try-catch。
 * 所有构造函数均调用 super(message)，保证 Throwable.getMessage() 正常工作。
 */
@Getter
@AllArgsConstructor
public class BizException extends RuntimeException {

    private Integer code = StatusCodeEnum.FAIL.getCode();

    private final String message;

    public BizException(String message) {
        super(message);
        this.message = message;
    }

    public BizException(StatusCodeEnum statusCodeEnum) {
        super(statusCodeEnum.getDesc());
        this.code = statusCodeEnum.getCode();
        this.message = statusCodeEnum.getDesc();
    }

}
