package fileforce;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;

import fileforce.Configuration.TomcatPoolDataSourcePropertiesWorker;
import fileforce.Controller.AsyncProcessWorker;

@Configuration
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class )
@EnableConfigurationProperties(TomcatPoolDataSourcePropertiesWorker.class)
@MapperScan("fileforce.MapperWorker")
@Controller
@SpringBootApplication
public class FileForceApplicationWorker {
	
	@Bean
	public String createIndexJobListener() {
		AsyncProcessWorker.createIndexJobListener();
		return "Success";
	}
	
	public static void main(String[] args) {
		SpringApplication.run(FileForceApplicationWorker.class, args);
	}
}
