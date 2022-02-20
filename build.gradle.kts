import de.theodm.tinywebapigen.generator.dsl.*
import de.theodm.tinywebapigen.generator.dsl.AuthConfig.SessionKeyAuth
import java.nio.file.Paths

plugins {
    kotlin("jvm") version "1.4.30" apply false
    kotlin("plugin.serialization") version "1.4.30" apply false

    id("org.openapi.generator") version "5.3.0"
}

buildscript {
    repositories {
        mavenLocal()
        jcenter()
    }

    dependencies {
        classpath("de.theodm:tiny-webapi-gen-generator:0.0.7-SNAPSHOT")
    }
}

repositories {
    jcenter()
    mavenLocal()
    maven("https://dl.bintray.com/theodm94/maven/")
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    group = "de.theodm"
    version = "0.0.1"


    repositories {
        jcenter()
        mavenLocal()
        maven("https://dl.bintray.com/theodm94/maven/")
    }
}

tasks.register("deploy", Task::class.java) {
    dependsOn(":play-with-friends-server:shadowJar")

    doLast {
        exec {
            commandLine("docker", "build", ".", "-t", "tdm-online-games")
        }

        exec {
            executable = "C:\\Program Files\\heroku\\bin\\heroku.cmd"
            args = listOf("container:push", "web", "--app", "tdm-online-games")
        }

        exec {
            executable = "C:\\Program Files\\heroku\\bin\\heroku.cmd"
            args = listOf("container:release", "web", "--app", "tdm-online-games")
        }

    }
}