import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;


public class LCOMVisitor {

    public static void main(String[] args) throws Exception {

        ProjectFiles pf = new ProjectFiles();
        ArrayList<File> files = pf.getProjectFiles();

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
                FieldVisitor fieldVisistor = new FieldVisitor();
                fieldVisistor.visit(c, null);
                HashSet<SimpleName> fields = fieldVisistor.getFields();
                HashMap<MethodDeclaration, HashSet<SimpleName>> variablesInMethods = fieldVisistor.namesInMethods;
                int lcom = calculateLCOM(fields, variablesInMethods);
                System.out.println(c.getName());
                System.out.println(lcom);
            }
        }
    }

    private static int calculateLCOM(HashSet<SimpleName> f, HashMap<MethodDeclaration, HashSet<SimpleName>> variablesInMethods) {

        ArrayList<MethodDeclaration> visited = new ArrayList<>();
        AtomicInteger lcom = new AtomicInteger();

        variablesInMethods.forEach((X,Y)->{
            System.out.println("method 1: " + X.getName());
            HashSet fields = (HashSet) f.clone();
            System.out.println("fields: " + fields);
            HashSet set1 = (HashSet) Y.clone();
            System.out.println("set 1: " + set1);
            set1.retainAll(fields);
            System.out.println("after getting fields: " + set1);
            variablesInMethods.forEach((A,B)->{
                if (!X.equals(A) && !visited.contains(A)) {
                    HashSet set2 = (HashSet) B.clone();
                    System.out.println("set 2:" + set2);
                    set2.retainAll(set1);
                    System.out.println("after checking union: " + set2);
                    if (set2.size() == 0) {
                        lcom.set(lcom.get() + 1);
                    } else {
                        lcom.getAndDecrement();
                    }
                }
            });
            visited.add(X);
        });
        if (lcom.get() < 0) {
            lcom.set(0);
        }
        return lcom.get();
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


    private static class FieldVisitor extends VoidVisitorAdapter {

        HashSet<SimpleName> fields = new HashSet<>();
        HashMap<MethodDeclaration, HashSet<SimpleName>> namesInMethods = new HashMap<>();

        public void visit(FieldDeclaration f, Object arg) {
            for (int i = 0; i < f.getVariables().size(); i++) {
                fields.add(f.getVariable(i).getName());
            }
        }

        public void visit(MethodDeclaration m, Object arg) {
            MethodNamesVisitor methodNamesVisitor = new MethodNamesVisitor();
            methodNamesVisitor.visit(m, null);
            namesInMethods.put(m, methodNamesVisitor.getNamesInMethods());
        }

        public HashSet<SimpleName> getFields() {
            return fields;
        }

        public HashMap<MethodDeclaration, HashSet<SimpleName>> getMethods() {
            return namesInMethods;
        }
    }

    private static class MethodNamesVisitor extends VoidVisitorAdapter {

        HashSet<SimpleName> namesInMethods = new HashSet<>();

        public void visit(SimpleName s, Object arg) {
            namesInMethods.add(s);
        }

        public HashSet<SimpleName> getNamesInMethods() {
            return namesInMethods;
        }

        }
    }
