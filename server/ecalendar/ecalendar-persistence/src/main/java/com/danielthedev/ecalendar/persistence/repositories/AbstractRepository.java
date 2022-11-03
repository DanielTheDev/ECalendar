package com.danielthedev.ecalendar.persistence.repositories;

import java.io.Serializable;
import java.util.function.Function;

import com.danielthedev.ecalendar.domain.entities.IEntity;
import com.danielthedev.ecalendar.domain.entities.RepeatingAttribute;
import com.danielthedev.ecalendar.persistence.DatabaseSingleton;

public abstract class AbstractRepository {

	private final DatabaseSingleton database = DatabaseSingleton.getInstance();
	
	public <T extends IEntity> T insertEntity(T entity) {
		return this.database.startTransaction((session)->{
			session.save(entity);
			return entity;
		});
	}
	
	public <T extends IEntity> T updateEntity(T entity) {
		return this.database.startTransaction((session)->{
			session.update(entity);
			return entity;
		});
	}
	

	public <T extends IEntity> T insertUpdateEntity(T entity) {
		return this.database.startTransaction((session)->{
			session.saveOrUpdate(entity);
			return entity;
		});
	}
	
	public <T extends IEntity, K> K loadEntity(T entity, Function<T, K> callback) {
		return this.database.startSession((session)->{
			T result = (T) session.byId(entity.getClass()).load(entity.getID());
			return callback.apply(result);
		});
	}
	
	public void deleteEntity(Object entity) {
		this.database.startTransaction((session)->{
			session.delete(entity);
			return null;
		});
	}
	
	public <T extends IEntity> T getEntityById(Class<T> entityClass, Serializable id) {
		return this.database.startSession((session)->{
			return session.byId(entityClass).load(id);
		});
	}
	
	public <T extends IEntity> T getEntityByNaturalId(Class<T> entityClass, String field, Serializable id) {
		return this.database.startSession((session)->{
			return session.byNaturalId(entityClass).using(field, id).load();
		});
	}
	
	public DatabaseSingleton getDatabase() {
		return database;
	}


}
