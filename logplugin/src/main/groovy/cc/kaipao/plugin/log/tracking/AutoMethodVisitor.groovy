package cc.kaipao.plugin.log.tracking

import cc.kaipao.plugin.log.Logger
import org.objectweb.asm.Handle
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

public class AutoMethodVisitor extends AdviceAdapter {

    String methodName;


    /**
     * Creates a new {@link AdviceAdapter}.
     *
     * @param api
     *            the ASM API version implemented by this visitor. Must be one
     *            of {@link Opcodes#ASM4}, {@link Opcodes#ASM5} or {@link Opcodes#ASM6}.
     * @param mv
     *            the method visitor to which this adapter delegates calls.
     * @param access
     *            the method's access flags (see {@link Opcodes}).
     * @param name
     *            the method's name.
     * @param desc
     *            the method's descriptor (see {@link Type Type}).
     */
    protected AutoMethodVisitor(MethodVisitor mv, int access, String name, String desc) {
        super(Opcodes.ASM4, mv, access, name, desc)
        methodName = name;
    }




    @Override
    protected void onMethodEnter() {
        super.onMethodEnter()
        Logger.info("|| onMethodEnter **> ${methodName}")
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode)
        Logger.info("|| onMethodExit **> ${methodName}")
    }

    @Override
    void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs)
        Logger.info("|| visitInvokeDynamicInsn **> ${methodName} ${name} ${desc}")
    }


}