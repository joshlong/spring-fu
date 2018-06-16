dependencies {
	api(project(":core"))
	api("org.springframework.data:spring-data-redis") {
		exclude(module = "org.slf4j:jcl-over-slf4j")
	}
	api("io.lettuce:lettuce-core")
}
