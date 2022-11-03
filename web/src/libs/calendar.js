class CalendarBoard {

	static MONTHS = ["Januari", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

	constructor(api) {
		this.date = new Date();
		this.api = api;
        this.eventManager = [];
		this.calendars = [];
	}

    load(callback) {
        const self = this;
		this.loadCalendars(function(calendars, success, error) {
			if(success) {
				self.calendars = calendars;
				self.setCalendar(calendars[0].getID());
				self.dispatchEvent('onCalendarsLoad', calendars);
			} else {
				self.dispatchEvent('onError', error);
			}
			if(callback != null) callback();
		});
    }

	loadCalendars(callback) {
		api.call(APIClient.ENDPOINT_CALENDAR_LIST, function(json, success, error) {
			if(success) {
				const calendars = [];
                json.calendars.forEach(calendar=>{
                    calendars.push(CalendarEntity.parse(calendar));
                });
                json.sharedcalendars.forEach(sharedcalendar=>{
                    calendars.push(SharedCalendarEntity.parse(sharedcalendar));
                });
				callback(calendars, true, null);
			} else {
				callback(null, false, error);
			}
		});
	}

	loadCalendarItems(callback) {
		api.call(APIClient.ENDPOINT_CALENDAR_ITEM_GET, function(json, success, error) {
			if(success) {
				const calendarItems = [];
                json.list.forEach(calendarItem=>{
                    calendarItems.push(CalendarItemEntity.parse(calendarItem));
                });
				callback(calendarItems, true, null);
			} else {
				callback(null, false, error);
			}
		}, {year: this.getYear(), month: this.getMonth()}, {calendarID: this.getCurrentCalendar().getID()});
	}

    on(name, handler) {
        if(this.eventManager[name] == null) {
            this.eventManager[name] = [];
        }
        this.eventManager[name].push(handler);
    }

    dispatchEvent(name, event) {
        if(this.eventManager[name] != null) {
            this.eventManager[name].forEach(handler=>handler(event));
        }
    }

	createCalendar(calendar) {
		if(calendar == null) {
			this.dispatchEvent('onError', "calendar not found");
		} else {
			const self = this;
			api.call(APIClient.ENDPOINT_CALENDAR_CREATE, function(json, success, error) {
				if(success) {
					const entity = CalendarEntity.parse(json);
					self.calendars.push(entity);
					self.dispatchEvent('onCalendarAdded', entity);
				} else {
					self.dispatchEvent('onError', error);
				}
			}, calendar.toJSON(), {});
			
		}
	}

	createItem(calendarItem) {
		if(calendarItem == null) {
			this.dispatchEvent('onError', "calendarItem is null");
		} else {
			const self = this;
			api.call(APIClient.ENDPOINT_CALENDAR_ITEM_CREATE, function(json, success, error) {
				if(success) {
					const item = CalendarItemEntity.parse(json);
					const index = self.calendarItems.indexOf(item);

					self.calendarItems.push(item);
					self.dispatchEvent('onItemAdded', item);
				} else {
					self.dispatchEvent('onError', error);
				}
			}, calendarItem.toJSON(), {calendarID: this.getCurrentCalendar().getID()});
		}
	}

	deleteSharedCalendar(calendarID, userID) {
		const self = this;
		api.call(APIClient.ENDPOINT_SHARED_CALENDAR_REMOVE, function(json, success, error) {
			if(success) {
				self.getCalendarByID(calendarID).removeSharedUser(userID);
				self.dispatchEvent('onSharedCalendarDeleted', self.getCalendarByID(calendarID));
			} else {
				self.dispatchEvent('onError', error);
			}
		}, {}, {calendarID: calendarID, userID: userID});
	}

	shareCalendar(calendarID, userID, permissions) {
		const self = this;
		api.call(APIClient.ENDPOINT_SHARED_CALENDAR_ADD, function(json, success, error) {
			if(success) {
				const calendar = CalendarEntity.parse(json);
				const index = self.calendars.indexOf(self.getCalendarByID(calendar.getID()));
				self.calendars[index] = calendar;
				self.dispatchEvent('onSharedCalendarAdded', calendar);
			} else {
				self.dispatchEvent('onError', error);
			}
		}, {accessPermissions: permissions}, {calendarID: calendarID, userID: userID});
	}

	getUserByName(name, callback) {
		const self = this;
		api.call(APIClient.ENDPOINT_USER_GET, function(json, success, error) {
			if(success) {
				const user = UserEntity.parse(json);
				callback(user);
			} else {
				self.dispatchEvent('onError', error);
			}
		}, {username: name}, {});
	}

	deleteCalendar(calendarID) {
		let calendar = this.getCalendarByID(calendarID);
		if(calendar == null) {
			this.dispatchEvent('onError', "calendarID not found");
		} else {
			const index = this.calendars.indexOf(calendar);
			const self = this;

			let endpoint = null;
			let payload = null;

			if(calendar instanceof SharedCalendarEntity) {
				endpoint = APIClient.ENDPOINT_SHARED_CALENDAR_REMOVE;
				payload = {calendarID: calendar.getID(), userID: api.getUser().getID()};
			} else {
				endpoint = APIClient.ENDPOINT_CALENDAR_DELETE;
				payload = {calendarID: calendar.getID()};
			}
			api.call(endpoint, function(json, success, error) {
				if(success) {
					self.calendars.splice(index, 1);
					self.dispatchEvent('onCalendarRemoved', calendarID);
				} else {
					self.dispatchEvent('onError', error);
				}
			}, {}, payload);
			
		}
	}

	deleteItem(itemID) {
		let item = this.getCalendarItemByID(itemID);
		if(item == null) {
			this.dispatchEvent('onError', "calendarItemID not found");
		} else {
			const index = this.calendarItems.indexOf(item);
			const self = this;
			api.call(APIClient.ENDPOINT_CALENDAR_ITEM_DELETE, function(json, success, error) {
				if(success) {
					self.calendarItems.splice(index, 1);
					self.dispatchEvent('onItemRemoved', itemID);
				} else {
					self.dispatchEvent('onError', error);
				}
			}, {}, {calendarID: this.getCurrentCalendar().getID(), calendarItemID: itemID});
		}
	}

	editCalendar(calendarEntity) {
		let calendar = this.getCalendarByID(calendarEntity.getID());
		if(calendar == null) {
			this.dispatchEvent('onError', "calendarID not found");
		} else {
			const self = this;
			api.call(APIClient.ENDPOINT_CALENDAR_EDIT, function(json, success, error) {
				if(success) {
					const calendarEntry = CalendarEntity.parse(json);
					const index = self.calendars.indexOf(self.getCalendarByID(calendarEntry.getID()));
					self.calendars[index] = calendarEntry;
					self.dispatchEvent('onCalendarEdited', calendarEntry);
				} else {
					self.dispatchEvent('onError', error);
				}
			}, calendarEntity.toJSON(), {calendarID: calendarEntity.getID()});
		}
	}

	editItem(calendarItem) {
		let item = this.getCalendarItemByID(calendarItem.getID());
		if(item == null) {
			this.dispatchEvent('onError', "calendarItemID not found");
		} else {
			const self = this;
			api.call(APIClient.ENDPOINT_CALENDAR_ITEM_EDIT, function(json, success, error) {
				if(success) {
					const itemEntity = CalendarItemEntity.parse(json);
					const index = self.calendarItems.indexOf(self.getCalendarItemByID(itemEntity.getID()));
					self.calendarItems[index] = itemEntity;
					self.dispatchEvent('onItemEdited', itemEntity);
				} else {
					self.dispatchEvent('onError', error);
				}
			}, calendarItem.toJSON(), {calendarID: this.getCurrentCalendar().getID(), calendarItemID: item.getID()});
		}
	}

	

	nextMonth() {
		this.date.setMonth(this.date.getMonth() + 1);
		this.reloadCalendar();
	}

	previousMonth() {
		this.date.setMonth(this.date.getMonth() - 1);
		this.reloadCalendar();
	}

	nextYear() {
		this.date.setFullYear(this.date.getFullYear() + 1);
		this.reloadCalendar();
	}

	previousYear() {
		this.date.setFullYear(this.date.getFullYear() - 1);
		this.reloadCalendar();
	}

	reloadCalendar() {
		this.dispatchEvent('onDateChange', this.getDate());
		this.setCalendarItems();
	}

    getDate() {
        return {year: this.getYear(), month: this.getMonth()};
    }

	getMonth() {
		return this.date.getMonth() + 1;
	}

	getYear() {
		return this.date.getFullYear();
	}
	
	getMonthName() {
		return this.getFullMonthName().substring(0, 3);
	}	

	getFullMonthName() {
		return CalendarBoard.MONTHS[this.date.getMonth()];
	}

	getDays() {
		return new Date(this.getYear(), this.getMonth(), 0).getDate();
	}

	getCalendarItemByID(ID) {
		for(let i = 0; i < this.calendarItems.length; i++) {
			if(this.calendarItems[i].getID() == ID) {
				return this.calendarItems[i];
			}
		}
	}

	getCalendarByID(ID) {
		for(let i = 0; i < this.calendars.length; i++) {
			if(this.calendars[i].getID() == ID) {
				return this.calendars[i];
			}
		}
	}


	setCalendar(calendarID) {
		if(this.calendars.length > 0) {
			let calendar = null;
			
			for(let i = 0; i < this.calendars.length; i++) {
				if(this.calendars[i].getID() == calendarID) {
					calendar = this.calendars[i];
					break;
				}
			}
			
			if(calendar == null) {
				throw "calendar is null";
			}
			
			this.calendar = calendar;
			this.dispatchEvent('onCalendarChange', this.calendar);
			this.dispatchEvent('onCalendarsLoad', this.calendars);
			this.date = new Date();
			this.dispatchEvent('onDateChange', this.getDate());

			this.setCalendarItems();
		} else {
			throw "cannot have zero calendars";
		}
	}

	setCalendarItems() {
		const self = this;
		this.loadCalendarItems(function(calendarItems, success, error) {
			if(success) {
				self.calendarItems = calendarItems;
				self.dispatchEvent('onCalendarItemsLoad', calendarItems);
			} else {
				self.dispatchEvent('onError', error);
			}
		});
	}

	getCalendarItems() {
		return this.calendarItems;		
	}

	getCurrentCalendar() {
		return this.calendar;
	}
}