import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;


public class CyclomaticComplexity {

    private static ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<>();
    private static ArrayList<Integer> numDecisions = new ArrayList<>();

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

            ComplexityVisitor run = new ComplexityVisitor();
            run.visit(cu, null);
        }
        CalculateComplexity();

    }

    private static void CalculateComplexity() {
        System.out.println();
        System.out.println("-------WMC Value-------");
        for (int i=0;i<classes.size();i++) {
            System.out.println("Class Name: "+classes.get(i).getName());
            System.out.println("WMC Value: " +classes.get(i).getMethods().size());
            System.out.println("WMC with Cyclomatic Complexity Value: "+numDecisions.get(i));
        }

    }

    private static class ComplexityVisitor extends VoidVisitorAdapter {
        public void visit(ClassOrInterfaceDeclaration ci, Object a) {
            classes.add(ci);
            numDecisions.add(0);
            incrementArrayList();
            super.visit(ci, a);
        }

        public void visit (IfStmt i, Object a) {
            incrementArrayList();
            if (i.toString().contains("&&")) {
                incrementArrayList();
            }
            if (i.toString().contains("||")) {
                incrementArrayList();
            }
            super.visit(i, a);
        }

        public void visit (SwitchStmt s, Object a) {
            incrementArrayList();
            super.visit(s, a);
        }

        public void visit (WhileStmt w, Object a) {
            incrementArrayList();
            super.visit(w, a);
        }

        public void visit (ForStmt f, Object a) {
            incrementArrayList();
            super.visit(f, a);
        }

        public void visit (ForEachStmt fe, Object a) {
            incrementArrayList();
            super.visit(fe, a);
        }

        public void visit (DoStmt d, Object a) {
            incrementArrayList();
            super.visit(d, a);
        }

        public void visit (AssertStmt a, Object o) {
            incrementArrayList();
            super.visit(a, o);
        }

        public void incrementArrayList() {
            int lastInt = numDecisions.get(numDecisions.size()-1);
            lastInt++;
            numDecisions.remove(numDecisions.size()-1);
            numDecisions.add(lastInt);
        }

    }
}