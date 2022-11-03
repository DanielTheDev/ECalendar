package com.danielthedev.ecalendar.persistence.repositories;

import java.time.YearMonth;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import com.danielthedev.ecalendar.domain.entities.CalendarEntity;
import com.danielthedev.ecalendar.domain.entities.CalendarItemEntity;

public class CalendarItemRepository extends AbstractRepository {

	public CalendarItemEntity createCalendarItem(CalendarItemEntity calendarItemEntity) {
		if(calendarItemEntity.getRepeatingAttribute() != null) {
			super.insertEntity(calendarItemEntity.getRepeatingAttribute());
		}
		return super.insertEntity(calendarItemEntity);
	}
	
	public void deleteCalendarItem(CalendarItemEntity calendarItemEntity) {
		super.deleteEntity(calendarItemEntity);
	}
	
	public List<CalendarItemEntity> getRepeatingCalendarItems(CalendarEntity calendarEntity) {
		return super.getDatabase().startSession(session->{
			NativeQuery<CalendarItemEntity> query = session.createSQLQuery("SELECT * FROM CalendarItem WHERE CalendarItem.repeatingAttributeID IS NOT NULL AND CalendarItem.calendarID = :calendarID");
			query.addEntity(CalendarItemEntity.class);
			query.setParameter("calendarID", calendarEntity.getID());
			return query.list();
		});
	}
	
	public List<CalendarItemEntity> getCalendarItems(CalendarEntity calendarEntity, int year, int month) {
		return super.getDatabase().startSession(session->{
			int endDay = YearMonth.of(year, month).lengthOfMonth();
			String startDate = String.format("%s-%s-%s", year, month, 1);
			String endDate = String.format("%s-%s-%s", year, month, endDay);
			NativeQuery<CalendarItemEntity> query = session.createSQLQuery("SELECT * FROM CalendarItem WHERE startDate <= :end AND endDate >= :start AND CalendarItem.calendarID = :calendarID AND CalendarItem.repeatingAttributeID IS NULL");
			query.addEntity(CalendarItemEntity.class);
			query.setParameter("start", startDate);
			query.setParameter("end", endDate);
			query.setParameter("calendarID", calendarEntity.getID());
			return query.list();

		});
	}

	public CalendarItemEntity getCalendarItem(CalendarEntity calendarEntity, int calendarItemID) {
		CalendarItemEntity calendarItem =  super.getDatabase().startSession(session->{
			
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<CalendarItemEntity> criteria = builder.createQuery(CalendarItemEntity.class);
			Root<CalendarItemEntity> root = criteria.from(CalendarItemEntity.class);
			
			criteria.select(root).where(builder.equal(root.get("ID"), calendarItemID), builder.equal(root.get("calendar"), calendarEntity.getID()));
			
			Query<CalendarItemEntity> query = session.createQuery(criteria);
			
			try {
				return query.uniqueResult();
			} catch (Exception e) {
				return null;
			}
		});
		return calendarItem;
	}

	public CalendarItemEntity editCalendarItem(CalendarItemEntity calendarItemEntity) {
		
		if(calendarItemEntity.getRepeatingAttribute() == null) {
			super.getDatabase().startTransaction((session)->{
				NativeQuery query = session.createSQLQuery("DELETE FROM `RepeatingAttribute` WHERE RepeatingAttribute.ID = (SELECT CalendarItem.ID FROM CalendarItem WHERE CalendarItem.ID = :calendarItem)");
				query.setParameter("calendarItem", calendarItemEntity.getID());
				query.executeUpdate();
				return null;
			});		
		} else {
			super.insertUpdateEntity(calendarItemEntity.getRepeatingAttribute());
		}
		
		return super.updateEntity(calendarItemEntity);
	}
}
