import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

plugins {
    `java-library`
    `maven-publish`
    signing
}

group = "com.infoyupay.humandate"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    // https://mvnrepository.com/artifact/org.assertj/assertj-core
    testImplementation("org.assertj:assertj-core:3.27.6")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

signing {
    val keyIdProp = findProperty("signing.keyId") as String?
    val passwordProp = findProperty("signing.password") as String?
    val secretKeyFileProp = findProperty("signing.secretKeyRingFile") as String?

    require(!keyIdProp.isNullOrBlank()) { "Missing signing.keyId in ~/.gradle/gradle.properties" }
    require(!passwordProp.isNullOrBlank()) { "Missing signing.password in ~/.gradle/gradle.properties" }
    require(!secretKeyFileProp.isNullOrBlank()) { "Missing signing.secretKeyRingFile in ~/.gradle/gradle.properties" }

    val secretKeyContent = file(secretKeyFileProp).readText()

    useInMemoryPgpKeys(
        keyIdProp,
        secretKeyContent,
        passwordProp
    )

    sign(publishing.publications["mavenJava"])
}


fun generateChecksum(file: File, algorithm: String): String {
    val digest = MessageDigest.getInstance(algorithm)
    val buffer = ByteArray(8192)
    FileInputStream(file).use { fis ->
        var read = fis.read(buffer)
        while (read > 0) {
            digest.update(buffer, 0, read)
            read = fis.read(buffer)
        }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}

tasks.register("generateChecksums") {
    dependsOn(tasks.named("publishToMavenLocal"))

    doLast {
        val libsDir = layout.buildDirectory.dir("libs").get().asFile
        val pomDir  = layout.buildDirectory.dir("publications/mavenJava").get().asFile

        val targets = libsDir.listFiles()?.toList().orEmpty() +
                pomDir.listFiles()?.toList().orEmpty()

        targets.filter { it.isFile }.forEach { file ->
            listOf("MD5" to "md5", "SHA-1" to "sha1").forEach { (algo, ext) ->
                val checksum = generateChecksum(file, algo)
                File("${file.absolutePath}.$ext").writeText(checksum)
            }
        }
    }
}
tasks.register<Zip>("releaseZip") {
    group = "distribution"
    archiveClassifier.set("release")

    dependsOn("publishToMavenLocal", "generateChecksums")

    from(layout.buildDirectory.dir("libs")) {
        include("*.jar")
        include("*.jar.asc")
        include("*.jar.md5")
        include("*.jar.sha1")
    }
    from(layout.buildDirectory.dir("publications/mavenJava")) {
        include("*.pom")
        include("*.pom.asc")
        include("*.pom.md5")
        include("*.pom.sha1")
    }
}
