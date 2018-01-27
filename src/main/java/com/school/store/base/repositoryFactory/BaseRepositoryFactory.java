package com.school.store.base.repositoryFactory;

import java.io.Serializable;

import javax.persistence.EntityManager;


import com.school.store.base.repository.impl.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;



public class BaseRepositoryFactory<R extends JpaRepository<T, I>,T,I extends Serializable> extends JpaRepositoryFactoryBean<R, T, I> {
    public BaseRepositoryFactory(Class<? extends R> repositoryInterface) {
		super(repositoryInterface);
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		// TODO Auto-generated method stub
		return new MyRepositoryFactory(entityManager);
	}
	
	private static class MyRepositoryFactory<T,I extends Serializable> extends JpaRepositoryFactory{
	   private EntityManager em;

	public MyRepositoryFactory(EntityManager em) {
		super(em);
		this.em = em;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object getTargetRepository(RepositoryInformation information) {
		// TODO Auto-generated method stub
		return new BaseRepository<T, I>((Class<T>)information.getDomainType(), em);
	}

	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		// TODO Auto-generated method stub
		return BaseRepository.class;
	}
	   
	
	
	}
	
}
