package cc.kaipao.plugin.log

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class LogPlugin implements Plugin<Project>{


    @Override
    void apply(Project project) {

        def android = project.extensions.getByType(AppExtension)
        registerTransform(android)

    }

    def static registerTransform(BaseExtension extension) {
        LogTransform transform = new LogTransform()
        extension.registerTransform(transform)

    }
}