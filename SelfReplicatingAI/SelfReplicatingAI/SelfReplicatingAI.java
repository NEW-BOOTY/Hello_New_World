/**
 * Self-replicating AI example.
 * Copyright (c) 2024 Devin B. Royal
 */
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.lang.reflect.Method;

public class SelfReplicatingAI {
    public static void main(String[] args) throws Exception {
        String code = "public class Replicator { public static void main(String[] args) { System.out.println(\"Hello, world!\"); } }";

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, code);

        Class<?> replicatorClass = Class.forName("Replicator");
        Method mainMethod = replicatorClass.getMethod("main", String[].class);
        mainMethod.invoke(null, (Object) null);
    }
}
