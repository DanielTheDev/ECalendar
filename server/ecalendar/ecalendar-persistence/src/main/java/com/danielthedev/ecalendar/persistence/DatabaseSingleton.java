package com.danielthedev.ecalendar.persistence;

import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.danielthedev.ecalendar.domain.entities.CalendarEntity;
import com.danielthedev.ecalendar.domain.entities.CalendarItemEntity;
import com.danielthedev.ecalendar.domain.entities.RepeatingAttribute;
import com.danielthedev.ecalendar.domain.entities.SharedCalendarEntity;
import com.danielthedev.ecalendar.domain.entities.SharedCalendarItemEntity;
import com.danielthedev.ecalendar.domain.entities.UserEntity;

public class DatabaseSingleton {

	private static DatabaseSingleton INSTANCE;
	private static final Class<?>[] entities = new Class<?>[] {UserEntity.class, CalendarEntity.class, CalendarItemEntity.class, RepeatingAttribute.class, SharedCalendarEntity.class, SharedCalendarItemEntity.class};
	
	private final StandardServiceRegistry registry;
	private final MetadataSources sources;
	private final Metadata metadata;
	private final SessionFactory sessionFactory;
	
	private DatabaseSingleton() {
		this.registry = new StandardServiceRegistryBuilder().configure().build();
		this.sources = new MetadataSources(this.registry);
		this.addModels();
		this.metadata = sources.buildMetadata();
		this.sessionFactory = this.metadata.buildSessionFactory();
	}
	
	private void addModels() {
		for(Class<?> entity : entities) {
			this.sources.addAnnotatedClass(entity);
		}
	}
	
	public static DatabaseSingleton getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new DatabaseSingleton();
		}
		return INSTANCE;
	}
	
	public <T> T startSession(Function<Session, T> callback) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			return callback.apply(session);
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}
	
	public <T> T  startTransaction(Function<Session, T> callback) {
		return this.startSession((session)->{
			T result = null;
			try {
				session.beginTransaction();
				result = callback.apply(session);
				session.getTransaction().commit();
			} catch (Exception e) {
				e.printStackTrace();
				session.getTransaction().rollback();
			}
			return result;
		});
	}
	
}
