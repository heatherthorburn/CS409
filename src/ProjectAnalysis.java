import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class ProjectAnalysis {

    private  ArrayList<File> files;
    private ArrayList<ClassData> cd;

    ProjectAnalysis() {
        ProjectFiles pf = new ProjectFiles();
        files = pf.getProjectFiles();
        cd = new ArrayList<>();
    }

    void start() throws IOException {

        CompilationUnit cu;

        for (File f : files) {
            try (FileInputStream in = new FileInputStream(f)) {
                cu = StaticJavaParser.parse(in);
            }
            ProgramAnalysisTool analysis = new ProgramAnalysisTool();
            analysis.visit(cu, null);
            ClassData c = analysis.getClassData();
            HashMap<String, HashSet<String>> namesInMethods = analysis.getNamesInMethods();
            c.addNamesInMethods(namesInMethods);
            cd.add(c);
            LCOM l = new LCOM(c);
        }

        CBO c = new CBO(cd);

        HashMap<String, HashSet<String>> coupling = c.calculateCBO();
        coupling.forEach((X,Y)->{
            System.out.println(X + " CBO: " + coupling.get(X).size());
        });

    }

    private static class ProgramAnalysisTool extends VoidVisitorAdapter {

        private ClassData c;
        private String currentMethod = null;
        private HashMap<String, HashSet<String>> namesInMethods = new HashMap<>();

        public void visit(ClassOrInterfaceDeclaration ci, Object a) {

            c = new ClassData(ci.getName().toString());
            super.visit(ci, a);
        }

        public void visit(FieldDeclaration f, Object a) {
            for (int i = 0; i < f.getVariables().size(); i++) {
                c.addFieldVariable(f.getVariable(i).getNameAsString());
            }
        }

        public void visit(MethodDeclaration m, Object a)
        {
            currentMethod = m.getNameAsString();
            namesInMethods.put(currentMethod, new HashSet<>());
            c.addMethod(m.getNameAsString());
            super.visit(m, a);
        }

        public void visit(VariableDeclarator v, Object a) {
            c.addVariableUsed(new Variable(v.getTypeAsString(), v.getNameAsString()));
            super.visit(v, a);
        }

        public void visit(NameExpr b, Object a) {
            if (currentMethod != null) {
                HashSet<String> names = namesInMethods.get(currentMethod);
                names.add(b.toString());
                namesInMethods.put(currentMethod, names); }
            super.visit(b, a);
        }

        public void visit(MethodCallExpr m, Object a) {
            c.addMethodCall(new MethodCall(m.toString(), m.getScope().toString(), m.removeScope().toString()));
            super.visit(m, a);
        }


        ClassData getClassData() {
            return c;
        }

        HashMap<String, HashSet<String>> getNamesInMethods() {
            return namesInMethods;
        }
    }
}

