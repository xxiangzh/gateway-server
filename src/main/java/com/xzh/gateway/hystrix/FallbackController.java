package com.xzh.gateway.hystrix;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.xzh.gateway.entity.Constant.FALLBACK_MESSAGE;

/**
 * 服务降级
 *
 * @author 向振华
 * @date 2021/02/03 17:04
 */
@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public Object fallback() {
        return FALLBACK_MESSAGE;
    }
}