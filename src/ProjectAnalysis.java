import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

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

        for (ClassData d : cd) {
            System.out.println("***********");
            System.out.println("CLASS NAME");
            System.out.println(d.getClassName());
            ArrayList<String> variablesUsed = d.getVariablesUsed();
            System.out.println("VARIABLES USED");
            for (String v : variablesUsed) {
                System.out.println(v);
            }
            ArrayList<String> methodsUsed = d.getMethods();
            System.out.println("METHODS USED");
            for (String m : methodsUsed) {
                System.out.println(m);
            }
            System.out.println("*******");
        }
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
            super.visit(f, a);
        }

        public void visit(VariableDeclarator v, Object a) {
            c.addVariableUsed(v.getTypeAsString());
        }
    }
}
