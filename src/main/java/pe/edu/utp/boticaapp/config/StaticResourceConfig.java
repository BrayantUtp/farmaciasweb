package pe.edu.utp.boticaapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // map common static resource patterns to classpath:/static/
        registry.addResourceHandler("/css/**", "/js/**", "/img/**", "/static/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(3600);
        // specific files (ensure /home.css and catalogo/pedidos styles are served)
        registry.addResourceHandler("/home.css", "/styles.css", "/catalogo.css", "/pedidos.css", "/cart.css")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}
