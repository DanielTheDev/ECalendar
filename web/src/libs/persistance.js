class APIClient {

    static ENDPOINT_USER_LOGIN = new APIEndpoint("/user/login/", "POST", false);
    static ENDPOINT_USER_REGISTER = new APIEndpoint("/user/register", "POST", false);
    static ENDPOINT_USER_GET = new APIEndpoint("/user/get", "POST", false);

    static ENDPOINT_CALENDAR_CREATE = new APIEndpoint("/calendar/create", "POST", true);
    static ENDPOINT_CALENDAR_LIST = new APIEndpoint("/calendar/list", "POST", true);
    static ENDPOINT_CALENDAR_DELETE = new APIEndpoint("/calendar/delete/{calendarID}", "POST", true);
    static ENDPOINT_CALENDAR_EDIT = new APIEndpoint("/calendar/edit/{calendarID}", "POST", true);

    static ENDPOINT_SHARED_CALENDAR_ADD = new APIEndpoint("/calendar/{calendarID}/share/add/{userID}", "POST", true);
    static ENDPOINT_SHARED_CALENDAR_EDIT = new APIEndpoint("/calendar/{calendarID}/share/edit/{userID}", "POST", true);
    static ENDPOINT_SHARED_CALENDAR_REMOVE = new APIEndpoint("/calendar/{calendarID}/share/remove/{userID}", "POST", true);

    static ENDPOINT_CALENDAR_ITEM_CREATE = new APIEndpoint("/calendar/{calendarID}/create", "POST", true);
    static ENDPOINT_CALENDAR_ITEM_GET = new APIEndpoint("/calendar/{calendarID}/get", "POST", true);
    static ENDPOINT_CALENDAR_ITEM_DELETE = new APIEndpoint("/calendar/{calendarID}/delete/{calendarItemID}", "POST", true);
    static ENDPOINT_CALENDAR_ITEM_EDIT = new APIEndpoint("/calendar/{calendarID}/edit/{calendarItemID}", "POST", true);

    static HOST = "localhost";
    static REQUEST = "http";
    static VERSION = "v1";
    static PORT = 4403;

    constructor(host, request, version, port) {
        this.host = host;
        this.request = request;
        this.version = version;
        this.port = port;
        this.baseurl = `${request}://${host}:${port}/api/${version}`;
        if(Cookies.get("token") != null) {
            this.token = new JWTToken(Cookies.get("token"));
            if(this.token.isExpired()) {
                token = null;
            }
        }
    }

    static getInstance() {
        return new APIClient(APIClient.HOST, APIClient.REQUEST, APIClient.VERSION, APIClient.PORT);
    }

    call(endpoint, callback, payload, placeholders) {
        const request = {
            cache: false,
            type: endpoint.getMethod(),
            url: this.baseurl + endpoint.getEndpoint(placeholders),
            crossDomain: true,
            data: JSON.stringify(payload),
            success: function(json) {

                if(json.success) {
                    callback(json.result, true, null);
                } else {
                    callback(null, false, json.error);
                }
                
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.error(textStatus); 
            }
        };
        if(endpoint.requireToken()) {
            request.headers = {"Authorization": this.token.getToken()};
        }
        $.ajax(request);
    }

    login(email, password, callback) {
        this.call(APIClient.ENDPOINT_USER_LOGIN, function(json, success, error) {
            if(success) {
                const token = new JWTToken(json.token);
                Cookies.set("token", json.token, {expires: token.getRemainingDays()});
                callback(true, null);
            } else {
                callback(false, error);
            }
        }, {email: email, password: password});
    }

    logout() {
        Cookies.remove('token');
        location.reload();
    }

    isLoggedIn() {
        if(this.token != null) {

            if(!this.token.isExpired()) {
                return true;
            }
        }
    }

    getUser() {
        return new UserEntity(this.token.getUserID(), this.token.getUsername());
    }
}