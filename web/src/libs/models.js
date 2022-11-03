class JWTToken {

    constructor(token) {
        const parts = token.split(".");
        this.header = JSON.parse(decodeURIComponent(escape(window.atob(parts[0]))));
        this.payload = JSON.parse(decodeURIComponent(escape(window.atob(parts[1]))));
        this.signature = parts[2];
        this.token = token;
    }

    getUserID() {
        return this.payload.userId;
    }

    getRemainingDays() {
        const diffTime = this.getExpireDate()-new Date();
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        return diffDays;
    }

    isExpired() {
        return 0 >= this.getExpireDate()-new Date();
    }

    getExpireDate() {
        return new Date(this.payload.expireDate);
    }

    getUsername() {
        return this.payload.username;
    }

    getType() {
        return this.header.typ;
    }

    getAlgorithm() {
        return this.header.alg;
    }

    getToken() {
        return this.token;
    }
}

class APIEndpoint {

    constructor(endpoint, method, requiretoken) {
        this.endpoint = endpoint;
        this.method = method;
        this.requiretoken = requiretoken;
    }

    getEndpoint(placeholders) {
        let result = this.endpoint;
        if(placeholders != null) {
            for (var placeholder in placeholders) {
                result = result.replace(`{${placeholder}}`, placeholders[placeholder]);
            };
        }
        return result;
    }

    getMethod() {
        return this.method;
    }

    requireToken() {
        return this.requiretoken;
    }
}

class UserEntity {

    constructor(ID, username) {
        this.ID = ID;
        this.username = username;
    }

    getID() {
        return this.ID;
    }

    getUsername() {
        return this.username;
    }

    static parse(json) {
        const ID = json.ID;
        const username = json.username;
        return new UserEntity(ID, username);
    }
}

class CalendarEntity {

    constructor(ID, name, owner, sharedusers) {
        this.ID = ID;
        this.name = name;
        this.owner = owner;
        this.sharedusers = sharedusers;
    }

    static parse(json) {
        const ID = json.ID;
        const name = json.name;
        let owner = null;
        if('owner' in json) owner = UserEntity.parse(json.owner);
        const sharedusers = [];
        for(let t = 0; t < json.sharedUsers.length; t++) {
            const user = UserEntity.parse(json.sharedUsers[t].user);
            user.accessPermissions = Permission.getPermissions(json.sharedUsers[t].accessPermissions);
            sharedusers.push(user);
        }
        return new CalendarEntity(ID, name, owner, sharedusers);
    }

    getID() {
        return this.ID;
    }

    getName() {
        return this.name;
    }

    getOwner() {
        return this.owner;
    }

    getSharedUsers() {
        return this.sharedusers;
    }

    removeSharedUser(userID) {
        for(let t = 0; t < this.sharedusers.length; t++) {
            if(this.sharedusers[t].getID() == userID) {
                const index = this.sharedusers.indexOf(this.sharedusers[t]);
                this.sharedusers.splice(index, 1);
                break;
            }
        }
    }

    toJSON() {
        const json = {
            name: this.name
        };
        return json;
    }
}

class SharedCalendarEntity extends CalendarEntity {

    constructor(accessPermissions, calendarID, calendarName, calendarOwner) {
        super(calendarID, calendarName, calendarOwner);
        this.accessPermissions = accessPermissions
    }

    static parse(json) {
        const accessPermissions = Permission.getPermissions(json.accessPermissions);
        const calendarID = json.calendar.ID;
        const calendarName = json.calendar.name;
        const calendarOwner = UserEntity.parse(json.calendar.owner);
        return new SharedCalendarEntity(accessPermissions, calendarID, calendarName, calendarOwner);
    }

    getAccessPermissions() {
        return this.accessPermissions;
    }
}

class CalendarItemEntity { 

    constructor(ID, calendarID, title, description, startDate, endDate, color, notifications, repeatingAttribute) {
        this.ID = ID;
        this.calendarID = calendarID;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.color = color;
        this.notifications = notifications;
        this.repeatingAttribute = repeatingAttribute;
    }

    static parse(json) {
        const ID = json.ID;
        const calendarID = json.calendarID;
        const title = json.title;
        const description = json.description;
        const startDate = new Date(json.startDate);
        const endDate = new Date(json.endDate);
        const color = ItemColor.getColorByID(json.color);
        const notifications = Notification.getNotifications(json.notifications);
        let repeatingAttribute = null;
        if('repeat' in json) repeatingAttribute = RepeatingAttributeEntity.parse(json.repeat);
        return new CalendarItemEntity(ID, calendarID, title, description, startDate, endDate, color, notifications, repeatingAttribute);
    }

    getID() {
        return this.ID;
    }

    getCalendarID() {
        return this.calendarID;
    } 

    getTitle() {
        return this.title;
    }

    getDescription() {
        return this.description;
    }

    getStartDate() {
        return this.startDate;
    }

    getEndDate() {
        return this.endDate;
    }

    getItemColor() {
        return this.color;
    }

    getNotifications() {
        return this.notifications;
    }

    getRepeatingAttribute() {
        return this.repeatingAttribute;
    }

    toJSON() {
        const json = {
            title: this.title, 
            description: this.description, 
            startDate:  this.toDateString(this.startDate), 
            endDate:  this.toDateString(this.endDate), 
            color: this.color.ID, 
            notifications: Notification.createMask(this.notifications)
        };
        if(this.repeatingAttribute != null) {
            json.repeat = {
                intervalType: this.repeatingAttribute.repeatingType.ID,
                amount: this.repeatingAttribute.amount,
                stopDate: this.toDateString(this.repeatingAttribute.stopDate)
            };
        }
        return json;
    }

    toDateString(date) {
        return `${date.getFullYear()}-${date.getMonth()+1}-${date.getDate()} ${date.getHours()+1}:${date.getMinutes()+1}`;
    }
}

class RepeatingAttributeEntity {

    constructor(repeatingType, amount, stopDate) {
        this.repeatingType = repeatingType;
        this.amount = amount;
        this.stopDate = stopDate;
    }
    
    static parse(json) {
        const repeatingType = RepeatingType.getRepeatingTypeByID(json.intervalType);
        const amount = json.amount;
        const stopDate = new Date(json.stopDate);
        return new RepeatingAttributeEntity(repeatingType, amount, stopDate);
    }

    getRepeatingType() {
        return this.repeatingType;
    }

    getAmount() {
        return this.amount;
    }

    getStopDate() {
        return this.stopDate;
    }
}


const RepeatingType = {
    DAILY: {ID: 1, name: "DAILY"},
    WEEKLY: {ID: 2, name: "WEEKLY"},
    MONTHLY: {ID: 3, name: "MONTHLY"},
    YEARLY: {ID: 4, name: "YEARLY"}
};

RepeatingType.getRepeatingTypeByID = function(id) {
    if(id == null) return null;
    for(repeatingType in RepeatingType) {
        if(RepeatingType[repeatingType].ID == id) {
            return RepeatingType[repeatingType];
        }
    }
    return null;
}

const ItemColor = {
    RED: {ID: 1, name: "RED"},
    PINK: {ID: 2, name: "PINK"},
    LIGHT_BLUE: {ID: 3, name: "LIGHT_BLUE"},
    ORANGE: {ID: 4, name: "ORANGE"},
    GREY: {ID: 5, name: "GREY"},
    AQUA: {ID: 6, name: "AQUA"},
    GOLD: {ID: 7, name: "GOLD"},
    PURPLE: {ID: 8, name: "PURPLE"}
};

ItemColor.getColorByID = function(id) {
    if(id == null) return null;
    for(color in ItemColor) {
        if(ItemColor[color].ID == id) {
            return ItemColor[color];
        }
    }
    return null;
}

const Notification = {
    POPUP: {mask: 1, name: "POPUP"},
    EMAIL: {mask: 2, name: "EMAIL"}
};

Notification.getNotifications = function(mask) {
    let list = [];
    let notification = null;
    for(notificationPointer in Notification) {
        notification = Notification[notificationPointer];
        if((notification.mask & mask) == notification.mask) {
            list.push(notification);
        }
    }
    return list;
}

Notification.createMask = function(notifications) {
    let mask = 0;
    notifications.forEach(notification=>{
        mask |= notification.mask;
    });
    return mask;
}

const Permission = {
    ITEM_CREATE: {mask: 1, name: "ITEM_CREATE", display: "Create Items"},
    ITEM_DELETE: {mask: 2, name: "ITEM_DELETE", display: "Delete Items"},
    ITEM_EDIT: {mask: 4, name: "ITEM_EDIT", display: "Edit Items"}
};

Permission.hasPermission = function(list, permission) {
    for(let t = 0; t < list.length; t++) {
        if(list[t] == permission) return true;
    }
    return false;
}

Permission.getPermissions = function(mask) {
    let list = [];
    let permission = null;
    for(permissionPointer in Permission) {
        permission = Permission[permissionPointer];
        if((permission.mask & mask) == permission.mask) {
            list.push(permission);
        }
    }
    return list;
}

Permission.createMask = function(permissions) {
    let mask = 0;
    permissions.forEach((permission)=>{
        mask |= permission.mask;
    });
    return mask;
}