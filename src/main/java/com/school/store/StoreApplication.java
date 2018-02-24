package com.school.store;

import com.school.store.base.repositoryFactory.BaseRepositoryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EntityScan(value = "com.school.store.admin.**.entity")
@EnableSpringDataWebSupport
@SpringBootApplication(scanBasePackages = {"com.school.store"})
@EnableJpaRepositories(basePackages = {"com.school.store.admin.**.service"},repositoryFactoryBeanClass = BaseRepositoryFactory.class)
@EnableAspectJAutoProxy // 开启aop
@EnableJpaAuditing // 允许 @CreatedBy 等注解的使用
public class StoreApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(StoreApplication.class, args);
	}

}
