plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

group = "io.github.jwyoon1220"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://maven.leafmc.one/snapshots/")
}

dependencies {
    compileOnly("cn.dreeam.leaf:leaf-api:1.21.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    // https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
    implementation("org.xerial:sqlite-jdbc:3.51.1.0")
    paperweight.devBundle("cn.dreeam.leaf", "1.21.4-R0.1-SNAPSHOT")
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.4")
        minHeapSize = "512m"
        maxHeapSize = "4096m"
    }
}
tasks.register<JavaExec>("leaf") {

    group = "run"
    description = "Build plugin with shadowJar and run Leaf server"

    /* ===== 선행 작업 ===== */
    dependsOn(tasks.shadowJar)

    doFirst {
        val shadowJar = tasks.shadowJar.get()
        val pluginJar = shadowJar.archiveFile.get().asFile

        val pluginsDir = file("run/plugins")
        pluginsDir.mkdirs()

        pluginJar.copyTo(
            File(pluginsDir, pluginJar.name),
            overwrite = true
        )
    }

    /* ===== 서버 실행 ===== */
    workingDir = file("run")

    mainClass.set("-jar")
    args("leaf-1.21.4-524.jar", "--nogui")

    standardInput = System.`in`

    jvmArgs(
        "-Xms8G",
        "-Xmx8G",
        "-XX:+UseG1GC",
        "-XX:+ParallelRefProcEnabled",
        "-XX:MaxGCPauseMillis=200",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+DisableExplicitGC"
    )
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
