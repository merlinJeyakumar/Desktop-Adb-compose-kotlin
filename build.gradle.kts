import org.jetbrains.compose.ComposePlugin.Dependencies
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.1"
}

group = "me.a1398"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.materialIconsExtended)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
    implementation("commons-io:commons-io:2.11.0")
    implementation(compose.desktop.currentOs)
    implementation(Dependencies.uiTooling)
    implementation(Dependencies.ui)
    implementation(Dependencies.preview)
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("org.apache.commons:commons-exec:1.3")
    implementation(files("libs/org.nmap4j-1.1.0.jar"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "SmartConnect"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "SmartConnect"
            packageVersion = "1.0.0"
        }
    }
}