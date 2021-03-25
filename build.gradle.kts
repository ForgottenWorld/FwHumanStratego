
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.serialization") version "1.4.31"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.gmail.samueler53"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io" )
}

dependencies {
    compileOnly("com.github.ForgottenWorld.FWEchelon:fwechelon:v0.0.8-alpha2")
    compileOnly("org.spigotmc:spigot-api:1.16.3-R0.1-SNAPSHOT")
    compileOnly("com.github.BrunoSilvaFreire:Skedule:0.1.3")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    compileOnly("com.charleskorn.kaml:kaml:0.29.0")

    implementation("com.github.stefvanschie.inventoryframework:IF:0.7.2")
    implementation("fr.mrmicky:FastBoard:1.1.0")
}

tasks.withType<ProcessResources> {
    from(sourceSets["main"].resources.srcDirs) {
        expand("version" to version)
    }
}

tasks.withType<ShadowJar> {
    dependencies {
        val included = setOf(
            "com.gmail.samueler53",
            "fr.mrmicky",
            "com.github.stefvanschie.inventoryframework"
        )
        exclude {
            !included.contains(it.moduleGroup)
        }
    }
    relocate("com.github.stefvanschie.inventoryframework", "com.gmail.samueler53.inventoryframework")
    relocate("fr.mrmicky.fastboard", "com.gmail.samueler53.fastboard")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}