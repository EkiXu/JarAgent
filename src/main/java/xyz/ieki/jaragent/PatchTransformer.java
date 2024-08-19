package xyz.ieki.jaragent;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PatchTransformer implements ClassFileTransformer {

    private Instrumentation inst;
    private Map<String,Class<PatchTemplate>> patches;


    public PatchTransformer(Instrumentation inst){
        this.inst = inst;
        this.patches = new HashMap<>();
        // 指定要扫描的包名
        String basePackage = "xyz.ieki.jaragent.patch";

        // 创建Reflections配置
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(basePackage))
                .setScanners(new SubTypesScanner(false), Scanners.TypesAnnotated));

        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(PatchAnnotation.class);
        for (Class<?> clazz : annotatedClasses) {
            PatchAnnotation annotation = clazz.getAnnotation(PatchAnnotation.class);
            if (annotation != null) {
                String value = annotation.value();
                try {
                    Class<PatchTemplate> patchClz = (Class<PatchTemplate>) clazz;
                    patches.put(value,patchClz);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        //
        String realClassName = className.replace("/", ".");
        Class<PatchTemplate> patchClz = patches.get(realClassName);

        if (patchClz != null) {
            try {
                System.out.println("[JarAgent] Transforming " + className);
                PatchTemplate patch = patchClz.getConstructor(ClassLoader.class).newInstance(loader);
                return patch.patch(classfileBuffer);
            } catch (Exception e) {
                System.err.println("[JarAgent] Error Patching" + realClassName + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            return classfileBuffer;
        }


        return classfileBuffer;
    }


    public void retransform() {
        Class<?>[] loadedClasses = inst.getAllLoadedClasses();
        ArrayList<Class<?>> patchClasses = new ArrayList<>();
        for (Class<?> clazz : loadedClasses) {
            //System.out.println(clazz.getName());
            if (patches.containsKey(clazz.getName())) {
                patchClasses.add(clazz);
            }
        }

        if(patches.isEmpty()){
            System.out.println("No patches");
            return;
        }

        try {
            System.out.println("trying to retransform "+patchClasses.size() +" Classes");
            inst.retransformClasses(patchClasses.toArray(new Class[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
