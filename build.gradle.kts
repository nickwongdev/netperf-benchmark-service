import org.gradle.api.tasks.bundling.Jar

/* Apply the plugins to the build */
plugins {
	kotlin("jvm") version "1.3.11"
	`maven-publish`
}

/* Describe what we are building */
group = "com.nickwongdev.netperf"
version = "1.0-SNAPSHOT"

/* Where to pull dependencies from */
repositories {
	mavenLocal()
	mavenCentral()
}

/* Gather Sources */
val sourcesJar by tasks.registering(Jar::class) {
	classifier = "sources"
	from(sourceSets.main.get().allSource)
}

/* Dependencies to build the project */
dependencies {
	compile(kotlin("stdlib"))
	compile("org.jetbrains.kotlin:kotlin-reflect")
	compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.0")
	testCompile("org.junit.jupiter:junit-jupiter-api:5.3.2")
}

/* How to publish */
publishing {
	repositories {
		mavenLocal()
	}
	publications {
		register("mavenJava", MavenPublication::class) {
			from(components["java"])
			artifact(sourcesJar.get())
		}
	}
}