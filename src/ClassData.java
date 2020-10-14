import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ClassData {

    private String className;
    private ArrayList<Variable> variables;
    private ArrayList<String> methods;
    private ArrayList<MethodCall> methodCalls;
    private HashSet<String> fieldNames;
    private HashMap<String, HashSet<String>> namesInMethods;

    ClassData(String n) {
        this.className = n;
        variables = new ArrayList<>();
        methods = new ArrayList<>();
        methodCalls = new ArrayList<>();
        fieldNames = new HashSet<>();
    }

    void addMethodCall(MethodCall m) {
        methodCalls.add(m);
    }

    void addVariableUsed(Variable v) {
        variables.add(v);
    }

    void addMethod(String s) {
        methods.add(s);
    }

    void addFieldVariable(String f) { fieldNames.add(f); }

    void addNamesInMethods(HashMap<String, HashSet<String>> names) { namesInMethods = names; }

    String getClassName() { return className; }

    public ArrayList<Variable> getVariablesUsed() { return variables; }

    ArrayList<String> getMethods() { return methods; }

    ArrayList<MethodCall> getMethodCalls() { return methodCalls; }

    HashSet<String> getFields() { return fieldNames; }

    HashMap<String, HashSet<String>> getNamesInMethods() { return namesInMethods; }

}
