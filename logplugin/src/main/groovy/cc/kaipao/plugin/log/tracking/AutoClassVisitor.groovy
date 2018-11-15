package cc.kaipao.plugin.log.tracking

import cc.kaipao.plugin.log.Logger
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Handle
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.TypePath

/**
 * 连遍历，满足两个条件才能修改方法字节码：
 *  1.类匹配
 *  2. 方法匹配
 */
public class AutoClassVisitor extends ClassVisitor {


    private boolean log = true;

    private boolean isMeetClassCondition = false;
    private String mClassName;
    private String[] mInterfaces;

    /**
     * 选择匹配的类
     * @param className
     * @param interfaces
     * @return
     */
    static boolean isMatchingClass(String className, String[] interfaces) {
        boolean isMeetClassCondition = false;
        Logger.info("||\n||------------------------------------------------------------------------------\n||visit class:${className}")

        if (interfaces != null) {
            interfaces.each {
                inf ->
                Logger.info("|| ${className} >> ${inf}")
            }
        }else{
            Logger.info("|| ${className} >> null")
        }


        // 删除android 开头的类，既系统类，以避免不可预测的错误
        if (className.startsWith('android/')) {
            return isMeetClassCondition;
        }

        isMeetClassCondition = true
        return isMeetClassCondition
    }

    static boolean isMatchMethod(String name, String desc) {
        if (name == 'onClick' && desc == '(Landroid/view/View;)V') {
            return true
        }else if (name.startsWith("lambda\$") && desc == '(Landroid/view/View;)V') {
            return true;
        }
        return false
    }

    private static boolean isMatchingInterfaces(String[] interfaces, String interfaceName) {
        boolean isMatch = false;
        interfaces.each {
            String inteface ->
                if (inteface == interfaceName) {
                    isMatch = true;
                }
        }
        return isMatch

    }

    AutoClassVisitor(final ClassVisitor cv) {
        super(Opcodes.ASM4, cv)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        isMeetClassCondition = isMatchingClass(name, interfaces)

        mClassName = name;
        mInterfaces = interfaces

        if (isMeetClassCondition) {

            if (log)
                Logger.logForEach('||* visitStart *', Logger.accCode2String(access), name, signature, superName, interfaces)
        }

    }


    @Override
    void visitInnerClass(String name, String outerName, String innerName, int access) {
        if (isMeetClassCondition) {
            if (log) {
                Logger.logForEach("||* visitInnerClass *", name, innerName, Logger.accCode2String(access))
            }

        }
        super.visitInnerClass(name, outerName, innerName, access)
    }



    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)
        MethodVisitor adapter = null;

        Logger.info("|| visit method:${name} ${desc}")
        // 符合条件，符合修改方法
        if (isMeetClassCondition) {
            if (isMatchMethod(name, desc)) {

                if (log) {
                    Logger.logForEach('||* match condition *', Logger.accCode2String(access), name, desc, signature, exceptions)
                }

                adapter = new AutoMethodVisitor(methodVisitor, access, name, desc) {


                    @Override
                    protected void onMethodEnter() {
                        super.onMethodEnter()

                        // setStartTime
                        methodVisitor.visitLdcInsn(name)
                        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false)
                        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/kaipao/dongjia/log/TimeCache", "setStartTime", "(Ljava/lang/String;J)V", false)


                    }

                    @Override
                    protected void onMethodExit(int opcode) {
                        super.onMethodExit(opcode)

                        // setEndTime
                        methodVisitor.visitLdcInsn(name)
                        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false)
                        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/kaipao/dongjia/log/TimeCache", "setEndTime", "(Ljava/lang/String;J)V", false)

                        //Log
                        methodVisitor.visitLdcInsn("AutoLog")
                        // getCostTime
                        methodVisitor.visitLdcInsn(name)
                        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/kaipao/dongjia/log/TimeCache", "getCostTime", "(Ljava/lang/String;)Ljava/lang/String;", false)
                        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false)


                    }
                }
            }
        }
        if (adapter != null) {
            return adapter
        }

        return methodVisitor

    }

    @Override
    void visitEnd() {
        if (isMeetClassCondition) {
            if (log) {
                Logger.logForEach('||* visitEnd *')
            }
        }
        super.visitEnd()
    }


}