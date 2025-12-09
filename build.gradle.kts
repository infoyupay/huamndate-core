import java.io.FileInputStream
import java.security.MessageDigest

plugins {
    `java-library`
    `maven-publish`
    signing
}

group = "com.infoyupay.humandate"
version = "1.0.0"

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
            pom {
                name.set("HumanDate Core")
                description.set("Lightweight human-friendly LocalDate parsing and formatting with multilingual support (EN/ES/QUE)")
                url.set("https://github.com/infoyupay/humandate-core")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.html")
                        distribution.set("repo")
                    }
                }

                scm {
                    url.set("https://github.com/infoyupay/humandate-core")
                    connection.set("scm:git:https://github.com/infoyupay/humandate-core.git")
                    developerConnection.set("scm:git:ssh://git@github.com/infoyupay/humandate-core.git")
                    tag.set("HEAD")
                }

                developers {
                    developer {
                        id.set("dvidal")
                        name.set("David Vidal")
                        email.set("info@infoyupay.com")
                        url.set("https://infoyupay.com")
                        organization.set("InfoYupay S.A.C.S.")
                        organizationUrl.set("https://infoyupay.com")
                    }
                }
            }

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

tasks.register("renamePom") {
    dependsOn(tasks.named("publishToMavenLocal"))

    doLast {
        val pubDir = layout.buildDirectory.dir("publications/mavenJava").get().asFile

        val originalPom = File(pubDir, "pom-default.xml")
        val originalAsc = File(pubDir, "pom-default.xml.asc")

        val newPom = File(pubDir, "humandate-core-${project.version}.pom")
        val newAsc = File(pubDir, "humandate-core-${project.version}.pom.asc")

        if (!originalPom.exists()) {
            throw GradleException("Expected pom-default.xml not found in $pubDir")
        }

        // Rename POM
        originalPom.copyTo(newPom, overwrite = true)
        originalPom.delete()

        // Rename signature (if exists)
        if (originalAsc.exists()) {
            originalAsc.copyTo(newAsc, overwrite = true)
            originalAsc.delete()
        } else {
            logger.warn("pom-default.xml.asc not found – signature may not have been generated")
        }
    }
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
tasks.named("generateChecksums") {
    dependsOn("renamePom")
}
tasks.register<Zip>("releaseZip") {
    group = "distribution"
    archiveClassifier.set("release")

    dependsOn("publishToMavenLocal", "generateChecksums")

    // Maven repository layout: com/infoyupay/humandate/humandate-core/1.0.0/
    val targetDir = "com/infoyupay/humandate/humandate-core/${project.version}"

    into(targetDir) {
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
}
