package com.danielthedev.ecalendar.application;

import static com.danielthedev.ecalendar.application.context.ECalenderContextHandler.redirect;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.danielthedev.ecalendar.application.context.ECalenderContext;
import com.danielthedev.ecalendar.application.handlers.CalendarHandler;
import com.danielthedev.ecalendar.application.handlers.CalendarItemHandler;
import com.danielthedev.ecalendar.application.handlers.SharedCalendarHandler;
import com.danielthedev.ecalendar.application.handlers.SharedCalendarItemHandler;
import com.danielthedev.ecalendar.application.handlers.UserHandler;

import io.javalin.Javalin;

public class ECalendarServer {

	private final String baseURL = "/api/v1/";

	private final UserHandler userHandler = new UserHandler();
	private final CalendarHandler calendarHandler = new CalendarHandler();
	private final CalendarItemHandler calendarItemHandler = new CalendarItemHandler();
	private final SharedCalendarHandler sharedCalendarHandler = new SharedCalendarHandler();
	private final SharedCalendarItemHandler sharedCalendarItemHandler = new SharedCalendarItemHandler();

	private final int port;
	private final String host;
	private final Javalin server;

	public ECalendarServer(String host, int port) {
		this.host = host;
		this.port = port;
		this.server = Javalin.create((config) -> config.defaultContentType = "application/json");
		this.setRoutes();
		this.setExceptionHandler();
		this.registerCustomContext();
	}

	private void setRoutes() {
		this.server.routes(() -> {
			path(baseURL, () -> {
				path("/user/", () -> {
					post("login", redirect(this.userHandler::login));
					post("register", redirect(this.userHandler::register));
					post("get", redirect(this.userHandler::get));
				});
				path("/calendar/", () -> {
					post("create", redirect(this.calendarHandler::create));
					post("list", redirect(this.calendarHandler::list));
					post("delete/:calendarID", redirect(this.calendarHandler::delete));
					post("edit/:calendarID", redirect(this.calendarHandler::edit));
				});

				path("/calendar/:calendarID/", () -> {
					post("get", redirect(this.calendarItemHandler::get));
					post("create", redirect(this.calendarItemHandler::create));
					post("edit/:calendarItemID", redirect(this.calendarItemHandler::edit));
					post("delete/:calendarItemID", redirect(this.calendarItemHandler::delete));
				});

				path("/calendar/:calendarID/share/", () -> {
					post("add/:userID", redirect(this.sharedCalendarHandler::add));
					post("edit/:userID", redirect(this.sharedCalendarHandler::edit));
					post("remove/:userID", redirect(this.sharedCalendarHandler::remove));
				});

				path("/calendar/:calendarID/share/:calendarItemID/", () -> {
					post("add/:userID", redirect(this.sharedCalendarItemHandler::add));
					post("remove/:userID", redirect(this.sharedCalendarItemHandler::remove));
					post("get/", redirect(this.sharedCalendarItemHandler::get));
				});
			});
		}).after(ctx-> {
			if(ctx.method() == "OPTIONS") {
				ctx.status(HttpURLConnection.HTTP_OK);
			}
		});
	}

	private void registerCustomContext() {
		this.server.before(ctx -> ctx.register(ECalenderContext.class, new ECalenderContext(ctx)));
	}

	private void setExceptionHandler() {
		this.server.exception(JSONException.class, (exception, ctx) -> {
			ctx.json(new JSONObject().put("success", false).put("error", "invalid json").toMap());
		}).exception(RuntimeException.class, (exception, ctx) -> {
			if(exception.getClass() != RuntimeException.class) exception.printStackTrace();
		});
	}

	public void startServer() {
		this.server.start(this.host, this.port);
	}
}
