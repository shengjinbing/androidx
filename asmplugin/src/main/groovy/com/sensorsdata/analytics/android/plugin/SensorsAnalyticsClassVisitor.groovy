package com.sensorsdata.analytics.android.plugin

import com.sensorsdata.analytics.android.plugin.utils.SensorsAnalyticsUtils
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Handle
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * 主要负责"拜访"类成员信息。其中包括标记在类上的注解、类的构造方法、类的字段、类的方法、静态代码块等。
 */
class SensorsAnalyticsClassVisitor extends ClassVisitor implements Opcodes {
    private final
    static String SDK_API_CLASS = "com/sensorsdata/analytics/android/sdk/SensorsDataAutoTrackHelper"
    private String[] mInterfaces
    private ClassVisitor classVisitor

    private HashMap<String, SensorsAnalyticsMethodCell> mLambdaMethodCells = new HashMap<>()

    SensorsAnalyticsClassVisitor(final ClassVisitor classVisitor) {
        super(Opcodes.ASM6, classVisitor)
        this.classVisitor = classVisitor
    }

    private
    static void visitMethodWithLoadedParams(MethodVisitor methodVisitor, int opcode, String owner, String methodName, String methodDesc, int start, int count, List<Integer> paramOpcodes) {
        for (int i = start; i < start + count; i++) {
            methodVisitor.visitVarInsn(paramOpcodes[i - start], i)
        }
        methodVisitor.visitMethodInsn(opcode, owner, methodName, methodDesc, false)
    }

    /**
     * 扫描类的时候第一个调用的方法
     * 可以拿到类的详细信息，然后对满足条件的类进行行过滤
     * @param version JDK的版本
     * @param access 类的修饰符，修饰符中以"ACC_"开头的常量，ACC_PUBLIC、ACC_ENUM
     * @param name 代表类的名称。通常以包名+类名来表示类，比如：a.b.c.MyClass,但是在字节码中是以路径的方式表示，即：a/b/c/MyClass.
     *             两种方式都不需要写.class扩展名。
     * @param signature 泛型类型
     * @param superName 当前类继承的父类。
     * @param interfaces 表示当前类实现的接口列表。
     */
    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        mInterfaces = interfaces
    }

    /**
     * 访问内部类信息
     * @param name
     * @param outerName
     * @param innerName
     * @param access
     */
    @Override
    void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access)
    }

    /**
     * 遍历类中成员信息结束
     */
    @Override
    void visitEnd() {
        super.visitEnd()
    }

   /**
     * 拿到需要修改的方法，然后进行修改操作
     * @param access 方法修饰符
     * @param name 方法名称
     * @param desc 方法签名
     * @param signature 泛型
     * @param exceptions 异常
     * @return
     */
    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)

        String nameDesc = name + desc

        methodVisitor = new SensorsAnalyticsDefaultMethodVisitor(methodVisitor, access, name, desc) {
            boolean isSensorsDataTrackViewOnClickAnnotation = false

            @Override
            void visitEnd() {
                super.visitEnd()

                if (mLambdaMethodCells.containsKey(nameDesc)) {
                    mLambdaMethodCells.remove(nameDesc)
                }
            }

            @Override
            void visitInvokeDynamicInsn(String name1, String desc1, Handle bsm, Object... bsmArgs) {
                super.visitInvokeDynamicInsn(name1, desc1, bsm, bsmArgs)

                try {
                    String desc2 = (String) bsmArgs[0]
                    SensorsAnalyticsMethodCell sensorsAnalyticsMethodCell = SensorsAnalyticsHookConfig
                            .LAMBDA_METHODS.get(Type.getReturnType(desc1).getDescriptor() + name1 + desc2)
                    if (sensorsAnalyticsMethodCell != null) {
                        Handle it = (Handle) bsmArgs[1]
                        mLambdaMethodCells.put(it.name + it.desc, sensorsAnalyticsMethodCell)
                    }
                } catch (Exception e) {
                    e.printStackTrace()
                }
            }

            /**
             * 在方法结束的时候处理这样不会影响到原有点击事件的响应速度(重点标记)
             * @param opcode
             */
            @Override
            protected void onMethodExit(int opcode) {
                super.onMethodExit(opcode)
            }

             /**
             * 进入方法插入字节码
             */
            @Override
            protected void onMethodEnter() {
                super.onMethodEnter()

                /**
                 * 在 android.gradle 的 3.2.1 版本中，针对 view 的 setOnClickListener 方法 的 lambda 表达式做特殊处理。
                 */
                SensorsAnalyticsMethodCell lambdaMethodCell = mLambdaMethodCells.get(nameDesc)
                if (lambdaMethodCell != null) {
                    Type[] types = Type.getArgumentTypes(lambdaMethodCell.desc)
                    int length = types.length
                    Type[] lambdaTypes = Type.getArgumentTypes(desc)
                    int paramStart = lambdaTypes.length - length
                    if (paramStart < 0) {
                        return
                    } else {
                        for (int i = 0; i < length; i++) {
                            if (lambdaTypes[paramStart + i].descriptor != types[i].descriptor) {
                                return
                            }
                        }
                    }
                    boolean isStaticMethod = SensorsAnalyticsUtils.isStatic(access)
                    if (!isStaticMethod) {
                        if (lambdaMethodCell.desc == '(Landroid/view/MenuItem;)Z') {
                            methodVisitor.visitVarInsn(ALOAD, 0)
                            methodVisitor.visitVarInsn(ALOAD, getVisitPosition(lambdaTypes, paramStart, isStaticMethod))
                            methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, lambdaMethodCell.agentName, '(Ljava/lang/Object;Landroid/view/MenuItem;)V', false)
                            return
                        }
                    }

                    for (int i = paramStart; i < paramStart + lambdaMethodCell.paramsCount; i++) {
                        methodVisitor.visitVarInsn(lambdaMethodCell.opcodes.get(i - paramStart), getVisitPosition(lambdaTypes, i, isStaticMethod))
                    }
                    methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, lambdaMethodCell.agentName, lambdaMethodCell.agentDesc, false)
                    return
                }

                if (nameDesc == 'onContextItemSelected(Landroid/view/MenuItem;)Z' ||
                        nameDesc == 'onOptionsItemSelected(Landroid/view/MenuItem;)Z') {
                    //带有参数的字节码指令
                    methodVisitor.visitVarInsn(ALOAD, 0)
                    methodVisitor.visitVarInsn(ALOAD, 1)
                    methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Ljava/lang/Object;Landroid/view/MenuItem;)V", false)
                }

                //android：onclick属性绑定，通过自定义的注解实现方法插庄。
                if (isSensorsDataTrackViewOnClickAnnotation) {
                    if (desc == '(Landroid/view/View;)V') {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/view/View;)V", false)
                        return
                    }
                }

                if ((mInterfaces != null && mInterfaces.length > 0)) {
                    if ((mInterfaces.contains('android/view/View$OnClickListener') && nameDesc == 'onClick(Landroid/view/View;)V')) {
                        //实现OnClickListener接口的点击事件
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/view/View;)V", false)
                    } else if (mInterfaces.contains('android/content/DialogInterface$OnClickListener') && nameDesc == 'onClick(Landroid/content/DialogInterface;I)V') {
                        //AlertDialog的点击事件，onClick方法有两个参数一个是DialogInterface另一个是Int
                        //ALOAD是指加载对象类型的参数，ILOAD是指加载基础类型的参数，如int、long、boolean等
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitVarInsn(ILOAD, 2)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/content/DialogInterface;I)V", false)
                    } else if (mInterfaces.contains('android/content/DialogInterface$OnMultiChoiceClickListener') && nameDesc == 'onClick(Landroid/content/DialogInterface;IZ)V') {
                        //支持MenuItem的点击事件
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitVarInsn(ILOAD, 2)
                        methodVisitor.visitVarInsn(ILOAD, 3)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/content/DialogInterface;IZ)V", false)
                    } else if (mInterfaces.contains('android/widget/CompoundButton$OnCheckedChangeListener') && nameDesc == 'onCheckedChanged(Landroid/widget/CompoundButton;Z)V') {
                        //支持CheckBox、SwitchCompat、RadioButton、ToggleButton、RadioGroup的点击事件,他们点击事件设置的listener一样
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitVarInsn(ILOAD, 2)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/widget/CompoundButton;Z)V", false)
                    } else if (mInterfaces.contains('android/widget/RatingBar$OnRatingBarChangeListener') && nameDesc == 'onRatingChanged(Landroid/widget/RatingBar;FZ)V') {
                        //支持RatingBar的点击事件
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/view/View;)V", false)
                    } else if (mInterfaces.contains('android/widget/SeekBar$OnSeekBarChangeListener') && nameDesc == 'onStopTrackingTouch(Landroid/widget/SeekBar;)V') {
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/view/View;)V", false)
                    } else if (mInterfaces.contains('android/widget/AdapterView$OnItemSelectedListener') && nameDesc == 'onItemSelected(Landroid/widget/AdapterView;Landroid/view/View;IJ)V') {
                        //支持Spinner的点击事件
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitVarInsn(ALOAD, 2)
                        methodVisitor.visitVarInsn(ILOAD, 3)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/widget/AdapterView;Landroid/view/View;I)V", false)
                    } else if (mInterfaces.contains('android/widget/TabHost$OnTabChangeListener') && nameDesc == 'onTabChanged(Ljava/lang/String;)V') {
                        //支持TabHost的点击事件
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackTabHost", "(Ljava/lang/String;)V", false)
                    } else if (mInterfaces.contains('android/widget/AdapterView$OnItemClickListener') && nameDesc == 'onItemClick(Landroid/widget/AdapterView;Landroid/view/View;IJ)V') {
                        //支持ListView和GridView的点击事件
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitVarInsn(ALOAD, 2)
                        methodVisitor.visitVarInsn(ILOAD, 3)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackViewOnClick", "(Landroid/widget/AdapterView;Landroid/view/View;I)V", false)
                    } else if (mInterfaces.contains('android/widget/ExpandableListView$OnGroupClickListener') && nameDesc == 'onGroupClick(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z') {
                        //支持ExpandableListView,OnGroup的点击事件
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitVarInsn(ALOAD, 2)
                        methodVisitor.visitVarInsn(ILOAD, 3)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackExpandableListViewGroupOnClick", "(Landroid/widget/ExpandableListView;Landroid/view/View;I)V", false)
                    } else if (mInterfaces.contains('android/widget/ExpandableListView$OnChildClickListener') && nameDesc == 'onChildClick(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z') {
                        //支持ExpandableListView,OnChild的点击事件
                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitVarInsn(ALOAD, 2)
                        methodVisitor.visitVarInsn(ILOAD, 3)
                        methodVisitor.visitVarInsn(ILOAD, 4)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS, "trackExpandableListViewChildOnClick", "(Landroid/widget/ExpandableListView;Landroid/view/View;II)V", false)
                    }
                }
            }

            /**
             * 可以在这里通过注解的方式操作字节码
             * @param des
             * @param visible
             * @return
             */
            @Override
            AnnotationVisitor visitAnnotation(String s, boolean b) {
                if (s == 'Lcom/sensorsdata/analytics/android/sdk/SensorsDataTrackViewOnClick;') {
                    isSensorsDataTrackViewOnClickAnnotation = true
                }
                return super.visitAnnotation(s, b)
            }
        }
        return methodVisitor
    }

    /**
     * 获取方法参数下标为 index 的对应 ASM index
     * @param types 方法参数类型数组
     * @param index 方法中参数下标，从 0 开始
     * @param isStaticMethod 该方法是否为静态方法
     * @return 访问该方法的 index 位参数的 ASM index
     */
    int getVisitPosition(Type[] types, int index, boolean isStaticMethod) {
        if (types == null || index < 0 || index >= types.length) {
            throw new Error("getVisitPosition error")
        }
        if (index == 0) {
            return isStaticMethod ? 0 : 1
        } else {
            return getVisitPosition(types, index - 1, isStaticMethod) + types[index - 1].getSize()
        }
    }
}