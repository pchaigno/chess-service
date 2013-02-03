package com.sun.jersey.TPTestApplication;

import javax.ws.rs.ApplicationPath;

import com.sun.jersey.api.core.PackagesResourceConfig;

@ApplicationPath("resources")
public class MyApp extends PackagesResourceConfig {
	public MyApp() {
	          super("com.sun.jersey.TPTestApplication.resources1" +
	          		";com.sun.jersey.TPTestApplication.resources1");
	}
}
