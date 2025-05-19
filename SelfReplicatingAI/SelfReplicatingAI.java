/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */

import javax.tools.*;
import java.io.*;
import java.lang.reflect.*;

public class SelfReplicatingAI {

    public static void main(String[] args) {
        try {
            // Define class name and source code
            String className = "Replicator";
            String sourceCode = """
                    /*
                     * Copyright © 2024 Devin B. Royal. All Rights Reserved.
                     */
                    public class Replicator {
                        public static void main(String[] args) {
                            System.out.println("Hello, world!");
                        }
                    }
                    """;

            // Write source code to file
            String fileName = className + ".java";
            File sourceFile = new File(fileName);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
                writer.write(sourceCode);
            }

            // Compile the source code
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                throw new IllegalStateException("Compiler not available. Ensure JDK is installed.");
            }

            int compilationResult = compiler.run(null, null, null, fileName);
            if (compilationResult != 0) {
                throw new RuntimeException("Compilation failed!");
            }

            // Load and execute the compiled class
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new File(".").toURI().toURL()});
            Class<?> loadedClass = Class.forName(className, true, classLoader);
            Method mainMethod = loadedClass.getMethod("main", String[].class);

            System.out.println("Executing Replicator class:");
            mainMethod.invoke(null, (Object) new String[]{});

            // Clean up generated files
            cleanupGeneratedFiles(fileName, className + ".class");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void cleanupGeneratedFiles(String... fileNames) {
        for (String fileName : fileNames) {
            File file = new File(fileName);
            if (file.exists() && !file.delete()) {
                System.err.println("Warning: Failed to delete " + fileName);
            }
        }
    }
}
