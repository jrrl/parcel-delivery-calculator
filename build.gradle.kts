plugins {
	id("org.springframework.boot") version "2.4.6"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	java
}

group = "com.mynt.test"
version = project.version
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.3")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}