plugins {
	base
	java
	idea
	`maven-publish`
	alias(libs.plugins.fabric.loom)
}

group = libs.versions.maven.group.get()
version = "${libs.versions.minecraft.get()}-${libs.versions.mod.get()}"

base {
	archivesName.set(libs.versions.archives.name)
}

repositories {
	mavenCentral()
}

dependencies {
	minecraft(libs.minecraft)
	mappings(libs.yarn)
	modImplementation(libs.bundles.fabric)

	implementation(libs.mixbox)?.let {
		include(it)
	}

	implementation(libs.word.wrap)?.let {
		include(it)
	}
	implementation(libs.guava.mini)?.let {
		include(it)
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17

	withSourcesJar()
}

tasks {
	processResources {
		inputs.property("version", libs.versions.mod.get())

		filesMatching("fabric.mod.json") {
			expand(mapOf("version" to libs.versions.mod.get()))
		}
	}

	jar {
		from("LICENSE") {
			rename { "${it}_${base.archivesName}" }
		}
	}

	register("collectJars", Copy::class.java) {
		group = "build"

		val destination = file("${layout.buildDirectory.get()}/libs/latest")

		from("${layout.buildDirectory.get()}/libs/${base.archivesName.get()}-$version.jar")
		from("${layout.buildDirectory.get()}/libs/${base.archivesName.get()}-$version-sources.jar")

		subprojects.forEach {
			from("${it.layout.buildDirectory.get()}/libs/${it.base.archivesName.get()}-$version.jar")
			from("${it.layout.buildDirectory.get()}/libs/${it.base.archivesName.get()}-$version-sources.jar")
		}

		into(destination)
	}

	register("deleteCollectedJars", Delete::class.java) {
		group = "build"

		val destination = file("${layout.buildDirectory.get()}/libs/latest")
		destination.delete()

		onlyIf { destination.exists() }
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	repositories {
	}
}
