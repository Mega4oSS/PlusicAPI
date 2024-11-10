plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "ru.artem.alaverdyan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.javassist:javassist:3.27.0-GA")
    implementation(fileTree("libs") {
        include("*.jar")
    })
    implementation("com.thoughtworks.paranamer:paranamer:2.8")
    implementation("ch.qos.logback:logback-classic:1.4.7")
    implementation("net.java.dev.jna:jna:5.13.0")
    implementation("net.java.dev.jna:jna-platform:5.13.0")
    implementation("org.fusesource.jansi:jansi:2.4.1")

}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType(Jar::class) {
    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Main-Class"] = "ru.artem.alaverdyan.PlusicInjector"
        attributes["Can-Retransform-Classes"] = true;
        attributes["Can-Redefine-Classes"] = true;
    }

}

tasks.withType<JavaCompile> { options.compilerArgs.add("-parameters") }
