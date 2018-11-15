package cc.kaipao.plugin.log.tracking

import cc.kaipao.plugin.log.utils.ChoiceUtil
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter


public class AutoModify {


    static byte[] modifyClassed(String className, byte[] srcByteCode) {
        byte[] classBytesCode = null;

        // 修改字节码

        try {
            classBytesCode = modifyClass(srcByteCode)
            return classBytesCode
        } catch (Exception e) {
            e.printStackTrace()
        }

        if (classBytesCode == null) {
            classBytesCode = srcByteCode;
        }

        return classBytesCode
    }

    /**
     * 修改类字节码
     * @param srcClass
     * @return
     * @throws IOException
     */
    private static byte[] modifyClass(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor adapter = new AutoClassVisitor(classWriter)
        ClassReader classReader = new ClassReader(srcClass)
        classReader.accept(adapter, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()

    }
}