package com.twotheleft.gradle.tigergraph

internal object Configurations {
    /**
     * The name of the runtime configuration for add Classpath dependencies to the plugin.
     *
     */
    const val gsqlRuntime = "gsqlRuntime"

    /**
     * The name of the extension for configuring the runtime behavior of the plugin.
     *
     * @see com.twotheleft.gradle.tigergraph.GsqlPluginExtension
     */
    const val extensionName = "tigergraph"
}
