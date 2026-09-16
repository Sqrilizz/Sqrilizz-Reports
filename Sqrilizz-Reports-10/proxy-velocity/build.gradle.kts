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

tasks.processResources {
    val ver = project.version.toString()
    inputs.property("version", ver)
    filesMatching("velocity-plugin.json") {
        expand("version" to ver)
    }
}

tasks.shadowJar {
    archiveBaseName.set("Sqrilizz-Reports-Velocity")
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
