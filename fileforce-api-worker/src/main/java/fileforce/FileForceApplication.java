package fileforce;

import static java.lang.System.getenv;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.annotation.PreDestroy;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
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

import fileforce.Configuration.BigOperationWorker;
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
	
	protected final String helloWorldQueueName = "hello.world.queue";
	
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
	    
		//return null;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
		//return null;
	}
	
	@Bean
	 public ConnectionFactory connectionFactory() {
	     final URI rabbitMqUrl;
	     try {
	         rabbitMqUrl = new URI(getEnvOrThrow("CLOUDAMQP_URL"));
	     } catch (URISyntaxException e) {
	         throw new RuntimeException(e);
	     }

	     final CachingConnectionFactory factory = new CachingConnectionFactory();
	     factory.setUsername(rabbitMqUrl.getUserInfo().split(":")[0]);
	     factory.setPassword(rabbitMqUrl.getUserInfo().split(":")[1]);
	     factory.setHost(rabbitMqUrl.getHost());
	     factory.setPort(rabbitMqUrl.getPort());
	     factory.setVirtualHost(rabbitMqUrl.getPath().substring(1));

	     return factory;
	 }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setRoutingKey(this.helloWorldQueueName);
        template.setQueue(this.helloWorldQueueName);
        return template;
    }

    @Bean
    public Queue queue() {
        return new Queue(this.helloWorldQueueName);
    }
    
    @Bean
    MessageListenerAdapter testListenerAdapter(BigOperationWorker testReceiver) {
        return new MessageListenerAdapter(testReceiver);
    }
    

    private static String getEnvOrThrow(String name) {
        String env = getenv(name);
        if (env == null) {
            env = "amqp://iaxlxurw:ZYEtbFikacPOQ0qbi1iSmFwj9H6uKJ0c@eagle.rmq.cloudamqp.com/iaxlxurw";
        	//throw new IllegalStateException("Environment variable [" + name + "] is not set.");
        }
        return env;
    }
	
	public static void main(String[] args) {
		//BigOperationWorker.processRequest();
		SpringApplication.run(FileForceApplication.class, args);
	}
}
