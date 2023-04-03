import java.nio.file.Paths
import net.schmizz.sshj.SSHClient
import java.util.*

plugins {
    kotlin("jvm") version "1.5.30" apply false
    kotlin("plugin.serialization") version "1.5.30" apply false

    id("org.openapi.generator") version "5.3.0"
}

buildscript {
    repositories {
        mavenLocal()
    }
    dependencies { classpath("com.hierynomus:sshj:0.32.0") }
}

repositories {
    jcenter()
    mavenLocal()
    maven("https://dl.bintray.com/theodm94/maven/")
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    group = "de.theodm"
    version = "0.0.1"


    repositories {
        jcenter()
        mavenLocal()
        maven("https://dl.bintray.com/theodm94/maven/")
    }
}

tasks.register("deployServer", Task::class.java) {
    dependsOn(":play-with-friends-server:shadowJar")

    this.doLast {
        val ssh = SSHClient()

        val props = Properties()
            .apply {
                load(rootDir.resolve("keys.properties").inputStream())
            }

        ssh.addHostKeyVerifier(props["DEPLOY_HOST_KEY_VERIFIER"].toString())

        ssh.connect(props["DEPLOY_HOST"].toString())
        ssh.authPassword(props["DEPLOY_HOST_USER"].toString(), props["DEPLOY_HOST_PASSWORD"].toString())

        try {
            /**
             * Executes a command on a remote host using SSH and prints the command, the output and the return code.
             * @param cmd The command to execute on the remote host.
             * @throws IOException If an I/O error occurs.
             */
            fun exec(cmd: String) {
                // Start a new session and execute the command
                val session = ssh.startSession()

                try {
                    // Print the command
                    println("[Command]: $cmd")

                    // Execute the command and get the output stream
                    val command = session.exec(cmd)
                    val output = command.inputStream.bufferedReader().readText()

                    // Print the output
                    println("Output: $output")

                    // Wait for the command to finish and get the return code
                    command.join()

                    val returnCode = command.exitStatus

                    // Print the return code
                    println("Return code: $returnCode")
                    println("")
                } finally {
                    // Close the session
                    session.close()
                }
            }

            exec("sudo mkdir /home/apps/wizard -p")

            exec("sudo chmod a+rwx /home/apps")
            exec("sudo chmod a+rwx /home/apps/wizard")

            exec("sudo systemctl stop wizard")
            exec("sudo useradd wizard")
            exec("sudo groupadd wizard")
            exec("sudo usermod -a -G wizard wizard")

            ssh.newSCPFileTransfer().upload(projectDir.resolve("play-with-friends-server").resolve("build").resolve("libs").resolve("play-with-friends-server-0.0.1-all.jar").absolutePath, "/home/apps/wizard/wizard-server.jar")

            exec("sudo chmod a+x /home/apps/wizard/wizard-server.jar")

            ssh.newSCPFileTransfer().upload(projectDir.resolve("wizard.service").absolutePath, "/etc/systemd/system/wizard.service")

            exec("sudo systemctl enable wizard")
            exec("sudo systemctl start wizard")

            // Hochladen der chameleon.conf Datei in den nginx sites-available Ordner
            ssh.newSCPFileTransfer().upload(projectDir.resolve("wizard.conf").absolutePath, "/etc/nginx/sites-available/wizard.conf")
            // Erstellen eines symbolischen Links von der wizard.conf Datei in den nginx sites enabled Ordner
            exec("sudo ln -s /etc/nginx/sites-available/wizard.conf /etc/nginx/sites-enabled/wizard.conf")
            // Testen der nginx Konfiguration auf Fehler
            exec("sudo nginx -t")
            // Neustarten von nginx
            exec("sudo systemctl restart nginx")
        } finally {
            ssh.disconnect()
        }
    }
}