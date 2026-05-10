plugins {
    java
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "dev.sqrilizz"
version = "8.0"

// Версии зависимостей
val paperApiVersion = "26.1.2.build.+"
val gsonVersion = "2.11.0"
val sqliteVersion = "3.47.2.0"
val hikariVersion = "6.2.1"
val okhttpVersion = "4.12.0" // 5.x требует миграцию API
val caffeineVersion = "3.2.0"
val bstatsVersion = "3.2.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    // Paper API
    compileOnly("io.papermc.paper:paper-api:$paperApiVersion")
    
    // Kotlin (только runtime, без stdlib-jdk8)
    implementation(kotlin("stdlib"))
    
    // Core dependencies
    implementation("com.google.code.gson:gson:$gsonVersion")
    // SQLite - опционально, только если нужно (закомментировано для уменьшения размера)
    // implementation("org.xerial:sqlite-jdbc:$sqliteVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")
    
    // bStats
    implementation("org.bstats:bstats-bukkit:$bstatsVersion")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(25)
    }
    
    compileKotlin {
        // Компилируем в ту же директорию что и Java
        destinationDirectory.set(layout.buildDirectory.dir("classes/java/main"))
    }
    
    // Важно: компилируем Kotlin ДО Java
    compileJava {
        dependsOn(compileKotlin)
        options.compilerArgs.add("-Xlint:deprecation")
    }
    
    processResources {
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
    
    shadowJar {
        archiveClassifier.set("")
        
        // Минимизация JAR - удаляем неиспользуемые классы
        minimize {
            // Исключаем классы которые загружаются через рефлексию
            exclude(dependency("org.xerial:.*"))
            exclude(dependency("com.zaxxer:.*"))
        }
        
        // Relocate зависимостей для избежания конфликтов
        relocate("org.bstats", "dev.sqrilizz.SQRILIZZREPORTS.libs.bstats")
        relocate("kotlin", "dev.sqrilizz.SQRILIZZREPORTS.libs.kotlin")
        relocate("com.google.gson", "dev.sqrilizz.SQRILIZZREPORTS.libs.gson")
        relocate("okhttp3", "dev.sqrilizz.SQRILIZZREPORTS.libs.okhttp3")
        relocate("okio", "dev.sqrilizz.SQRILIZZREPORTS.libs.okio")
        relocate("com.github.benmanes.caffeine", "dev.sqrilizz.SQRILIZZREPORTS.libs.caffeine")
        
        // Exclude unnecessary files
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE*")
        exclude("META-INF/NOTICE*")
        exclude("META-INF/maven/**")
        exclude("META-INF/versions/**")
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
        exclude("META-INF/services/javax.annotation.processing.Processor")
        exclude("META-INF/native-image/**")
        exclude("META-INF/proguard/**")
        exclude("META-INF/*.kotlin_module")
        exclude("org/checkerframework/**")
        exclude("org/jetbrains/annotations/**")
        exclude("org/intellij/**")
        exclude("DebugProbesKt.bin")
        exclude("kotlin/**/*.kotlin_builtins")
        
        mergeServiceFiles()
        
        manifest {
            attributes(
                "Multi-Release" to "true",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Built-By" to "Gradle ${gradle.gradleVersion}",
                "Build-Jdk" to "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})"
            )
        }
    }
    
    build {
        dependsOn(shadowJar)
    }
    
    // Задача для копирования JAR в папку плагинов (если нужно)
    register<Copy>("copyToPlugins") {
        group = "build"
        description = "Copies the built JAR to the plugins folder"
        from(shadowJar)
        into(file("${projectDir}/run/plugins"))
        dependsOn(shadowJar)
    }
    
    runServer {
        minecraftVersion("26.1.2")
    }
}
