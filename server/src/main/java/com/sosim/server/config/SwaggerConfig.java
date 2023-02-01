package com.sosim.server.config;

import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Autowired
    private SysConfig sysConfig;

    private Info info(String title) {
        String description = "Sosim Server API Document";
        String contactName = "Sosim Server";
        String contactUrl = "https://sosim.sample.com";

        return new Info()
            .title(title)
            .description(description)
            .version(this.sysConfig.getVersion())
            .contact(new Contact()
                .name(contactName)
                .url(contactUrl))
            .license(
                new License()
                    .name("Apache License Version 2.0")
                    .url("https://github.com/springfox/springfox/blob/master/LICENSE")
            );
    }

    @Bean
    public OpenAPI customOpenAPI() {
        String keyName = "tkn: {tkn}";
        OpenAPI openAPI = new OpenAPI()
            .components(new Components()
                .addSecuritySchemes(keyName, new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER)
                    .name("tkn")
                )
            ).addSecurityItem(new SecurityRequirement().addList(keyName));

        List<Server> serverList = new ArrayList<>();
        serverList.add(new Server().url("http://ip:28080"));
        serverList.add(new Server().url("http://localhost:8080"));
        serverList.add(new Server().url("http://localhost:28081"));

        openAPI.setServers(serverList);
        openAPI.setInfo(info("Sosim API"));
        return openAPI;
    }

}
