package com.xzh.gateway.error;

import com.xzh.gateway.entity.Constant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 全局异常处理
 *
 * @author 向振华
 * @date 2020/07/01 09:52
 */
public class ErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    private static final Log logger = LogFactory.getLog(DefaultErrorWebExceptionHandler.class);

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
        Map<String, Object> errorAttributes = new HashMap<>(4);
        if (error instanceof BusinessException) {
            errorAttributes.put("code", ((BusinessException) error).getCode());
            errorAttributes.put("message", ((BusinessException) error).getMsg());
        } else if (error instanceof NotFoundException) {
            errorAttributes.put("code", Constant.ERROR_CODE);
            errorAttributes.put("message", "服务暂不可用，请稍后重试");
        } else if (error instanceof TimeoutException) {
            errorAttributes.put("code", Constant.ERROR_CODE);
            errorAttributes.put("message", "请求超时，请稍后重试");
        } else if (error instanceof ResponseStatusException) {
            errorAttributes.put("code", Constant.NOT_FOUND);
            errorAttributes.put("message", "404 NOT FOUND");
        } else {
            errorAttributes.put("code", Constant.ERROR_CODE);
            errorAttributes.put("message", Constant.ERROR_MESSAGE);
        }
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
        if (!CollectionUtils.isEmpty(errorAttributes)) {
            if (Constant.NOT_FOUND == (int) errorAttributes.get("code")) {
                return HttpStatus.NOT_FOUND;
            }
        }
        return HttpStatus.OK;
    }

    /**
     * 打印异常信息
     *
     * @param request
     * @param errorStatus
     */
    @Override
    protected void logError(ServerRequest request, HttpStatus errorStatus) {
        Throwable ex = super.getError(request);
        this.log(request, ex, errorStatus.is5xxServerError() ? logger::error : logger::warn);
    }

    private void log(ServerRequest request, Throwable ex, BiConsumer<Object, Throwable> logger) {
        if (ex instanceof BusinessException) {

        } else if (ex instanceof ResponseStatusException) {
            logger.accept(this.buildMessage(request, ex), (Throwable) null);
        } else {
            logger.accept(this.buildMessage(request, (Throwable) null), ex);
        }
    }

    private String buildMessage(ServerRequest request, Throwable ex) {
        StringBuilder message = new StringBuilder("Failed to handle request [");
        message.append(request.methodName());
        message.append(" ");
        message.append(request.uri());
        message.append("]");
        if (ex != null) {
            message.append(": ");
            message.append(ex.getMessage());
        }
        return message.toString();
    }
}
