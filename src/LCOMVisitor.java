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

    /**
     *
     * @param args
     * @throws Exception
     *
     * Visitor called to get classes, then field variables, then all simple names in the
     * class methods. These simple names are compared to the fields before calculating the unions of the methods to get LCOM.
     *
     */

    public static void main(String[] args) throws Exception {

        ProjectFiles pf = new ProjectFiles();
        ArrayList<File> files = pf.getProjectFiles();
        System.out.println("-----LCOM Values-----");

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
                System.out.println("Class: " + c.getName());
                System.out.println("LCOM: " + lcom);
            }
        }
    }

    /**
     * Visitor to get classes in the file directory
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

    /**
     * Visitor to get field declarations
     */


    private static class FieldVisitor extends VoidVisitorAdapter {

        HashSet<SimpleName> fields = new HashSet<>();
        HashMap<MethodDeclaration, HashSet<SimpleName>> namesInMethods = new HashMap<>();

        public void visit(FieldDeclaration f, Object arg) {
            for (int i = 0; i < f.getVariables().size(); i++) {
                fields.add(f.getVariable(i).getName());
            }
            super.visit(f, arg);
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


    /**
     *  Visitor to get classes method names and simple names within the the method
     */

    private static class MethodNamesVisitor extends VoidVisitorAdapter {

        HashSet<SimpleName> namesInMethods = new HashSet<>();

        public void visit(SimpleName s, Object arg) {
            namesInMethods.add(s);
            super.visit(s, arg);
        }

        public HashSet<SimpleName> getNamesInMethods() {
            return namesInMethods;
        }

    }

    /**
     *
     * @param f
     * @param variablesInMethods
     * @return
     *
     * Calculates the LCOM by getting the unions of all methods, then comparing the simple names
     * used in the methods with the field declarations in the class.
     * If there is a non empty set after the union calculation then the LCOM is decremented, otherwise
     * it is incremented.
     * If LCOM is negative it is set to 0, otherwise LCOM = LCOM.
     */

    private static int calculateLCOM(HashSet<SimpleName> f, HashMap<MethodDeclaration, HashSet<SimpleName>> variablesInMethods) {

        ArrayList<MethodDeclaration> visited = new ArrayList<>();
        AtomicInteger lcom = new AtomicInteger();

        variablesInMethods.forEach((X,Y)->{
            HashSet fields = (HashSet) f.clone();
            HashSet set1 = (HashSet) Y.clone();
            set1.retainAll(fields);
            variablesInMethods.forEach((A,B)->{
                if (!X.equals(A) && !visited.contains(A)) {
                    HashSet set2 = (HashSet) B.clone();
                    set2.retainAll(set1);
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
    }
