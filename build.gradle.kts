import org.gradle.api.tasks.testing.Test

plugins {
    java
    alias(libs.plugins.quarkus)
    alias(libs.plugins.lombok)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform(libs.quarkus.bom))
    implementation(libs.quarkus.arc)
    implementation(libs.quarkus.jdbcMssql)
    implementation(libs.quarkus.jdbcPostgressql)
    implementation(libs.quarkus.reastyReactive)
    implementation(libs.quarkus.reastyReactiveJackson)
    implementation(libs.quarkus.slf4j)

    implementation(libs.jdbi.core)
    implementation(libs.jdbi.sqlobject)

    testImplementation(libs.test.quarkus.junit5)
    testImplementation(libs.test.quarkus.restAssured)
    testImplementation(libs.test.assertj)
    testImplementation(libs.test.testcontainers)
}

group = "org.acme"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test>().configureEach {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
