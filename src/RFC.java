import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class RFC {

    private static ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<>();
    private static ArrayList<Integer> numMethodsAndCalls = new ArrayList<>();
    private static ArrayList<String> methodsAndCalls = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        ProjectFiles pf = new ProjectFiles();
        ArrayList<File> files = pf.getProjectFiles();
        CompilationUnit cu;

        for (File f: files) {
            System.out.println("-------NEW FILE: " + f.getName() +"--------");
            FileInputStream in = new FileInputStream(f);
            try {
                cu = StaticJavaParser.parse(in);
            } finally {
                in.close();
            }

            RFCVisitor run = new RFCVisitor();
            run.visit(cu, null);
        }
        calculateRFC();

    }

    private static void calculateRFC() {
        System.out.println();
        System.out.println("-------RFC Values-------");
        for (int i=0;i<classes.size();i++) {
            System.out.println("Class Name: "+classes.get(i).getName());
            System.out.println("RFC Value: "+numMethodsAndCalls.get(i));
        }

    }

    private static class RFCVisitor extends VoidVisitorAdapter {

        /*
         * Once a class declaration is found, all of the methods in the class are added to a list and those
         * methods are added to the RFC value of the class
         *
         * @param ci
         * @param a
         */
        public void visit(ClassOrInterfaceDeclaration ci, Object a) {
            String methodAsString;
            classes.add(ci);
            List<MethodDeclaration> methodsInClass = ci.getMethods();
            for (MethodDeclaration md : methodsInClass) {
                methodAsString = md.getDeclarationAsString();
                methodsAndCalls.add(methodAsString);
            }
            int numMethodsInClass = methodsInClass.toArray().length;
            numMethodsAndCalls.add(numMethodsInClass);
            super.visit(ci, a);
        }

         /*
         * When a method is called, the visit method checks that it is not already counted towards the RFC and if it is
         * not then the RFC value is incremented
         *
         * @param mc
         * @param a
         */
        public void visit (MethodCallExpr mc, Object a) {
            String parsedCall = null;
            if (mc.toString().contains(".")) {
                parsedCall = mc.toString().substring(mc.toString().indexOf("."));
            }
            if (!methodsAndCalls.contains(parsedCall) && !methodsAndCalls.contains(mc.toString())) {
                methodsAndCalls.add(mc.toString());
                int lastInt = numMethodsAndCalls.get(numMethodsAndCalls.size()-1);
                lastInt++;
                numMethodsAndCalls.remove(numMethodsAndCalls.size()-1);
                numMethodsAndCalls.add(lastInt);
            }
            super.visit(mc, a);

        }

    }
}