plugins {
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    jcenter()
}

val ktor_version = "1.4.0"
val pac4j_version = "4.2.0"

dependencies {
    // Standard-Bibliothek
    "implementation"(kotlin("stdlib-jdk8"))

    // Kotlin-Logging Framework
    "implementation"("io.github.microutils:kotlin-logging:1.5.4")

    // Logback für das Logging
    "implementation"("ch.qos.logback:logback-classic:1.2.3")

    // JUnit für Tests
    "testImplementation"("org.junit.jupiter:junit-jupiter:5.4.2")

    // Truth für Assertions
    "testImplementation"("com.google.truth:truth:1.1")

    implementation("javax.inject:javax.inject:1")

    "implementation"("io.javalin:javalin:4.1.1")
    "implementation"("io.javalin:javalin-openapi:4.1.1")

    "implementation"("io.reactivex.rxjava3:rxjava:3.1.3")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

// https://mvnrepository.com/artifact/net.sf.py4j/py4j
    implementation("net.sf.py4j:py4j:0.10.9.3")

    "implementation"("com.fasterxml.jackson.core:jackson-databind:2.13.1")
//    "implementation"("com.fasterxml.jackson.module:jackson-module-parameter-names:2.13.1")
//    "implementation"("com.fasterxml.jackson.module:jackson-datatype-jsr310:2.13.1")
//    "implementation"("com.fasterxml.jackson.module:jackson-datatype-jdk8:2.13.1")

    "implementation"(project(":play-with-friends-commons"))
    "implementation"(project(":tdm-lobby-service"))
    "implementation"(project(":tdm-wizard-service"))

    // Guice als DI-Framework
    "implementation"("com.google.inject:guice:5.0.1")
}

sourceSets {
    main {
        java {
            srcDir(buildDir.resolve("generated/main"))
        }
    }

    test {
        java {
            srcDir(buildDir.resolve("generated/test"))
        }
    }
}

tasks
    .getByName("processResources")
    .dependsOn(":client:copyAndBuildClient")

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
        kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
        kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
    }
    withType<Test> {
        useJUnitPlatform()
    }
}

ktlint {
    version.set("0.40.0")
    debug.set(true)

    filter {
        include("**/generated/**")
        include("**/kotlin/**")
    }

    disabledRules.add("no-wildcard-imports")
}

sourceSets["main"].java {
    srcDir("src/generated/kotlin")
}

sourceSets["main"].resources {
    srcDir("src/generated/resources")
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "de.theodm.pwf.MainKt"
    }
}