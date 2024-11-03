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
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/org.javassist/javassist
    implementation("org.javassist:javassist:3.27.0-GA")
    implementation(fileTree("libs") {
        include("*.jar")
    })
    implementation("com.thoughtworks.paranamer:paranamer:2.8")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
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