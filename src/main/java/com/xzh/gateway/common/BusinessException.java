package com.xzh.gateway.common;

import lombok.Data;

/**
 * 业务异常
 *
 * @author 向振华
 * @date 2020/06/05 11:34
 */
@Data
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private int code;

    /**
     * 提示信息
     */
    private String msg;

    public BusinessException(String msg) {
        super(msg);
        this.code = Constant.CODE_GATEWAY;
        this.msg = msg;
    }

    public BusinessException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
