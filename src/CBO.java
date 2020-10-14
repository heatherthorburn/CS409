import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CBO {

    private ArrayList<ClassData> classesData;
    private HashMap<String, HashSet<String>> coupling;

    CBO(ArrayList<ClassData> c) {
        this.classesData = c;
        coupling = new HashMap<>();
    }

    HashMap<String, HashSet<String>> calculateCBO() {

        for (ClassData cd : classesData) {
            coupling.put(cd.getClassName(), new HashSet<>());
        }

        for (ClassData cd : classesData) {
            ArrayList<MethodCall> methodCalls = cd.getMethodCalls();
            for (MethodCall m : methodCalls) {
                for (ClassData cd2 : classesData) {
                    ArrayList<String> classesMethods = cd2.getMethods();
                    if (classesMethods.contains(m.getMethod()) && !cd2.getClassName().equals(cd.getClassName())) {
                        addCouple(cd.getClassName(), cd2.getClassName());
                        break;
                    }
                }
            }
        }
        return coupling;
    }


    private void addCouple(String a, String b) {
        HashSet<String> current = coupling.get(a);
        current.add(b);
        coupling.put(a, current);
        current = coupling.get(b);
        current.add(a);
        coupling.put(b, current);
    }
}
