import java.util.ArrayList;

public class ClassData {

    private String className;
    private ArrayList<String> variablesUsed;
    private ArrayList<String> methods;

    ClassData(String n) {
        this.className = n;
        variablesUsed = new ArrayList<>();
        methods = new ArrayList<>();
    }

    public void addVariableUsed(String s) {
        variablesUsed.add(s);
    }

    public void addMethod(String s) {
        methods.add(s);
    }

    public String getClassName() {
        return className;
    }

    public ArrayList<String> getVariablesUsed() {
        return variablesUsed;
    }

    public ArrayList<String> getMethods() {
        return methods;
    }


}
