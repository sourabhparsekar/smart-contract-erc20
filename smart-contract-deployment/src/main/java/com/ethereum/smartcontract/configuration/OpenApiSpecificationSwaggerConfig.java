package com.ethereum.smartcontract.configuration;

import com.ethereum.smartcontract.utils.Constants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * @author sourabh
 * @implNote Configuration class for Open API Specifications
 */
@Configuration
public class OpenApiSpecificationSwaggerConfig {

    static final String ETHER = "Alchemy URL";

    /**
     * Open API Configuration Bean
     *
     * @param title
     * @param version
     * @param description
     * @return
     */
    @Bean
    public OpenAPI openApiConfiguration(
            @Value("${openapi.title}") final String title,
            @Value("${openapi.version}") final String version,
            @Value("${openapi.description}") final String description
    ) {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Ethereum Node URL").addList("Private Key"))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "Ethereum Node URL",
                                        new SecurityScheme()
                                                .name(Constants.ETHEREUM_NODE_URL)
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.HEADER)
                                                .description("To generate alchemy url, refer https://docs.alchemy.com/alchemy/introduction/getting-started")
                                )
                                .addSecuritySchemes(
                                        "Private Key",
                                        new SecurityScheme()
                                                .name(Constants.PRIVATE_KEY)
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.HEADER)
                                                .description("Private Key to generate Credentials.")

                                )
                )
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description)
                        .license(getLicense())
                        .contact(getContact())
                );
    }

    /**
     * Contact details for the developer(s)
     *
     * @return
     */
    private Contact getContact() {
        Contact contact = new Contact();
        contact.setEmail("sourabhanant@gmail.com");
        contact.setName("Sourabh Parsekar");
        contact.setUrl("https://sourabhparsekar.medium.com/");
        contact.setExtensions(Collections.emptyMap());
        return contact;
    }

    /**
     * License creation
     *
     * @return
     */
    private License getLicense() {
        License license = new License();
        license.setName("Apache License, Version 2.0");
        license.setUrl("http://www.apache.org/licenses/LICENSE-2.0");
        license.setExtensions(Collections.emptyMap());
        return license;
    }

}
