package fileforce;


import java.util.Properties;

import javax.annotation.PreDestroy;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;

import fileforce.Configuration.TomcatPoolDataSourceProperties;


@Configuration
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class )
@EnableConfigurationProperties(TomcatPoolDataSourceProperties.class)
@MapperScan("fileforce.Mapper")
@Controller
@SpringBootApplication
public class FileForceApplication {
	
	@Autowired
	private TomcatPoolDataSourceProperties tomcatPoolDataSourceProperties;
	private org.apache.tomcat.jdbc.pool.DataSource pool;
	
	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		TomcatPoolDataSourceProperties config = tomcatPoolDataSourceProperties;
		this.pool = new org.apache.tomcat.jdbc.pool.DataSource();
		this.pool.setDriverClassName(config.getDriverClassName());
		this.pool.setUrl(config.getUrl());
		if (config.getUsername() != null) {
			this.pool.setUsername(config.getUsername());
		}
		if (config.getPassword() != null) {
			this.pool.setPassword(config.getPassword());
		}
		this.pool.setInitialSize(config.getInitialSize());
		this.pool.setMaxActive(config.getMaxActive());
		this.pool.setMaxIdle(config.getMaxIdle());
		this.pool.setMinIdle(config.getMinIdle());
		this.pool.setTestOnBorrow(config.isTestOnBorrow());
		this.pool.setTestOnReturn(config.isTestOnReturn());
		this.pool.setValidationQuery(config.getValidationQuery());
		return this.pool;
	}
	
	@PreDestroy
	public void close() {
		if (this.pool != null) {
			this.pool.close();
		}
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());
		Properties configProps = new Properties();
        configProps.put("cacheEnabled", "false");
        sqlSessionFactoryBean.setConfigurationProperties(configProps);
        
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean
			.setMapperLocations(resolver.getResources("classpath:mapperXml/*.xml"));
		
		 return sqlSessionFactoryBean.getObject();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}
	
	public static void main(String[] args) {
		SpringApplication.run(FileForceApplication.class, args);
	}
}
