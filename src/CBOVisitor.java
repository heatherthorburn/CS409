import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;


public class CBOVisitor {

    public static void main(String[] args) throws Exception {

        ProjectFiles pf = new ProjectFiles();
        ArrayList<File> files = pf.getProjectFiles();
        ArrayList<CBOData> cboData = new ArrayList<>();

        for (File f : files) {
            FileInputStream in = new FileInputStream(f);
            CompilationUnit cu;
            try {
                cu = StaticJavaParser.parse(in);
            } finally {
                in.close();
            }
            ClassVisitor classVisitor = new ClassVisitor();
            classVisitor.visit(cu, null);
            ArrayList<ClassOrInterfaceDeclaration> classes = classVisitor.getClasses();
            for (ClassOrInterfaceDeclaration c : classes) {
                MethodVisitor methodVisitor = new MethodVisitor();
                methodVisitor.visit(c, null);
                cboData.add(new CBOData(c.getName(), methodVisitor.getMethodDeclarations(), methodVisitor.getMethodCalls()));
            }
        }

        for (CBOData data : cboData) {
            ArrayList<SimpleName> methodCalls = data.getMethodsCalled();
            for (SimpleName m : methodCalls) {
                for (CBOData comparison : cboData) {
                    ArrayList<SimpleName> classesDeclared = comparison.getMethodsDeclared();
                    if (classesDeclared.contains(m) && !comparison.getClassName().equals(data.getClassName())) {
                        comparison.addCoupling(data.getClassName());
                        data.addCoupling(comparison.getClassName());
                    }
                }
            }
        }

        for (CBOData data : cboData) {
            System.out.println("Class: " + data.getClassName());
            System.out.println("CBO: " + data.getCBO());
            data.printCouplingClasses();
        }
    }





    /**
     * Simple visitor implementation for extracting class information
     * along with fields and local variables
     */
    private static class ClassVisitor extends VoidVisitorAdapter {

        ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<>();

        public void visit(ClassOrInterfaceDeclaration n, Object arg) {
            classes.add(n);
            super.visit(n, arg);
        }

        public ArrayList<ClassOrInterfaceDeclaration> getClasses() {
            return classes;
        }
    }


    private static class MethodVisitor extends VoidVisitorAdapter {

        ArrayList<SimpleName> methodCalls = new ArrayList<>();
        ArrayList<SimpleName> methodDeclarations = new ArrayList<>();

        public void visit(MethodDeclaration m, Object arg) {
            methodDeclarations.add(m.getName());
            super.visit(m, arg);
        }

        public void visit(MethodCallExpr m, Object arg) {
            methodCalls.add(m.getName());
            super.visit(m, arg);
        }

        public void visit(FieldAccessExpr f, Object arg) {
            //System.out.println("Field Access: " + f.getScope());
        }

        public ArrayList<SimpleName> getMethodCalls() {
            return methodCalls;
        }

        public ArrayList<SimpleName> getMethodDeclarations() {
            return methodDeclarations;
        }

    }
}
