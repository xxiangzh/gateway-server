package com.xzh.gateway.common.route;

import com.xzh.gateway.utils.AesUtils;
import com.xzh.gateway.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 向振华
 * @date 2020/09/08 17:26
 */
@RestController
@RequestMapping("route")
public class DynamicRouteController {

    @Resource
    private DynamicRouteService dynamicRouteService;

    /**
     * 获取路由
     */
    @GetMapping("/get/{args}")
    public Flux<RouteDefinition> get(@PathVariable String args) {
        String decrypt = AesUtils.decrypt(args);
        if (StringUtils.isBlank(decrypt) || !StringUtils.isNumeric(decrypt)) {
            return null;
        }
        if (System.currentTimeMillis() < Long.parseLong(decrypt)) {
            return null;
        }
        return dynamicRouteService.get();
    }

    /**
     * 更新路由
     */
    @PutMapping("/update")
    public void update(@RequestBody String args) {
        String decrypt = AesUtils.decrypt(args);
        if (StringUtils.isNotBlank(decrypt)) {
            List<RouteDefinitionDTO> dtos = JsonUtils.convertJson(decrypt, RouteDefinitionDTO.class);
            dynamicRouteService.updateRoutes(dtos);
        }
    }

    /**
     * 删除路由
     */
    @DeleteMapping("/delete/{args}")
    public void delete(@PathVariable String args) {
        String decrypt = AesUtils.decrypt(args);
        if (StringUtils.isNotBlank(decrypt)) {
            dynamicRouteService.delete(decrypt);
        }
    }
}