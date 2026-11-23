plugins {
    `java-library`
    id("com.gradleup.shadow")
}

dependencies {
    implementation(project(":core"))
    compileOnly("net.md-5:bungeecord-api:26.1-R0.1-SNAPSHOT")
}

tasks.processResources {
    val ver = project.version.toString()
    inputs.property("version", ver)
    filesMatching("bungee.yml") {
        expand("version" to ver)
    }
}

tasks.shadowJar {
    archiveBaseName.set("Sqrilizz-Reports-Bungee")
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
