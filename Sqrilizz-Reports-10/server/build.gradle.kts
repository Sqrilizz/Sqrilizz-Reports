import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("com.gradleup.shadow")
}

val botImplementation by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = false
}

val botRuntime by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    extendsFrom(botImplementation)
}

configurations.compileClasspath.get().extendsFrom(botImplementation)

dependencies {
    implementation(project(":core"))
    compileOnly("io.papermc.paper:paper-api:26.3.build.+")
    compileOnly("com.google.code.gson:gson:2.14.0")
    implementation("com.zaxxer:HikariCP:6.3.0")

    add("botImplementation", "org.xerial:sqlite-jdbc:3.50.3.0")
    add("botImplementation", "com.mysql:mysql-connector-j:9.3.0")
}

tasks.processResources {
    val ver = project.version.toString()
    filteringCharset = "UTF-8"
    inputs.property("version", ver)
    filesMatching("plugin.yml") {
        expand("version" to ver)
    }
}

tasks.shadowJar {
    archiveBaseName.set("Sqrilizz-Reports-Standard-Edition")
    archiveClassifier.set("")
    relocate("com.zaxxer.hikari", "dev.sqrilizz.reports.libs.hikari")

    exclude("META-INF/maven/**")
    exclude("META-INF/native-image/**")
    exclude("META-INF/proguard/**")
}

val botShadowJar = tasks.register<ShadowJar>("botShadowJar") {
    group = "build"
    description = "Builds Sqrilizz-Reports Bot Edition with offline bundled database drivers"
    archiveBaseName.set("Sqrilizz-Reports-Bot-Edition")
    archiveClassifier.set("")
    from(sourceSets.main.get().output)
    configurations = listOf(project.configurations.runtimeClasspath.get(), botRuntime)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    relocate("com.zaxxer.hikari", "dev.sqrilizz.reports.libs.hikari")
    relocate("org.sqlite", "dev.sqrilizz.reports.libs.sqlite")
    relocate("com.mysql", "dev.sqrilizz.reports.libs.mysql")

    exclude("META-INF/maven/**")
    exclude("META-INF/native-image/**")
    exclude("META-INF/proguard/**")
}

tasks.build {
    dependsOn(tasks.shadowJar, botShadowJar)
}

