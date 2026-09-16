plugins {
    `java-library`
    id("com.gradleup.shadow")
}

dependencies {
    implementation(project(":core"))
    compileOnly("net.md-5:bungeecord-api:26.1-R0.1-SNAPSHOT")
}

val projectVersion = project.version.toString()
tasks.processResources {
    inputs.property("version", projectVersion)
    filesMatching("bungee.yml") {
        expand("version" to projectVersion)
    }
}

tasks.shadowJar {
    archiveBaseName.set("Sqrilizz-Reports-Bungee")
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
