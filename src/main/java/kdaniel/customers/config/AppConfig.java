package kdaniel.customers.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Author Kiszel Dániel
 * @Date 2025-04-26
 * @Description Application-wide configuration class.
 * Provides beans for ModelMapper (for DTO mapping) and BCryptPasswordEncoder (for password hashing).
 */

@Configuration
public class AppConfig {

    /**
     * @Description Provides a ModelMapper bean that skips null values during mapping.
     * Useful for partial updates (e.g., editing only non-null fields).
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        return modelMapper;
    }

    /**
     * @Description Provides a BCryptPasswordEncoder bean for hashing passwords securely.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
