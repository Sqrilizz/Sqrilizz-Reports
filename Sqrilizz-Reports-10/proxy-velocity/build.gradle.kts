plugins {
    `java-library`
    id("com.gradleup.shadow")
}

val velocityVersion = "3.4.0-SNAPSHOT"

dependencies {
    implementation(project(":core"))
    compileOnly("com.velocitypowered:velocity-api:$velocityVersion")
    annotationProcessor("com.velocitypowered:velocity-api:$velocityVersion")
}

val projectVersion = project.version.toString()
tasks.processResources {
    inputs.property("version", projectVersion)
    filesMatching("velocity-plugin.json") {
        expand("version" to projectVersion)
    }
}

tasks.shadowJar {
    archiveBaseName.set("Sqrilizz-Reports-Velocity")
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
