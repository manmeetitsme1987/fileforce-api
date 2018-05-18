package fileforce.Configuration;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;


@Configuration
@MapperScan("fileforce.MapperWorker")
public class AppConfig {
	
	private org.apache.tomcat.jdbc.pool.DataSource pool;
	
	@Value("${tomcat.datasource.driverClassName}")
	private String driverClassName; 
	
	@Value("${tomcat.datasource.url}")
	private String url;
	
	@Value("${tomcat.datasource.username}")
	private String username;
	
	@Value("${tomcat.datasource.password}")
	private String password;
	
	@Value("${tomcat.datasource.maxActive}")
	private int maxActive;
	
	@Value("${tomcat.datasource.maxIdle}")
	private int maxIdle;
	
	@Value("${tomcat.datasource.minIdle}")
	private int minIdle;
	
	@Value("${tomcat.datasource.initialSize}")
	private int initialSize;
	
	@Value("${tomcat.datasource.testOnBorrow:FALSE}")
	private boolean testOnBorrow;
	
	@Value("${tomcat.datasource.testOnReturn:FALSE}")
	private boolean testOnReturn;
	
	@Value("${connected.app.consumer.key1}")
	private String key1;
	
	@Value("${connected.app.consumer.key2}")
	private String key2;
	
	@Value("${jks.file.name}")
	private String jksFileName;
	
	@Value("${jks.file.password}")
	private String jksFilePassword;
	
	public String getJksFileName() {
		return jksFileName;
	}

	public void setJksFileName(String jksFileName) {
		this.jksFileName = jksFileName;
	}

	public String getJksFilePassword() {
		return jksFilePassword;
	}

	public void setJksFilePassword(String jksFilePassword) {
		this.jksFilePassword = jksFilePassword;
	}

	public String getJksAlias() {
		return jksAlias;
	}

	public void setJksAlias(String jksAlias) {
		this.jksAlias = jksAlias;
	}

	public String getJksAliasPassword() {
		return jksAliasPassword;
	}

	public void setJksAliasPassword(String jksAliasPassword) {
		this.jksAliasPassword = jksAliasPassword;
	}

	@Value("${jks.file.alias}")
	private String jksAlias;
	
	@Value("${jks.file.alias.password}")
	private String jksAliasPassword;
	
	public String getKey1() {
		return key1;
	}

	public void setKey1(String key1) {
		this.key1 = key1;
	}

	public String getKey2() {
		return key2;
	}

	public void setKey2(String key2) {
		this.key2 = key2;
	}
	
	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	@Bean(destroyMethod = "close")
	public DataSource getDataSource() {
		this.pool = new org.apache.tomcat.jdbc.pool.DataSource();
		this.pool.setDriverClassName(driverClassName);
		this.pool.setUrl(url);
		this.pool.setUsername(username);
		this.pool.setPassword(password);
		this.pool.setInitialSize(initialSize);
		this.pool.setMaxActive(maxActive);
		this.pool.setMaxIdle(maxIdle);
		this.pool.setMinIdle(minIdle);
		this.pool.setTestOnBorrow(testOnBorrow);
		this.pool.setTestOnReturn(testOnReturn);
		//this.pool.setValidationQuery(validationQuery);
		return this.pool;
	}
	
	@PreDestroy
	public void close() {
		if (this.pool != null) {
			this.pool.close();
		}
	}
	
   @Bean
   public DataSourceTransactionManager transactionManager() {
       return new DataSourceTransactionManager(getDataSource());
   }
   
   @Bean
   public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
       String propertiesFilename = "application.properties";
       PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
       configurer.setLocation(new ClassPathResource(propertiesFilename));
       return configurer;
   }
   
   @Bean
   public SqlSessionFactory sqlSessionFactory() throws Exception {
      SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
      sessionFactory.setDataSource(getDataSource());
      return sessionFactory.getObject();
   }
}
