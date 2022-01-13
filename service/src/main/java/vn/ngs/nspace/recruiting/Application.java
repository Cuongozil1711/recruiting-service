package vn.ngs.nspace.recruiting;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import vn.ngs.nspace.lib.annotation.EnableStringTrim;
import vn.ngs.nspace.lib.annotation.GlobalException;

@SpringBootApplication(scanBasePackages = "vn.ngs.nspace")
@EnableScheduling
@EnableStringTrim
@GlobalException
@EnableAsync
@EnableJpaRepositories("vn.ngs.nspace")
@EntityScan(basePackages = "vn.ngs.nspace")
@OpenAPIDefinition(info = @Info(title = "Recruitment-service", version = "0.1", description = "Recruiting Information"))
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
