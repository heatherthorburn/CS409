import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CBO {

    private ArrayList<ClassData> classesData;
    public HashMap<String, HashSet<String>> coupling;

    public CBO(ArrayList<ClassData> c) {
        this.classesData = c;
        coupling = new HashMap<>();
    }

    public HashMap<String, HashSet<String>> calculateCBO() {

        ArrayList<String> classes = new ArrayList<>();

        for (ClassData cd: classesData) {
            classes.add(cd.getClassName());
            coupling.put(cd.getClassName(), new HashSet<>());
        }

        for (ClassData cd : classesData) {
            ArrayList<String> methodCalls = cd.getMethodCalls();
            ArrayList<Variable> variables = cd.getVariablesUsed();
            for (String m : methodCalls) {
                for (Variable v : variables) {
                    if (m.startsWith(v.getName()) && classes.contains(v.getType()) && !v.getType().equals(cd.getClassName())) {
                        HashSet<String> current = coupling.get(cd.getClassName());
                        current.add(v.getType());
                        coupling.put(cd.getClassName(), current);
                        current = coupling.get(v.getType());
                        current.add(cd.getClassName());
                        coupling.put(v.getType(), current);
                    }
                }
            }
        }

        return coupling;
    }

}
