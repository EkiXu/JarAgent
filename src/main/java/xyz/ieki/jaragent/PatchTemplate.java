package xyz.ieki.jaragent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

import java.io.ByteArrayInputStream;

public abstract class PatchTemplate {
    protected ClassPool classPool;
    protected CtClass ctClass = null;

    public PatchTemplate(ClassLoader loader){
        classPool = new ClassPool();
        classPool.appendSystemPath();
        if (loader != null) {
            classPool.appendClassPath(new LoaderClassPath(loader));
        }
    }

    public abstract void patch_steps() throws Exception;

    public byte[] patch(byte[] classfileBuffer)  {
        try{
            // 构造CtClass
            ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

            // 执行patch方法
            patch_steps();

            return ctClass.toBytecode();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (ctClass != null) {
                ctClass.detach();
            }
        }
        return classfileBuffer;
    }
}
