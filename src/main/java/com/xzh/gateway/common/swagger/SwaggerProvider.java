package com.xzh.gateway.common.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 向振华
 * @date 2020/07/01 09:52
 */
@Primary
@Component
public class SwaggerProvider implements SwaggerResourcesProvider {

    public static final String API_URI = "/v2/api-docs";

    public static final String PROD = "prod";

    @Value("${spring.profiles.active}")
    private String active;

    @Resource
    private RouteDefinitionLocator routeDefinitionLocator;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        if (PROD.equals(active)) {
            return resources;
        }
        routeDefinitionLocator.getRouteDefinitions().subscribe(routeDefinition -> routeDefinition.getPredicates()
                .forEach(predicateDefinition -> resources.add(
                        swaggerResource(routeDefinition.getId(),
                                predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0")
                                        .replace("/**", API_URI)))));
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        return swaggerResource;
    }
}