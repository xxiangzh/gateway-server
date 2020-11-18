package com.xzh.gateway.common.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author 向振华
 * @date 2020/07/01 09:52
 */
@RestController
@RequestMapping("/swagger-resources")
public class SwaggerHandler {

    @Autowired(required = false)
    private UiConfiguration uiConfiguration;

    @Autowired(required = false)
    private SecurityConfiguration securityConfiguration;

    @Resource
    private SwaggerProvider swaggerProvider;

    @GetMapping("/configuration/ui")
    public Mono<ResponseEntity<UiConfiguration>> uiConfiguration() {
        return Mono.just(new ResponseEntity<>(
                Optional.ofNullable(uiConfiguration).orElse(UiConfigurationBuilder.builder().build()), HttpStatus.OK));
    }

    @GetMapping("/configuration/security")
    public Mono<ResponseEntity<SecurityConfiguration>> securityConfiguration() {
        return Mono.just(new ResponseEntity<>(
                Optional.ofNullable(securityConfiguration).orElse(SecurityConfigurationBuilder.builder().build()), HttpStatus.OK));
    }

    @GetMapping
    public Mono<ResponseEntity> swaggerResources() {
        List<SwaggerResource> list = swaggerProvider.get();
        return Mono.just((new ResponseEntity<>(list, HttpStatus.OK)));
    }
}
