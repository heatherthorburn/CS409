import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnalysisTool {

    private static ArrayList<File> files = new ArrayList<>();
    private static ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<>();
    private static ArrayList<MethodDeclaration> methods = new ArrayList<>();
    private static HashMap<ClassOrInterfaceDeclaration, Integer> simpleComplexities = new HashMap<>();

    public static void main(String[] args) throws Exception {

        getFiles();
        CompilationUnit cu;

        for (File f: files) {
            System.out.println("-------NEW FILE: " + f.getName() +"--------");
            FileInputStream in = new FileInputStream(f);
            try {
                cu = StaticJavaParser.parse(in);
            } finally {
                in.close();
            }

            new ProgramAnalysisTool().visit(cu, null);
        }

        calculateSimpleComplexity();
        calculateComplexComplexity();

    }

    private static void calculateSimpleComplexity() {
        System.out.println("---------SIMPLE CLASS COMPLEXITY----------");
        for (ClassOrInterfaceDeclaration c : classes) {
            simpleComplexities.put(c, c.getMethods().size());
        }
        simpleComplexities.forEach((c, complexity) -> {
            System.out.println("Class Name: " + c.getName());
            System.out.println("Simple Complexity: " + complexity);
        });
    }

    private static void calculateComplexComplexity() {
        for (ClassOrInterfaceDeclaration c: classes) {
            List<MethodDeclaration> methodList = c.getMethods();
            int totalComplexity = 0;
            for (MethodDeclaration m : methodList) {
                int complexity = 1;
            }


        }
    }

    private static void getFiles() {
       File[] folderFiles;
       File in = new File("/home/heather/409groupassignment/Animal");
       folderFiles = in.listFiles();
       for (File f : folderFiles) {
            if (f.getName().endsWith(".java")) {
                files.add(f);
            }
        }
    }


    private static class ProgramAnalysisTool extends VoidVisitorAdapter {

        public void visit(ClassOrInterfaceDeclaration ci, Object a) {
            classes.add(ci);
            super.visit(ci, a);
        }

        public void visit(MethodDeclaration m, Object a) {
            methods.add(m);
            super.visit(m, a);
        }
    }

}
