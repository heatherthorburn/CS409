import com.github.javaparser.Position;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class AnalysisTool {

    private static ArrayList<File> files = new ArrayList<>();
    private static ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<>();
    private static ArrayList<MethodDeclaration> methods = new ArrayList<>();
    private static HashMap<ClassOrInterfaceDeclaration, Integer> simpleComplexities = new HashMap<>();
    private static int complexityRunning;
    private static int finalClassLine;
    private static ArrayList<Integer> cyclomaticComplexity = new ArrayList<>();
    private static Optional classRange = Optional.empty();



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
        System.out.println("---------COMPLEX CLASS COMPLEXITY----------");
        int classIndex=0;
        int classComplexity = cyclomaticComplexity.get(classIndex);
        for (File f : files) {
            if (classIndex != 0) {
                classComplexity = cyclomaticComplexity.get(classIndex) - cyclomaticComplexity.get(classIndex - 1);
            }
            System.out.println("Class Name: "+f.getName());
            System.out.println("Cyclomatic Complexity: "+classComplexity);
            classIndex++;
        }
    }

    private static void getFiles() {
       File[] folderFiles;
       File in = new File("Animal");
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
            //cyclomaticComplexity.add(complexityRunning);
            classRange = ci.getRange();
            finalClassLine = parseRange(classRange);
            super.visit(ci, a);
        }

        public void visit(MethodDeclaration m, Object a) {
            methods.add(m);
            Optional p = m.getRange();
            int methodRange = parseRange(p);
            ArrayList<Integer> finalClassLines = makeLineList(finalClassLine);
            if (finalClassLines.contains(methodRange)) {
                cyclomaticComplexity.add(complexityRunning);
            }
            super.visit(m, a);
        }

        public void visit(MethodCallExpr m, Object a) {
            complexityRunning++;
            super.visit(m, a);
        }

        public void visit(ConditionalExpr c, Object a) {
            complexityRunning++;
            super.visit(c, a);
        }

        public void visit(ForEachStmt fe, Object a) {
            complexityRunning++;
            super.visit(fe, a);
        }

        public void visit(ForStmt f, Object a) {
            complexityRunning++;
            super.visit(f, a);
        }

        public void visit(WhileStmt w, Object a) {
            complexityRunning++;
            super.visit(w, a);
        }

        public void visit(DoStmt d, Object a) {
            complexityRunning++;
            super.visit(d, a);
        }

        public void visit(SwitchEntry se, Object a) {
            complexityRunning++;
            super.visit(se, a);
        }

        public void visit(BinaryExpr be, Object a) {
            BinaryExpr.Operator operator = be.getOperator();
            if (operator.asString().equals("AND") || operator.asString().equals("OR")) {
                complexityRunning++;
            }
            super.visit(be, a);
        }

        public int parseRange(Optional r) {
            String s = r.toString();
            int lineIndex = s.indexOf("line", s.indexOf("line")+1);
            int numIndex = lineIndex + 5;
            int spaceIndex = s.indexOf(",", numIndex);
            String lineNumber = s.substring(numIndex, spaceIndex);
            return Integer.parseInt(lineNumber);
        }

        public ArrayList<Integer> makeLineList(int l) {
            ArrayList<Integer> lineList = new ArrayList();
            lineList.add(l);
            lineList.add(l-1);
            return lineList;
        }


    }

}
