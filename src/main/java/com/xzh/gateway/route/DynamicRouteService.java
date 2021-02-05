package com.xzh.gateway.route;

import com.xzh.gateway.entity.RouteDefinitionDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态路由服务
 *
 * @author 向振华
 * @date 2021/02/04 10:26
 */
@Service
public class DynamicRouteService implements ApplicationEventPublisherAware {

    @Resource
    private RouteDefinitionLocator routeDefinitionLocator;

    @Resource
    private RouteDefinitionWriter routeDefinitionWriter;

    private ApplicationEventPublisher publisher;

    /**
     * 获取路由
     *
     * @return
     */
    public Flux<RouteDefinition> get() {
        return routeDefinitionLocator.getRouteDefinitions();
    }

    /**
     * 更新路由
     *
     * @param dtos
     */
    public void updateRoutes(List<RouteDefinitionDTO> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            return;
        }
        for (RouteDefinitionDTO dto : dtos) {
            RouteDefinition routeDefinition = convertRouteDefinition(dto);
            update(routeDefinition);
        }
    }

    /**
     * 更新路由
     *
     * @param definition
     */
    public void update(RouteDefinition definition) {
        delete(definition.getId());
        save(definition);
    }

    /**
     * 删除路由
     *
     * @param id
     */
    public void delete(String id) {
        try {
            routeDefinitionWriter.delete(Mono.just(id)).subscribe();
        } catch (Exception ignored) {
        }
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 保存路由
     *
     * @param definition
     */
    public void save(RouteDefinition definition) {
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 参数转换成路由对象
     *
     * @param dto
     * @return
     */
    private RouteDefinition convertRouteDefinition(RouteDefinitionDTO dto) {
        RouteDefinition definition = new RouteDefinition();
        // 设置id
        definition.setId(dto.getId());
        // 设置order
        definition.setOrder(dto.getOrder());
        // 设置断言
        List<PredicateDefinition> pdList = new ArrayList<>();
        List<RouteDefinitionDTO.PredicateDefinitionDTO> predicateDefinitionDTOList = dto.getPredicates();
        for (RouteDefinitionDTO.PredicateDefinitionDTO gpDefinition : predicateDefinitionDTOList) {
            PredicateDefinition predicate = new PredicateDefinition();
            predicate.setArgs(gpDefinition.getArgs());
            predicate.setName(gpDefinition.getName());
            pdList.add(predicate);
        }
        definition.setPredicates(pdList);
        // 设置过滤器
        List<FilterDefinition> filters = new ArrayList<>();
        List<RouteDefinitionDTO.FilterDefinitionDTO> gatewayFilters = dto.getFilters();
        for (RouteDefinitionDTO.FilterDefinitionDTO filterDefinition : gatewayFilters) {
            FilterDefinition filter = new FilterDefinition();
            filter.setName(filterDefinition.getName());
            filter.setArgs(filterDefinition.getArgs());
            filters.add(filter);
        }
        definition.setFilters(filters);
        // 设置uri
        URI uri = null;
        String uriStr = dto.getUri();
        if (StringUtils.isNotBlank(uriStr)) {
            String http = "http";
            if (uriStr.startsWith(http)) {
                uri = UriComponentsBuilder.fromHttpUrl(dto.getUri()).build().toUri();
            } else {
                uri = URI.create(dto.getUri());
            }
        }
        definition.setUri(uri);
        return definition;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}