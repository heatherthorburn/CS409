import com.github.javaparser.ast.expr.SimpleName;

import java.util.ArrayList;
import java.util.HashSet;

public class CBOData {
    private SimpleName className;
    private ArrayList<SimpleName> methodsDeclared;
    private ArrayList<SimpleName> methodsCalled;
    private HashSet<SimpleName> coupling;

    CBOData(SimpleName n, ArrayList<SimpleName> md, ArrayList<SimpleName> mc) {
        this.className = n;
        this.methodsDeclared = md;
        this.methodsCalled = mc;
        coupling = new HashSet<>();
    }

    SimpleName getClassName() {
        return className;
    }

    ArrayList<SimpleName> getMethodsDeclared() {
        return methodsDeclared;
    }

    ArrayList<SimpleName> getMethodsCalled() {
        return methodsCalled;
    }

    void addCoupling(SimpleName n) {
        coupling.add(n);
    }

    int getCBO() {
        return coupling.size();
    }

    void printCouplingClasses() {
        System.out.println(coupling.toString());
    }
}
