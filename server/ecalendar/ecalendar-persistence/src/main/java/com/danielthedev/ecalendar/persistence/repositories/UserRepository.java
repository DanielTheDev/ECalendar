package com.danielthedev.ecalendar.persistence.repositories;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.query.NativeQuery;

import com.danielthedev.ecalendar.domain.entities.UserEntity;

public class UserRepository extends AbstractRepository {

	private static final Class<UserEntity> entityClass = UserEntity.class; 
	
	public UserEntity getUserWithCalendarsById(int id) {
		return this.getDatabase().startSession((session)->{
			UserEntity user = session.byId(entityClass).load(id);
			if(user == null) return null;
			user.getOwnedCalendars().forEach((lazyload)->{
				lazyload.getSharedCalendars().forEach((lazyload1)->{});
			});
			user.getSharedCalendars().forEach((lazyload)->{});
			return user;
		});
	}
	
	public UserEntity getUserByEmail(String email) {
		return super.getEntityByNaturalId(entityClass, "email", email);
	}
	
	public UserEntity registerUser(UserEntity userEntity) {
		return super.insertEntity(userEntity);
	}

	public UserEntity getUserById(int userID) {
		return this.getEntityById(entityClass, userID);
	}

	public UserEntity getUserByUsername(String username) {
		return super.getDatabase().startSession((session)->{
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<UserEntity> query = cb.createQuery(entityClass);
			Root<UserEntity> from = query.from(entityClass);
			query.select(from);
			query.where(cb.equal(from.get("username"), username));
			TypedQuery<UserEntity> typed = session.createQuery(query);
		    try {
		        return typed.getSingleResult();
		    } catch (final NoResultException nre) {
		        return null;
		    }
		});
	}
}
