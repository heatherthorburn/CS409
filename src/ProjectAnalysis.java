import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ProjectAnalysis {

    private static ArrayList<File> files;
    private static ProjectFiles pf;
    private static  ArrayList<ClassData> cd;

    public ProjectAnalysis() {
        pf = new ProjectFiles();
        files = pf.getProjectFiles();
        cd = new ArrayList<>();
    }

    public void start() throws IOException {

        CompilationUnit cu;

        for (File f : files) {
            FileInputStream in = new FileInputStream(f);
            try {
                cu = StaticJavaParser.parse(in);
            } finally {
                in.close();
            }
            new ProgramAnalysisTool().visit(cu, null);
        }

        // testing RBO, will tidy up.

        CBO c = new CBO(cd);
        HashMap<String, HashSet<String>> coupling = c.calculateCBO();

        coupling.forEach((X,Y)->{
            System.out.println(X + " CBO: " + coupling.get(X).size() + " " + coupling.get(X).toString());
        });
    }

    private static class ProgramAnalysisTool extends VoidVisitorAdapter {

        private ClassData c;

        public void visit(ClassOrInterfaceDeclaration ci, Object a) {
            c = new ClassData(ci.getName().toString());
            cd.add(c);
            super.visit(ci, a);
        }

        public void visit(MethodDeclaration m, Object a) {
            c.addMethod(m.getNameAsString());
            super.visit(m, a);
        }

        public void visit(FieldDeclaration f, Object a) {
            c.addField(f.toString());
            super.visit(f, a);
        }

        public void visit(VariableDeclarator v, Object a) {
            c.addVariableUsed(new Variable(v.getTypeAsString(), v.getNameAsString()));
            super.visit(v, a);
        }

        public void visit(MethodCallExpr m, Object a) {
            System.out.println(m.toString());
            c.addMethodCall(m.toString());
        }
    }
}
