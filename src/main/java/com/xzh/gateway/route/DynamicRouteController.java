package com.xzh.gateway.route;

import com.xzh.gateway.entity.RouteDefinitionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author 向振华
 * @date 2021/02/04 10:26
 */
@RestController
@RequestMapping("/route")
public class DynamicRouteController {

    @Autowired
    private DynamicRouteService dynamicRouteService;

    /**
     * 获取路由
     */
    @GetMapping
    public Flux<RouteDefinition> get() {
        return dynamicRouteService.get();
    }

    /**
     * 更新路由
     */
    @PutMapping
    public void update(@RequestBody List<RouteDefinitionDTO> dto) {
        dynamicRouteService.updateRoutes(dto);
    }

    /**
     * 删除路由
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        dynamicRouteService.delete(id);
    }
}