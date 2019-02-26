package com.twotheleft.gradle.tigergraph

import com.twotheleft.gradle.tigergraph.Configurations.extensionName
import com.twotheleft.gradle.tigergraph.Configurations.gsqlRuntime
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.TaskProvider

open class GsqlPlugin : Plugin<Project> {
    /**
     * The name of the task that copies the GSQL source files into the build directory.
     *
     * @see com.twotheleft.gradle.tigergraph.GsqlCopySources
     */
    val copySourcesTaskName = "gsqlCopySources"

    /**
     * The name of the task that runs the interactive GSQL shell.
     *
     * @see com.twotheleft.gradle.tigergraph.GsqlShell
     */
    val gsqlShellTaskName = "gsqlShell"

    val defaultGsqlScriptsDirectory = "db_scripts"

override fun apply(project: Project): Unit = project.run {
    // Register extension for dsl
    val gsqlPluginExtension = extensions.create(extensionName, GsqlPluginExtension::class.java, project)
    gsqlPluginExtension.scriptDir.set(layout.projectDirectory.dir(defaultGsqlScriptsDirectory))
    gsqlPluginExtension.outputDir.set(layout.buildDirectory.dir(defaultGsqlScriptsDirectory))

    logger.info(gsqlPluginExtension.scriptDir.toString())

    registerGsqlCopySourcesTask(gsqlPluginExtension)
    registerGsqlTask(gsqlPluginExtension)

    logger.info(gsqlPluginExtension.toString())

    // Create CopySources task
    /*
    project.tasks.run {
        create(gsqlShellTaskName, GsqlShell::class.java)
    }
    */

    with(project) {
        logger.lifecycle("GSQL Plugin successfully applied to ${project.name}")
        /*
        tasks.withType(GsqlTask::class.java) { task ->
            logger.info("${task.name}: is of type GsqlTask")
            task.dependsOn(gsqlCopySources)
        }
        */
        configurations.maybeCreate(gsqlRuntime)
                .description = "Gsql Runtime for Tigergraph Plugin"

        dependencies.add(gsqlRuntime, "com.tigergraph.client:Driver:2.1.7")
        dependencies.add(gsqlRuntime, "commons-cli:commons-cli:1.4")
        dependencies.add(gsqlRuntime, "jline:jline:2.11")
        dependencies.add(gsqlRuntime, "org.json:json:20180130")
        dependencies.add(gsqlRuntime, "javax.xml.bind:jaxb-api:2.3.1")
    }
}

private fun Project.registerGsqlCopySourcesTask(gsqlPluginExtension: GsqlPluginExtension): TaskProvider<GsqlCopySources> =
        tasks.register(copySourcesTaskName, GsqlCopySources::class.java) { gsqlCopySources ->
            gsqlCopySources.group = JavaBasePlugin.BUILD_TASK_NAME
            gsqlCopySources.description = "Copy gsql scripts from input directory to build directory prior to execution."
            gsqlCopySources.inputDir.set(gsqlPluginExtension.scriptDir)
            gsqlCopySources.outputDir.set(gsqlPluginExtension.outputDir)
            // gsqlCopySources.outputDir
            // gsqlCopySources.tokens.set(gsqlPluginExtension.tokens)
        }

private fun Project.registerGsqlTask(gsqlPluginExtension: GsqlPluginExtension): TaskProvider<GsqlTask> =
        tasks.register(gsqlShellTaskName, GsqlTask::class.java) { gsqlTask ->
            gsqlTask.dependsOn(copySourcesTaskName)
            gsqlTask.connectionData.setAdminUserName(gsqlPluginExtension.adminUserName)
            gsqlTask.connectionData.setAdminPassword(gsqlPluginExtension.adminPassword)
            gsqlTask.connectionData.setUserName(gsqlPluginExtension.userName)
            gsqlTask.connectionData.setPassword(gsqlPluginExtension.password)
            gsqlTask.connectionData.setServerName(gsqlPluginExtension.serverName)

            logger.info("serverName: ${gsqlTask.connectionData.getServerName()}")
        }
}
