import com.github.gradle.node.npm.task.NpmTask
import com.github.gradle.node.task.NodeTask

plugins {
    id("com.github.node-gradle.node") version "3.1.1"
}

tasks.register("buildClient", NpmTask::class.java) {
    dependsOn(tasks.getByName("npmInstall"))

    inputs.files(fileTree("node_modules"))
    inputs.files(fileTree("src"))
    inputs.files(fileTree("public"))
    inputs.files(fileTree("types"))

    inputs.file("package.json")
    inputs.file("package-lock.json")
    inputs.file("postcss.config.js")
    inputs.file("snowpack.config.mjs")
    inputs.file("svelte.config.js")
    inputs.file("tailwind.config.js")
    inputs.file("tsconfig.json")
    inputs.file("web-test-runner.config.js")

    outputs.dir("build")

    args.set(listOf("run", "build"))
}

tasks.register("copyAndBuildClient", Copy::class.java) {
    dependsOn(tasks.getByName("buildClient"))

    from(projectDir
        .toPath()
        .resolve("build")
        .toFile())
    into(project(":play-with-friends-server")
        .projectDir
        .toPath()
        .resolve("src")
        .resolve("generated")
        .resolve("resources")
        .resolve("public")
        .toFile()
    )
}

node {
    download.set(false)

    version.set("16.13.2")
}