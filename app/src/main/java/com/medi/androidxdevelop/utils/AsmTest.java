package com.medi.androidxdevelop.utils;

/**
 * Created by lixiang on 2020/12/30
 * Describe:
 * https://zhuanlan.zhihu.com/p/94498015?utm_source=wechat_timeline
 * 1.暂时不支持koltin
 * 2.使用ASM插件ASM ByteCode Outline查看字节码
 */
public class AsmTest {
    private int num1 = 10;
    public int add(int a,int b){
        return a+b+num1;
    }
}

       /* java源码
        public int add(int a,int b) {
           return a+b+num1;
         }

        class字节码
        public int add(int, int);
        descriptor: (II)I
        flags: ACC_PUBLIC
        Code:
        stack=2, locals=3, args_size=3
        0: iload_1
        1: iload_2
        2: iadd
        3: aload_0
        4: getfield      #2                  // Field num1:I
        7: iadd
        8: ireturn
        LineNumberTable:
        line 14: 0
        LocalVariableTable:
        Start  Length  Slot  Name   Signature
        0       9     0  this   Lcom/wuba/asmdemo/Test;
        0       9     1     a   I
        0       9     2     b   I

        ASM对应的API
        mv = cw.visitMethod(ACC_PUBLIC, "add", "(II)I", null, null);
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 1);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(IADD);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "com/wuba/asmdemo/Test", "num1", "I");
        mv.visitInsn(IADD);
        mv.visitInsn(IRETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "Lcom/wuba/asmdemo/Test;", null, l0, l1, 0);
        mv.visitLocalVariable("a", "I", null, l0, l1, 1);
        mv.visitLocalVariable("b", "I", null, l0, l1, 2);
        mv.visitMaxs(2, 3);
        mv.visitEnd();*/




