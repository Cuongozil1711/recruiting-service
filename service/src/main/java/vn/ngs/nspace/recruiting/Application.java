package vn.ngs.nspace.recruiting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import vn.ngs.nspace.lib.annotation.EnableStringTrim;
import vn.ngs.nspace.lib.annotation.GlobalException;

@SpringBootApplication(scanBasePackages = "vn.ngs.nspace")
@EnableScheduling
@EnableStringTrim
@GlobalException
@EnableAsync
@EnableJpaRepositories("vn.ngs.nspace")
@EntityScan(basePackages = "vn.ngs.nspace")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
