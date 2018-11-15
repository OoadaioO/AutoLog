package cc.kaipao.plugin.log

import cc.kaipao.plugin.log.tracking.AutoModify
import cc.kaipao.plugin.log.utils.TextUtil
import com.android.annotations.NonNull
import com.android.annotations.Nullable
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils

class LogTransform extends Transform {

    @Override
    String getName() {
        return "LogPlugin"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    public void transform(
            @NonNull Context context,
            @NonNull Collection<TransformInput> inputs,
            @NonNull Collection<TransformInput> referencedInputs,
            @Nullable TransformOutputProvider outputProvider,
            boolean isIncremental) throws IOException, TransformException, InterruptedException {
        //开始计算消耗的时间
        Logger.info("||=======================================================================================================")
        Logger.info("||                                                 开始计时                                               ")
        Logger.info("||=======================================================================================================")
        def startTime = System.currentTimeMillis()

        if (Logger.isDebug()) {
            printlnJarAndDir(inputs)
        }

        /**遍历输入文件*/
        inputs.each { TransformInput input ->
            /**
             * 遍历jar
             */
            input.jarInputs.each { JarInput jarInput ->

                // 获取输出文件名
                String destName = jarInput.file.name
                /** 截取文件路径的md5值重命名输出文件,因为可能同名,会覆盖*/
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8)
                if (destName.endsWith(".jar")) {
                    // 去掉后缀
                    destName = destName.substring(0, destName.length() - 4)
                }
                File dest = outputProvider.getContentLocation("${destName}_${hexName}", jarInput.contentTypes, jarInput.scopes, Format.JAR)
                Logger.info("||-->开始遍历特定jar ${dest.absolutePath}")

                // todo 修改jar文件
                def modifiedJar = null;

                // 输出文件拷贝至目标目录
                if (modifiedJar == null) {
                    modifiedJar = jarInput.file
                }
                FileUtils.copyFile(modifiedJar, dest)

            }
            /**
             * 遍历目录
             */
            input.directoryInputs.each { DirectoryInput directoryInput ->

                // 获取输出目录
                // 修改代码
                // 记录修改过的代码
                // 文件拷贝至输出目录
                // 修改文件替换
                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

                Logger.info("||-->开始遍历特定目录 ${dest.absolutePath}")
                File inputDir = directoryInput.file
                if (inputDir) {
                    HashMap<String, File> modifyMap = new HashMap<>()

                    inputDir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                        File classFile ->
                            // 修改字节码
                            File modified =  modifyClassFile(inputDir, classFile, context.getTemporaryDir())
                            if (modified != null) {
                                // 存储修改文件 Key:/xx.class Value:File
                                def keyClassName = classFile.absolutePath.replace(inputDir.absolutePath, "")
                                modifyMap.put(keyClassName, modified)
                            }

                    }

                    FileUtils.copyDirectory(directoryInput.file, dest)


                    modifyMap.entrySet().each {
                        Map.Entry<String, File> en ->
                            File target = new File(dest.absolutePath + en.getKey())
                            if (target.exists()) {
                                target.delete()
                            }
                            FileUtils.copyFile(en.getValue(), target)
                            en.getValue().delete()
                    }


                }

                Logger.info("||-->结束遍历特定目录  ${dest.absolutePath}")
            }
        }

        //计算耗时
        def cost = (System.currentTimeMillis() - startTime) / 1000
        Logger.info("||=======================================================================================================")
        Logger.info("||                                       计时结束:费时${cost}秒                                           ")
        Logger.info("||=======================================================================================================")
    }

    /**
     * 修改字节码保存为临时文件
     * @param dir 输入目录
     * @param classFile 类文件
     * @param tempDir 临时文件目录
     * @return  修改后文件
     */
    private static File modifyClassFile(File dir, File classFile, File tempDir) {
        File modified = null;

        try {
            // 获取类名
            String className = TextUtil.path2ClassName(classFile.absolutePath.replace(dir.absolutePath + File.separator, ""))

            // 读取字节码
            byte[] sourceClassBytes = IOUtils.toByteArray(new FileInputStream(classFile))

            // 修改字节码
            byte[] modifiedClassBytes = AutoModify.modifyClassed(className, sourceClassBytes)
            if (modifiedClassBytes) {

                // 保存修改后字节码，到临时文件目录
                modified = new File(tempDir, className + ".class")
                if (modified.exists()) {
                    modified.delete()
                }

                File parent = modified.getParentFile()
                if (parent != null) {
                    if (!parent.exists()) {
                        parent.mkdirs()
                    }
                }
                modified.createNewFile()
                new FileOutputStream(modified).write(modifiedClassBytes)
            }

        } catch (Exception e) {
            e.printStackTrace()
        }
        return modified

    }


    /**
     * 包括两种数据:jar包和class目录，打印出来用于调试
     */
    private static void printlnJarAndDir(Collection<TransformInput> inputs) {

        def classPaths = []
        String buildTypes
        String productFlavors
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                classPaths.add(directoryInput.file.absolutePath)
                buildTypes = directoryInput.file.name
                productFlavors = directoryInput.file.parentFile.name
                Logger.info("||项目class目录：${directoryInput.file.absolutePath}")
            }
            input.jarInputs.each { JarInput jarInput ->
                classPaths.add(jarInput.file.absolutePath)
                Logger.info("||项目jar包：${jarInput.file.absolutePath}")
            }
        }
    }
}