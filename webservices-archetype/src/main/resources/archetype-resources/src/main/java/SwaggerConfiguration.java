package ${package};

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import com.wordnik.swagger.model.ApiInfo;
 
@EnableSwagger
@Configuration
public class SwaggerConfiguration {
    @Inject
    private SpringSwaggerConfig springSwaggerConfig;
 

    @Value("${documentation.services.version}")
    private String apiVersion;
    
    @Bean
    public SwaggerSpringMvcPlugin createSwaggerPlugin() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiVersion(this.apiVersion)
                .apiInfo(new ApiInfo( // TODO: change the fields below as appropriate
                        "Webservice API",
                        "Web Application for Doing Stuff",
                        null, // terms of service url
                        null, // contact
                        null, // license
                        null // license url
                ));
    }
}
