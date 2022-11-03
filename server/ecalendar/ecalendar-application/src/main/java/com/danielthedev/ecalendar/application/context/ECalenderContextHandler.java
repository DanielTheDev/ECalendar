package com.danielthedev.ecalendar.application.context;

import io.javalin.http.Handler;

@FunctionalInterface
public interface ECalenderContextHandler {

	void handle(ECalenderContext ctx);

	public static Handler redirect(ECalenderContextHandler handler) {
		return (ctx) -> handler.handle(ctx.use(ECalenderContext.class));
	}
}
