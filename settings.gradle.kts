rootProject.name = "spring-fu-build"

include("bootstraps",
		"core",
		"dependencies",
		"docs",
		"modules",
		"modules:logging",
		"modules:logging-logback",
		"modules:dynamic-configuration",
		"modules:jackson",
		"modules:redis",
		"modules:mongodb",
		"modules:mongodb-coroutine",
		"modules:mustache",
		"modules:test",
		"modules:webflux",
		"modules:webflux-coroutine",
		"modules:webflux-netty",
		"modules:webflux-jetty",
		"modules:webflux-tomcat",
		"modules:webflux-undertow",
		"modules:webflux-test-common",
		"samples:coroutine-webapp",
		"samples:minimal-webapp",
		"samples:reactive-webapp")

