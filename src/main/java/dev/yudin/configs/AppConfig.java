package dev.yudin.configs;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import dev.yudin.exceptions.AppConfigurationException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;

@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = "dev.yudin")
@PropertySources({
		@PropertySource("classpath:jdbc-connection-postgresql.properties"),
		@PropertySource("classpath:admin-jdbc-connection-postgresql.properties")
})
@Configuration
public class AppConfig implements WebMvcConfigurer {
	@Autowired
	private Environment propertyDataHolder;
	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public SpringResourceTemplateResolver templateResolver() {

		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();

		templateResolver.setApplicationContext(applicationContext);
		templateResolver.setPrefix("/WEB-INF/views/");
		templateResolver.setSuffix(".html");

		return templateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {

		SpringTemplateEngine templateEngine = new SpringTemplateEngine();

		templateEngine.setTemplateResolver(templateResolver());
		templateEngine.setEnableSpringELCompiler(true);

		return templateEngine;
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {

		ThymeleafViewResolver resolver = new ThymeleafViewResolver();

		resolver.setTemplateEngine(templateEngine());
		registry.viewResolver(resolver);
	}

	@Bean
	public DataSource dataSource() {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();

		String jdbcDriver = propertyDataHolder.getProperty("jdbc.driver");
		String jdbcUrl = propertyDataHolder.getProperty("jdbc.url");
		String jdbcUser = propertyDataHolder.getProperty("jdbc.user");
		String jdbcPassword = propertyDataHolder.getProperty("jdbc.password");

		try {
			dataSource.setDriverClass(jdbcDriver);
		} catch (PropertyVetoException e) {
			throw new AppConfigurationException("Error happened during set Driver class", e);

		}
		dataSource.setJdbcUrl(jdbcUrl);
		dataSource.setUser(jdbcUser);
		dataSource.setPassword(jdbcPassword);
		// connection pool properties for C3PO
		dataSource.setMinPoolSize(5);
		dataSource.setMaxPoolSize(20);
		dataSource.setMaxIdleTime(30_000);
		return dataSource;
	}

	@Bean
	public DataSource adminDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		String jdbcDriver = propertyDataHolder.getProperty("admin.jdbc.driver");
		String jdbcUrl = propertyDataHolder.getProperty("admin.jdbc.url");
		String jdbcUser = propertyDataHolder.getProperty("admin.jdbc.user");
		String jdbcPassword = propertyDataHolder.getProperty("admin.jdbc.password");
		dataSource.setDriverClassName(jdbcDriver);
		dataSource.setUrl(jdbcUrl);
		dataSource.setUsername(jdbcUser);
		dataSource.setPassword(jdbcPassword);
		return dataSource;
	}

	@Bean
	public SessionFactory sessionFactory() throws IOException {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setPackagesToScan("dev.yudin");
		sessionFactory.setHibernateProperties(hibernateProperties());
		sessionFactory.afterPropertiesSet();
		return sessionFactory.getObject();
	}

	private Properties hibernateProperties() {
		Properties prop = new Properties();
		prop.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		prop.put("hibernate.show_sql", true);
		return prop;
	}

	@Bean
	public HibernateTransactionManager transactionManager() throws IOException {
		var manager =  new HibernateTransactionManager();
		manager.setSessionFactory(sessionFactory());
		return manager;
	}
}
