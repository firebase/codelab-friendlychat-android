import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.readText
import kotlin.io.path.writeText

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.13.0" apply false
    id("com.android.library") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.21" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}

allprojects {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
    }
}

tasks {
    register("clean", Delete::class) {
        delete(rootProject.buildDir)
    }

    register("rename") {
        val applicationId = project.properties["applicationId"] as String?
        val DEFAULT_APP_ID = "com.google.firebase.codelab.friendlychat"
        val DEFAULT_DIRECTORY = appIdToPath(DEFAULT_APP_ID)

        doFirst {
            // Assert that an applicationId argument was passed
            if (applicationId == null) {
                throw IllegalArgumentException(
                    "Please specify an applicationId when running this command:\n" +
                            "./gradlew rename -PapplicationId=com.example.id"
                )
            }
        }

        doLast {
            // Iterate through all files under DEFAULT_DIRECTORY
            val defaultDirectoryFile = file(DEFAULT_DIRECTORY)
            val absolutePath = Paths.get(defaultDirectoryFile.absolutePath)
            Files.walk(absolutePath).forEach { path ->
                val file = path.toFile()
                if (file.isFile) {
                    // Replace the default applicationId with the new one
                    // For the package declaration and any relevant imports
                    path.writeText(path.readText()
                        .replace("(package|import)\\s+${DEFAULT_APP_ID}".toRegex(), "$1 $applicationId"))
                }

                // Copy file to new directory
                val newDirectory = file(appIdToPath(applicationId!!))
                val newFile = file("${newDirectory.absolutePath}/${file.relativeTo(defaultDirectoryFile)}")
                file.copyTo(target = newFile)
                // Delete original file
                file.delete()
            }

            // Update applicationId and namespace in build.gradle.kts
            with (file("app/build.gradle.kts").toPath()) {
                writeText(
                    readText().replace("(namespace|applicationId)\\s*=\\s*\"${DEFAULT_APP_ID}\"".toRegex(),
                        "$1 = \"$applicationId\""))
            }
        }
    }
}

fun appIdToPath(appId: String) = "app/src/main/java/${appId.replace(".", "/")}"