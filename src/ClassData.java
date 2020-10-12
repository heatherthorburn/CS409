import java.util.ArrayList;

public class ClassData {

    private String className;
    private ArrayList<Variable> variables;
    private ArrayList<String> methods;
    private ArrayList<String> fields;
    private ArrayList<String> methodCalls;

    ClassData(String n) {
        this.className = n;
        variables = new ArrayList<>();
        methods = new ArrayList<>();
        fields = new ArrayList<>();
        methodCalls = new ArrayList<>();
    }

    public void addField(String f) {
        fields.add(f);
    }

    public void addMethodCall(String m) {
        methodCalls.add(m);
    }

    public void addVariableUsed(Variable v) {
        variables.add(v);
    }

    public void addMethod(String s) {
        methods.add(s);
    }

    public String getClassName() {
        return className;
    }

    public ArrayList<Variable> getVariablesUsed() {
        return variables;
    }

    public ArrayList<String> getMethods() {
        return methods;
    }

    public ArrayList<String> getFields() {
        return fields;
    }

    public ArrayList<String> getMethodCalls() {
        return methodCalls;
    }
}
