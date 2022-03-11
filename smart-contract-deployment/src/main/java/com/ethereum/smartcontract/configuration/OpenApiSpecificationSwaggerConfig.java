package com.ethereum.smartcontract.configuration;

import com.ethereum.smartcontract.Constants;
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

    static final String SECURITY_SCHEME_NAME = "Alchemy URL";

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
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        new SecurityScheme()
                                                .name(Constants.ALCHEMY_URL_HEADER)
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.HEADER)
                                        .description("To generate url, refer https://docs.alchemy.com/alchemy/introduction/getting-started")




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
