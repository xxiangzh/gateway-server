package com.xzh.gateway.common.error;

import com.xzh.gateway.common.BusinessException;
import com.xzh.gateway.common.Constant;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理
 *
 * @author 向振华
 * @date 2020/07/01 09:52
 */
public class ErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    public ErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                    ResourceProperties resourceProperties,
                                    ErrorProperties errorProperties,
                                    ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 自定义异常返回值
     *
     * @param request
     * @param includeStackTrace
     * @return
     */
    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        Throwable error = super.getError(request);
        Map<String, Object> errorAttributes = new HashMap<>(8);
        if (error instanceof BusinessException) {
            errorAttributes.put("status", ((BusinessException) error).getCode());
        } else {
            errorAttributes.put("status", Constant.CODE_GATEWAY);
        }
        errorAttributes.put("message", error.getMessage());
        errorAttributes.put("info", null);
        errorAttributes.put("data", null);
        return errorAttributes;
    }

    /**
     * 指定响应处理方法
     *
     * @param errorAttributes
     * @return
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * 获取HTTP状态码
     *
     * @param errorAttributes
     * @return
     */
    @Override
    protected HttpStatus getHttpStatus(Map<String, Object> errorAttributes) {
        return HttpStatus.OK;
    }
}
