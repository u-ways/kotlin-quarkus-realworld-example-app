plugins {
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.allopen") version "1.4.32"
    /**
     * FIXME: This plugin is used to resolve JPA constructor hell issues in Kotlin,
     *  but it is not working as expected in Quarkus atm. See:
     *  https://quarkusio.zulipchat.com/#narrow/stream/187030-users/topic/kotlin.2Eplugin.2Enoarg/near/245461656
     */
    kotlin("plugin.jpa") version "1.4.32"
    id("io.quarkus")
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-hibernate-orm-panache-kotlin")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkus:quarkus-jdbc-h2")
    implementation("io.quarkus:quarkus-resteasy-jackson")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-resteasy")
    implementation("io.quarkus:quarkus-smallrye-jwt")
    implementation("io.quarkus:quarkus-smallrye-jwt-build")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.+")
    implementation("at.favre.lib:bcrypt:0.9.0")
    testImplementation("io.quarkus:quarkus-junit5-mockito")
    testImplementation("io.quarkus:quarkus-test-security-jwt")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    testImplementation("nl.jqno.equalsverifier:equalsverifier:3.5")
}

/** Kotlin linter settings ************************/
ktlint {
    verbose.set(true)
}

group = "io.realworld"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
    kotlinOptions.javaParameters = true
}
