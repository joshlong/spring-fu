dependencies {
	api(project(":core"))
	api("org.springframework.data:spring-data-redis") {

		//
//		<dependency>
//		<groupId>org.springframework.data</groupId>
//		<artifactId>spring-data-redis</artifactId>
//		<version>2.0.7.RELEASE</version>
//		<scope>compile</scope>
//		<exclusions>
//		<exclusion>
//		<artifactId>jcl-over-slf4j</artifactId>
//		<groupId>org.slf4j</groupId>
//		</exclusion>
//		</exclusions>
//		</dependency>
//		<dependency>
//		<groupId>io.lettuce</groupId>
//		<artifactId>lettuce-core</artifactId>
//		<version>5.0.4.RELEASE</version>
//		<scope>compile</scope>
//		</dependency>
//		</dependencies>

		exclude(module = "org.slf4j:jcl-over-slf4j")
	}
	api("io.lettuce:lettuce-core")
}
