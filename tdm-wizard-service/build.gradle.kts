
repositories {
    jcenter()
}

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

// https://mvnrepository.com/artifact/net.sf.py4j/py4j
    implementation("net.sf.py4j:py4j:0.10.9.3")

    implementation("javax.inject:javax.inject:1")
    "implementation"("io.reactivex.rxjava3:rxjava:3.1.3")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    implementation("de.vandermeer:asciitable:0.3.2")

}


tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    withType<Test> {
        useJUnitPlatform()
    }
}