package com.gideon.remotedebug;

import java.lang.reflect.Method;

/**
 * JavaClass执行工具
 *
 * @author zzm 
 * @author Gideon
 */
public class JavaClassExecuter {

    /**
     * 执行外部传过来的代表一个Java类的Byte数组<br>
     * 将输入类的byte数组中代表java.lang.System的CONSTANT_Utf8_info常量修改为劫持后的HackSystem类
     * 执行方法为该类的static main(String[] args)方法，输出结果为该类向System.out/err输出的信息
     * @param classByte 代表一个Java类的Byte数组
     * @return 执行结果
     */
    public static String execute(byte[] classByte) {
        return execute(classByte, null);
    }
    
    /**
     * 执行外部传过来的代表一个Java类的Byte数组<br>
     * 将输入类的byte数组中代表java.lang.System的CONSTANT_Utf8_info常量修改为劫持后的HackSystem类
     * 执行方法为该类的static main(String[] args)方法，输出结果为该类向System.out/err输出的信息
     * @param classByte 代表一个Java类的Byte数组
     * @param parent 代表要设置的ParentClassLoader,以防Web容器的双亲委派机制导致Class异常加载
     * @return 执行结果
     */
    public static String execute(byte[] classByte, ClassLoader parent) {
        HackSystem.clearBuffer();
        ClassModifier cm = new ClassModifier(classByte);
        byte[] modiBytes = cm.modifyUTF8Constant("java/lang/System", "com/gideon/remotedebug/HackSystem");
        HotSwapClassLoader loader;
        if (parent == null) {
            loader = new HotSwapClassLoader();
        }else {
            loader = new HotSwapClassLoader(parent);
        }
        Class clazz = loader.loadByte(modiBytes);
        try {
            Method method = clazz.getMethod("main", new Class[] { String[].class });
            method.invoke(null, new String[] { null });
        } catch (Throwable e) {
            e.printStackTrace(HackSystem.out);
        }
        return HackSystem.getBufferString();
    }
}

