package xyz.ieki.jaragent.patch;

import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import xyz.ieki.jaragent.PatchAnnotation;
import xyz.ieki.jaragent.PatchTemplate;

// patch the class xyz.eki.vuljava.demos.web.VulController
@PatchAnnotation("xyz.eki.vuljava.demos.web.VulController")
public class ExamplePatch extends PatchTemplate{

    public ExamplePatch(ClassLoader loader) {
        super(loader);
    }

    void patch1() throws Exception {
        CtMethod ctMethod =  ctClass.getDeclaredMethod("test");
        ctMethod.setBody("{\n" +
                "        return \"Patched\";\n" +
                "    }");
    }

    // add a new method and add annotation to it
    void patch2() throws Exception {
        CtMethod newMethod = CtMethod.make(
                "public String newRoute() { return \"newRoute\";}"
                , ctClass);

        ConstPool constPool = ctClass.getClassFile().getConstPool();
        // 创建注解属性
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation requestMapping = new Annotation("org.springframework.web.bind.annotation.RequestMapping", constPool);
        Annotation responseBody = new Annotation("org.springframework.web.bind.annotation.ResponseBody", constPool);


        // 设置注解的值为 "/hello"
        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(constPool);
        arrayMemberValue.setValue(new StringMemberValue[]{new StringMemberValue("/new", constPool)});
        requestMapping.addMemberValue("value", arrayMemberValue);

        // 将注解添加到方法上
        attr.addAnnotation(requestMapping);
        attr.addAnnotation(responseBody);
        newMethod.getMethodInfo().addAttribute(attr);

        ctClass.addMethod(newMethod);
    }

    @Override
    public void patch_steps() throws Exception {
        patch1();
        patch2();
    }
}
