plugins {
    `java-library`
    id("com.gradleup.shadow")
}

dependencies {
    implementation(project(":core"))
    compileOnly("io.papermc.paper:paper-api:26.3.build.+")
    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("org.xerial:sqlite-jdbc:3.50.3.0")
    implementation("com.mysql:mysql-connector-j:9.3.0")
    implementation("com.google.code.gson:gson:2.14.0")
}

val projectVersion = project.version.toString()
tasks.processResources {
    filteringCharset = "UTF-8"
    inputs.property("version", projectVersion)
    filesMatching("plugin.yml") {
        expand("version" to projectVersion)
    }
}

tasks.shadowJar {
    archiveBaseName.set("Sqrilizz-Reports-Server")
    archiveClassifier.set("")
    relocate("com.zaxxer.hikari", "dev.sqrilizz.reports.libs.hikari")
    relocate("org.sqlite", "dev.sqrilizz.reports.libs.sqlite")
    relocate("com.mysql", "dev.sqrilizz.reports.libs.mysql")
    relocate("com.google.gson", "dev.sqrilizz.reports.libs.gson")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
