// build.gradle.kts (en la raíz de tu proyecto)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") // Aplica el plugin de Kotlin JVM
    id("org.jetbrains.compose") // Aplica el plugin de Compose Desktop
    // ¡NUEVO! O de nuevo, agrega esta línea para Kotlin 2.0.0
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "com.gestorpeliculas"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("mysql:mysql-connector-java:8.0.30") // O tu versión preferida
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0")
    implementation(compose.material)
    implementation(compose.foundation)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "GestorPeliculas"
            packageVersion = "1.0.0"
        }
    }
}