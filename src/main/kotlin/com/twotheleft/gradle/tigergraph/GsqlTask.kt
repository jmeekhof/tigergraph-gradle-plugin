package com.twotheleft.gradle.tigergraph

import com.twotheleft.gradle.tigergraph.Configurations.gsqlRuntime
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class GsqlTask() : GsqlAbstract() {
    @Input
    lateinit var scriptPath: String

    @TaskAction
    override fun exec() {

        val cfg: Configuration? = project.configurations.findByName(gsqlRuntime)

        if (cfg != null) {
            classpath = cfg
        }

        main = "com.tigergraph.client.Driver"
        args = buildArgs()
        logger.info("Args: $args")
        super.exec()
    }

    override fun buildArgs(): List<String> {
        val newArgs: MutableList<String> = mutableListOf<String>()

        println(connectionData.getServerName())

        newArgs.add("--ip")
        newArgs.add(connectionData.getServerName())
        newArgs += determineUser(superUser)

        newArgs.add("${project.buildDir}/$scriptPath")

        return newArgs
    }
}
