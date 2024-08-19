package xyz.ieki.jaragent;

import java.lang.instrument.Instrumentation;

public class AgentMain {


    public static void premain(String agentArg, Instrumentation inst) {
        try {
            System.out.println("[JarAgent] Premain Agent");

            PatchTransformer patchTransformer = new PatchTransformer(inst);
            inst.addTransformer(patchTransformer, true);
        }catch (Throwable e){
            e.printStackTrace();
        }
    }


    public static void agentmain(String agentArg, Instrumentation inst) {
        try {
            System.out.println("[JarAgent] Attach Agent");
            PatchTransformer patchTransformer = new PatchTransformer(inst);
            inst.addTransformer(patchTransformer, true);
            patchTransformer.retransform();
        }catch (Throwable e){
            System.out.println("[JarAgent] Error "+ e.getMessage());
            e.printStackTrace();
        }
    }
}
