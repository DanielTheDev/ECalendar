const isMobile = window.matchMedia("only screen and (max-width: 600px)").matches;
const api = APIClient.getInstance();
const board = new CalendarBoard(api);

if(!api.isLoggedIn()) {
	window.location.href = "/login";
}

$(document).ready(function () {

	board.on('onDateChange', function(event) {
		$("#year-display").text(board.getYear());
		$("#month-display").text(board.getMonthName())
		$("#date-title").text(`${board.getFullMonthName()} ${board.getYear()}`);
	});

	board.on('onError', errorAlert);

	board.on('onCalendarChange', function(calendar) {
		loadCalendars();
	});
	board.on('onItemRemoved', removeCalendarItem);

	board.on('onItemAdded', function(item) {
		addCalendarItem(item);
		closeCalendarItem();
	});
	board.on('onItemEdited', function(item) {
		removeCalendarItem(item.getID());
		addCalendarItem(item);
		closeCalendarItem();
	});

	board.on('onCalendarAdded', function(calendar) {
		loadCalendars();
		closeCalendar();
	});
	board.on('onCalendarEdited', function(calendar) {
		loadCalendars();
		closeCalendar();
	});
	board.on('onCalendarRemoved', function(calendarID) {
		$(`#${calendarID}.calendarname`).remove();
	});
	board.on('onCalendarsLoad', function(calendars) {
		loadCalendars();
	});

	
	board.on('onCalendarItemsLoad', function(calendarItems) {

		let content = "";
		for(let day = 1; day <= board.getDays(); day++) {
			content += `<div id="day-${day}" class="calendar-day"><hr><a>${day}</a><div id="calender-day-content"></div><div class="clr"></div></div>`;
		}
		$("#board").html(content);

		for(let t = 0; t < calendarItems.length; t++) {
			addCalendarItem(calendarItems[t]);
		}
	});

	board.on('onSharedCalendarAdded', function(calendar) {
		closeCalendar();
		openCalendar(calendar);
	});

	board.on('onSharedCalendarDeleted', function(calendar) {
		closeCalendar();
		openCalendar(calendar);
	});

	board.load();

	$("#add-permission").click(function() {
		board.getUserByName($("#add-permission-user").val(), function(user) {

			const calendarID = $("#popup-calendar-save").attr("calendar-id");
			const permissions =
			($("#add-permission-create").is(":checked") ? Permission.ITEM_CREATE.mask : 0) | 
			($("#add-permission-edit").is(":checked") ? Permission.ITEM_EDIT.mask : 0) | 
			($("#add-permission-delete").is(":checked") ? Permission.ITEM_DELETE.mask : 0);

			board.shareCalendar(calendarID, user.getID(), permissions);
		});
	});

	$("#showcalendars").click(function() {
		$("#calendarlist").toggleClass("hide");
	});

	$("#popup-delete").click(function(e) {
		let itemID = e.target.attributes['item-id'].value;
		board.deleteItem(itemID);
	});

	$("#popup-calendar-save").click(function() {
		const result = getCalendarFromPopup();
		if('error' in result) {
			errorAlert(result.error);
		} else {
			if($(this).attr('calendar-id') == null) {
				board.createCalendar(result);
			} else {
				const calendarItemID = $(this).attr('calendar-id');
				result.ID = calendarItemID;
				board.editCalendar(result);
			}
		}
	});

	$("#popup-save").click(function() {
		const result = getCalendarItemFromPopup();
		if('error' in result) {
			errorAlert(result.error);
		} else {
			if($("#create-edit-screen").attr('type') == 'create') {
				board.createItem(result);
			} else if($("#create-edit-screen").attr('type') == 'edit') {
				const calendarItemID = $("#create-edit-screen").attr('edit-id');
				result.ID = calendarItemID;
				board.editItem(result);
			}
		}
	});
	$("#logout").click(function() {
		api.logout();
	});
	$("#create-button").click(openCalendarItem);
	$("#create-edit-screen .cancel-btn").click(closeCalendarItem);
	$("#create-edit-calendar-screen .cancel-btn").click(closeCalendar);

    if (isMobile) {
        $("#box-title").focusin(function() {
            $("#create-edit-screen").css("top", "60%");
        });
        $("#box-title").focusout(function() {
            $("#create-edit-screen").css("top", "50%");
        });
    }
	$("#box-title").click(function (e) {
		if ($(this).val() == "New Item") {
			$(this).val("");
		}
	});
	$("#box-calendar-title").click(function (e) {
		if ($(this).val() == "New Calendar") {
			$(this).val("");
		}
	});
	$("#box-title").keydown(function (e) {
		if (e.keyCode == 13) {
			e.target.blur();
		}
	});
	$(".popup-form").submit(function(e) {
		e.preventDefault();
	});
	$("#box-repeattype").change(function (e) {
		let val = $(this).val();
		if (val == 0) {
			$("#repeat-label, #box-repeatamount, #box-repeatstopdate").addClass("hide");
		} else {
			$("#repeat-label, #box-repeatamount, #box-repeatstopdate").removeClass("hide");
		}
	});
	$("#selected-color").click(function () {
		$("#color-pick").toggleClass("hide");
	});
	$("#color-box .dot").click(function (e) {
		var classes = e.target.classList;
		for (let i = 0; i < classes.length; i++) {
			if (classes[i].startsWith("clr")) {
				var selected = getSelectedColor();
				if (selected != null) {
					$("#selected-color").removeClass(selected).addClass(classes[i]);
				}
				break;
			}
		}
		$("#color-pick").toggleClass("hide");
	});
});

function getSelectedColor() {
	var classes = $("#selected-color").get(0).classList;
	for (let i = 0; i < classes.length; i++) {
		if (classes[i].startsWith("clr")) {
			return classes[i];
		}
	}
	return null;
}

function calendarItemToHTML(calendarItem) {
	return `<div id="${calendarItem.getID()}" class="item-list clr-${calendarItem.getItemColor().ID}"><span class="item-list-name">${calendarItem.getTitle()}</span></div>`;
}

function loadCalendars() {
	let content = "";
	for(let t = 0; t < board.calendars.length; t++) {
		if(board.calendars[t].getID() != board.getCurrentCalendar().getID()) {
			content += `<div id="${board.calendars[t].getID()}" class="calendarname"><a id="select">${board.calendars[t].getName()}</a><a id="edit">&#9998;</a><a id="delete">&#10005;</a></div>`;
		} else {
			let text = board.calendars[t].getName();
			$("#calendar-title").text(text);
		}
	}
	$("#calendarlist").html(content + `<div class="calendarname"><a id="create">&#9547;</a></div>`);
	$(".calendarname").click(function(e) {

		$("#calendarlist").toggleClass("hide");
		switch(e.target.id) {
			case "select" : {
				board.setCalendar($(this).get(0).id);
				break;
			}
			case "edit" : {
				openCalendar(board.getCalendarByID($(this).get(0).id));
				break;
			}
			case "delete" : {
				board.deleteCalendar($(this).get(0).id);
				break;
			}
			case "create" : {
				openCalendar();
				break;
			}
		}
		
	});
}

function addCalendarItem(calendarItem) {

	function isInRange(itemStartDate, itemEndDate, startDate, endDate) {
		return itemStartDate <= endDate && itemEndDate >= startDate;
	}

	function add(calendarItem, startDate, endDate) {
		if(startDate.getDate() == endDate.getDate() && startDate.getMonth() == endDate.getMonth() && startDate.getFullYear() == endDate.getFullYear()) {
			let item = $(`#day-${startDate.getDate()} #calender-day-content`);
			item.html(item.html() + calendarItemToHTML(calendarItem));
		} else {
			let startDay = startDate.getDate();
			let endDay = endDate.getDate();

			if(startDate.getMonth()+1 < board.getMonth() || startDate.getFullYear() < board.getYear()) {
				startDay = 1;
			}

			if(endDate.getMonth()+1 > board.getMonth() || endDate.getFullYear() > board.getYear()) {
				endDay = board.getDays();	
			}
			
			for(startDay; startDay <= endDay; startDay++) {
				let item = $(`#day-${startDay} #calender-day-content`);
				item.html(item.html() + calendarItemToHTML(calendarItem));
			}
		}
	}

	if(calendarItem.getRepeatingAttribute() == null) {
		add(calendarItem, calendarItem.getStartDate(), calendarItem.getEndDate());
	} else {
		const amount = calendarItem.getRepeatingAttribute().getAmount();
		const start = new Date(`${board.getYear()}-${board.getMonth()}-1`);
		const end = new Date(`${board.getYear()}-${board.getMonth()}-${board.getDays()}`);

		const itemStart = new Date(calendarItem.getStartDate().valueOf());
		const itemEnd  = new Date(calendarItem.getEndDate().valueOf());
		const stopDate = calendarItem.getRepeatingAttribute().getStopDate();

		while(itemStart < stopDate && start < stopDate) {
			if(isInRange(itemStart, itemEnd, start, end)) {
				add(calendarItem, itemStart, itemEnd);
			}

			switch(calendarItem.getRepeatingAttribute().getRepeatingType()) {
				case RepeatingType.DAILY: {
					itemStart.setDate(itemStart.getDate() + amount);
					itemEnd.setDate(itemEnd.getDate() + amount);
					break;
				}
				case RepeatingType.WEEKLY: {
					itemStart.setDate(itemStart.getDate() + amount*7);
					itemEnd.setDate(itemEnd.getDate() + amount*7);
					break;
				}
				case RepeatingType.MONTHLY: {
					itemStart.setMonth(itemStart.getMonth() + amount);
					itemEnd.setMonth(itemEnd.getMonth() + amount);
					break;
				}
				case RepeatingType.YEARLY: {
					itemStart.setFullYear(itemStart.getFullYear() + amount);
					itemEnd.setFullYear(itemEnd.getFullYear() + amount);
					break;
				}
			}
		}

		if(isInRange(itemStart, itemEnd, start, end)) {
			add(calendarItem, itemStart, itemEnd);
		}
	}

	$(".item-list").click(function(e) {
		openCalendarItem(board.getCalendarItemByID($(this).get(0).id));	
	});
}

function removeCalendarItem(itemID) {
	$(`#${itemID}.item-list`).remove();
	closeCalendarItem();
}

function closeCalendarItem() {
	$('#create-edit-screen #popup-delete').removeAttr('item-id');
	closePopup();
}

function closeCalendar() {
	$('#create-edit-calendar-screen #popup-calendar-save').removeAttr('calendar-id');
	closePopup();
}

function closePopup() {
	$(".popup-screen").addClass("hide");
	$("#popup-overlay").addClass("hide");
}

function openCalendar(calendar) {
	$("#add-permission-user").val("");
	$("#add-permission-create, #add-permission-edit, #add-permission-delete").prop("checked", false);
	$("#sharedlist").html("");
	$("#popup-calendar-save").removeClass("hide");
	$("#row-owner, #row-permissions").addClass("hide");
	if(!(calendar instanceof CalendarEntity)) {
		$("#box-calendar-title").val("New Calendar");
	} else {
		if(calendar instanceof SharedCalendarEntity) {
			$("#popup-calendar-save").addClass("hide");
			$("#row-owner, #row-permissions").removeClass("hide");
			const permissionslist = calendar.getAccessPermissions();

			let permissions = "";

			for(let t = 0; t < permissionslist.length; t++) {
				permissions+=permissionslist[t].display;
				if(t + 1 != permissionslist.length) {
					permissions += ", ";
				}
			}
			
			$("#box-calendar-permissions").text(permissions);
			$("#box-calendar-owner").text(calendar.getOwner().getUsername());
		} else {
			let list = "";
			let user = null;
			for(let t = 0; t < calendar.getSharedUsers().length; t++) {
				user = calendar.getSharedUsers()[t];
				list+=
				`<div class="row" user-id="${user.getID()}">
                <div class="col-33">${user.getUsername()}</div>
                <div class="col-15 center"><input class="change-share-create" type="checkbox" ${Permission.hasPermission(user.accessPermissions, Permission.ITEM_CREATE) ? "checked" : ""} disabled></div>
                <div class="col-15 center"><input class="change-share-edit" type="checkbox" ${Permission.hasPermission(user.accessPermissions, Permission.ITEM_EDIT) ? "checked" : ""} disabled></div>
                <div class="col-15 center"><input class="change-share-delete" type="checkbox" ${Permission.hasPermission(user.accessPermissions, Permission.ITEM_DELETE) ? "checked" : ""} disabled></div>
                <div class="col-15 center"><input class="remove-share" type="submit" value="remove"></div>
            	</div>`;
			}

			$("#sharedlist").html(list);
			$("#sharedlist .remove-share").click(function(e) {
				const calendarID = $('#create-edit-calendar-screen #popup-calendar-save').attr('calendar-id');
				const userID = $(e.target.parentNode.parentNode).attr("user-id");
				board.deleteSharedCalendar(calendarID, userID);
			});
		}

		$("#box-calendar-title").val(calendar.getName());
		$('#create-edit-calendar-screen #popup-calendar-save').attr('calendar-id', calendar.getID());

	}
	$("#create-edit-calendar-screen").removeClass("hide");
	$("#popup-overlay").removeClass("hide");
}

function openCalendarItem(calendarItem) {
	$("#popup-notification-popup, #popup-notification-email").prop( "checked", false);

	if(!(calendarItem instanceof CalendarItemEntity)) {
		$('#create-edit-screen').attr('type', 'create');
		const now = new Date();
		const startDate = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().substring(0, 16);
		now.setHours(now.getHours() + 1);
		const endDate = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().substring(0, 16);
		$("#box-startdate").val(startDate);
		$("#box-enddate").val(endDate);
		$("#popup-delete").addClass("hide");

		$("#box-repeattype").val(0);
		$("#box-repeattype").change();
		$("#box-repeatamount").val(1);
		$("#box-repeatstopdate").val(endDate.substring(0, 10));
	} else {
		$('#create-edit-screen').attr('type', 'edit');
		$('#create-edit-screen').attr('edit-id', calendarItem.getID());	
		$("#popup-delete").removeClass("hide");
		$('#popup-delete').attr('item-id', calendarItem.getID());
		$("#box-title").val(calendarItem.getTitle());
		$("#box-description").val(calendarItem.getdescription);
		$("#box-startdate").val(calendarItem.getStartDate().toISOString().substring(0, 16));
		$("#box-enddate").val(calendarItem.getEndDate().toISOString().substring(0, 16));

		calendarItem.getNotifications().forEach(notification=>{
			switch(notification) {
				case Notification.EMAIL : {
					$("#popup-notification-email").prop( "checked", true);
					break;
				}
				case Notification.POPUP : {
					$("#popup-notification-popup").prop( "checked", true);
					break;
				}
			}
		});

		if(calendarItem.getRepeatingAttribute() != null) {
			$("#box-repeattype").val(calendarItem.getRepeatingAttribute().getRepeatingType().ID);
			$("#box-repeatamount").val(calendarItem.getRepeatingAttribute().getAmount());
			$("#box-repeatstopdate").val(calendarItem.getRepeatingAttribute().getStopDate().toISOString().substring(0, 10));
		} else {
			$("#box-repeattype").val(0);
		}
		$("#box-repeattype").change();
	}
	$("#create-edit-screen").removeClass("hide");
	$("#popup-overlay").removeClass("hide");
}


function errorAlert(msg) {
	$("#alert-overlay").removeClass("hide");
	$("#alertbox").html(`<div class="alert"><span><strong>Alert! </strong>${msg}<a id="error-close">&#10006;</a></span></div>`);

	$(".alert").addClass("fadeInDown");
	$(".alert").on("animationend", function () {
		$(this).removeClass('fadeInDown');
	});

	$("#error-close").click(function() {
		$("#alert-overlay").addClass("hide");
		$(".alert").remove();
	});
}

function getCalendarFromPopup() {
	const name = $("#box-calendar-title").val();

	if(name == null || !(name.length >= 3 && name.length <= 32 && name.match(/^[a-zA-Z0-9\s]+$/))) {
		return {error: "name is invalid"};
	}

	const calendar = new CalendarEntity(0, name, null);
	return calendar;

}


function getCalendarItemFromPopup() {
	const title = $("#box-title").val();

	if(title == null || !(title.length >= 3 && title.length <= 32 && title.match(/^[a-zA-Z0-9\s]+$/))) {
		return {error: "title is invalid"};
	}

	const description = $("#box-description").text();

	if(description == null || !(description.length <= 255)) {
		return {error: "description is invalid"};
	}

	const startDate = new Date($("#box-startdate").val());

	if(startDate == null) {
		return {error: "startDate is invalid"};
	}

	const endDate = new Date($("#box-enddate").val());

	if(endDate == null) {
		return {error: "endDate is invalid"};
	}

	if(endDate < startDate) {
		return {error: "startDate cannot be after endDate"};
	}

	const itemColor = ItemColor.getColorByID(getSelectedColor().substring(4));

	if(itemColor == null) {
		return {error: "itemColor is invalid"};
	}

	let notificationMask = ($("#popup-notification-email").is(":checked") ? 2 : 0) | ($("#popup-notification-popup").is(":checked") ? 1 : 0)
	const notifications = Notification.getNotifications(notificationMask);

	if(notifications == null) {
		return {error: "notifications are invalid"};
	}
	
	const repeatingTypeID = $("#box-repeattype").children("option:selected").val();
	const repeatingType = RepeatingType.getRepeatingTypeByID(repeatingTypeID);

	if(repeatingTypeID != 0 && repeatingType == null) {
		return {error: "notifications are invalid"};
	}

	const repeatingAmount = $("#box-repeatamount").val();
	
	if(repeatingTypeID != 0 && repeatingAmount == null || repeatingAmount < 1 || repeatingAmount > 1000) {
		return {error: "repeatingAmount is invalid"};
	}


	const repeatingStopDate = new Date($("#box-repeatstopdate").val());



	if(repeatingTypeID != 0 && repeatingStopDate == null) {
		return {error: "repeatingStopDate is invalid"};
	}

	if(repeatingTypeID != 0 && repeatingStopDate < endDate) {
		return {error: "repeatingStopDate cannot be before endDate"};
	}

	const itemID = $('#popup-delete').attr('item-id');
	const calendarID = board.getCurrentCalendar().getID();
	
	const calendarItem = new CalendarItemEntity(itemID, calendarID, title, description, startDate, endDate, itemColor, notifications, null);
	
	if(repeatingType != null) {
		calendarItem.repeatingAttribute = new RepeatingAttributeEntity(repeatingType, repeatingAmount, repeatingStopDate);
	}
	return calendarItem;

}