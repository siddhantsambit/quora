package io.swagger.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-05-06T15:34:19.214+05:30")

@Configuration
public class SwaggerDocumentationConfig {

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("User Registration API")
            .description("## API of User Signup Service  This specification describes the endpoints, request/response model of the User Signup API. This API provides REST resources to register users in Proman application.  The resources should only be used over HTTPS (SSL) and can be accessed via the following base URL:  ```  https://proman.io/api/v1/ ```  ## API usage guidelines  This section describes general rules and guidelines for using REST based services offered by Proman.   * All resources can only be accessed via HTTPS and a respective HTTP Basic Authentication header is also required.   * Proman reserves the right to add new parameters without further notice (in request and response).   * Proman reserves the right to add new error codes without further notice. Proman will thereby stick to the HTTP STATUS CODES as defined by W3C.   * Proman reserves the right to enhance enumerations by new values.   * Proman reserves the right to increase the \"maxItem\" settings of arrays without further notice.    Each consumer needs to tolerant about changes to the API as much as possible ([see Tolerant Reader Pattern](http://martinfowler.com/bliki/TolerantReader.html))  ## Business definitions A user who is trying to register should provide the required inputs including email address in order to be registered successfully in Proman application. After the signup, user will remain in REGISTERED status and cannot access Proman application. Once approved by an administrator, user will turn to ACTIVE status and can start accessing Proman application.  ### Note: Proman application supports HTTPS only scheme. All requests are sent in a secured and encrypted manner and hence the password is never sent in a text/plain format. ### Password Policy:   * Minimum of 8 character length    * Should contain alphabets with at least one special character and one number    * Allowed special characters are # $ @ _ - !  ## Release Notes ### 1.0.0 * Initial version ")
            .license("")
            .licenseUrl("http://unlicense.org")
            .termsOfServiceUrl("")
            .version("1.0.0")
            .contact(new Contact("","", ""))
            .build();
    }

    @Bean
    public Docket customImplementation(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                    .apis(RequestHandlerSelectors.basePackage("io.swagger.api"))
                    .build()
                .directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(java.time.OffsetDateTime.class, java.util.Date.class)
                .apiInfo(apiInfo());
    }

}
