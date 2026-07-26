package cpt.api.account_management_service.config;

import cpt.api.account_management_service.interceptor.GlobalRequestValidationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GlobalRequestValidationInterceptor())
                .addPathPatterns("/api/**"); // TODO - Parameterize this in the application.properties to enable easy configuration of patterns
    }
}
