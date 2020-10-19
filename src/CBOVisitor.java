import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class CBOVisitor {

    /**
     *
     * @param args
     * @throws Exception
     *
     * Visitor called to get classes first, then another visitor to get fields and variables
     * for respective classes.
     *
     */

    public static void main(String[] args) throws Exception {

        ProjectFiles pf = new ProjectFiles();
        ArrayList<File> files = pf.getProjectFiles();
        ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<>();

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
            ArrayList<ClassOrInterfaceDeclaration> fileClasses = classVisitor.getClasses();
            for (ClassOrInterfaceDeclaration c : fileClasses) {
                classes.add(c);
            }
            }
        calculateCBO(classes);
    }

    /**
     *
     * @param classes
     *
     * Method goes through the types of variables and fields used in the respective classes.
     * Any variables/fields declared are assumed to be used, and if the field/variable type is a class
     * in the directory, a coupling is added to a HashSet for the pair of classes.
     */

    private static void calculateCBO(ArrayList<ClassOrInterfaceDeclaration> classes) {

        HashSet<String> types = new HashSet<>();
        HashMap<String, HashSet<String>> couplings = new HashMap<>();

        for (ClassOrInterfaceDeclaration c : classes) {
            types.add(c.getNameAsString());
            couplings.put(c.getNameAsString(), new HashSet<>());
        }

        for (ClassOrInterfaceDeclaration c : classes) {
            HashSet classesTypes = (HashSet) types.clone();
            VariableVisitor variableVisitor = new VariableVisitor();
            variableVisitor.visit(c, null);
            HashSet<String> variablesUsed = variableVisitor.getVariablesUsed();
            variablesUsed.retainAll(classesTypes);
            for (String v : variablesUsed) {
                if (!v.equals(c.getNameAsString())) {
                    HashSet<String> temp = couplings.get(c.getNameAsString());
                    temp.add(v);
                    couplings.put(c.getNameAsString(), temp);
                    temp = couplings.get(v);
                    temp.add(c.getNameAsString());
                    couplings.put(v, temp);
                }
            }
        }

        couplings.forEach((X, Y) -> {
            System.out.println("Class: " + X);
            System.out.println("CBO: " + Y.size());
        });
    }



    /**
     * Visitor that extracts all class or interface declarations from the file directory.
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
     *  Visitor that extract all the variables and field declarations for the given class.
     */

    private static class VariableVisitor extends VoidVisitorAdapter {

        HashSet<String> variablesUsed = new HashSet<>();
        
        public void visit(FieldDeclaration f, Object arg) {
            variablesUsed.add(f.getElementType().asString());
        }

        public void visit(VariableDeclarator v, Object arg) {
            variablesUsed.add(v.getTypeAsString());
        }

        public HashSet<String> getVariablesUsed() {
            return variablesUsed;
        }
    }
}

