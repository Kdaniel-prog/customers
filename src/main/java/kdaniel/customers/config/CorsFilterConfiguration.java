package kdaniel.customers.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

/**
 * @Author Kiszel Dániel
 * @Date 2025-04-26
 * @Description Controller for managing customers.
 * Handles requests for getting customer data, modifying customer details,
 * and deleting customers. Only accessible to ADMIN for sensitive actions.
 */

@Configuration
@EnableWebMvc
public class CorsFilterConfiguration implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(String.valueOf(allowedOrigins))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*");
    }

}
