package com.sensorsdata.analytics.android.plugin

import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

/**
 * 父类实现MethodVisitor接口，主要负责"拜访"方法的信息，用来进行具体的方法字节码操作。
 *
 * JVM指令:
 * load 命令：用于将局部变量表的指定位置的相应类型变量加载到操作数栈顶；
 * store命令：用于将操作数栈顶的相应类型数据保入局部变量表的指定位置；
 * invokevirtual:调用实例方法
 * ireturn: 当前方法返回int
 */
class SensorsAnalyticsDefaultMethodVisitor extends AdviceAdapter {

    SensorsAnalyticsDefaultMethodVisitor(MethodVisitor mv, int access, String name, String desc) {
        super(Opcodes.ASM6, mv, access, name, desc)
    }

    /**
     * 表示 ASM 开始扫描这个方法
     */
    @Override
    void visitCode() {
        super.visitCode()
    }


    /**
     * 访问方法操作指令
     * @param opcode
     * @param owner
     * @param name
     * @param desc
     */
    @Override
    void visitMethodInsn(int opcode, String owner, String name, String desc) {
        super.visitMethodInsn(opcode, owner, name, desc)
    }

    @Override
    void visitAttribute(Attribute attribute) {
        super.visitAttribute(attribute)
    }

    /**
     * 表示方法输出完毕
     */
    @Override
    void visitEnd() {
        super.visitEnd()
    }

    /**
     * 域操作指令，用来加载或者存储对象的Field
     * @param opcode
     * @param owner
     * @param name
     * @param desc
     */
    @Override
    void visitFieldInsn(int opcode, String owner, String name, String desc) {
        super.visitFieldInsn(opcode, owner, name, desc)
    }

    @Override
    void visitIincInsn(int var, int increment) {
        super.visitIincInsn(var, increment)
    }

    /**
     * 访问本地变量类型指令 操作码可以是LOAD,STORE，RET中一种；
     * @param i
     * @param i1
     */
    @Override
    void visitIntInsn(int i, int i1) {
        super.visitIntInsn(i, i1)
    }

    /**
     * 该方法是 visitEnd 之前调用的方法，可以反复调用。用以确定类方法在执行时候的堆栈大小。
     * @param maxStack
     * @param maxLocals
     */
    @Override
    void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack, maxLocals)
    }

    @Override
    void visitVarInsn(int opcode, int var) {
        super.visitVarInsn(opcode, var)
    }

    @Override
    void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label)
    }

    @Override
    void visitLookupSwitchInsn(Label label, int[] ints, Label[] labels) {
        super.visitLookupSwitchInsn(label, ints, labels)
    }

    @Override
    void visitMultiANewArrayInsn(String s, int i) {
        super.visitMultiANewArrayInsn(s, i)
    }

    @Override
    void visitTableSwitchInsn(int i, int i1, Label label, Label[] labels) {
        super.visitTableSwitchInsn(i, i1, label, labels)
    }

    @Override
    void visitTryCatchBlock(Label label, Label label1, Label label2, String s) {
        super.visitTryCatchBlock(label, label1, label2, s)
    }

    @Override
    void visitTypeInsn(int opcode, String s) {
        super.visitTypeInsn(opcode, s)
    }

    @Override
    void visitLocalVariable(String s, String s1, String s2, Label label, Label label1, int i) {
        super.visitLocalVariable(s, s1, s2, label, label1, i)
    }

    @Override
    void visitInsn(int opcode) {
        super.visitInsn(opcode)
    }

    @Override
    AnnotationVisitor visitAnnotation(String s, boolean b) {
        return super.visitAnnotation(s, b)
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter()
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode)
    }
}