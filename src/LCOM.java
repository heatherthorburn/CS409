import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.lang.Math;

class LCOM {

    private HashSet<String> fields;
    private HashMap<String, HashSet<String>> methodsUsedFields;
    private ArrayList<String> methods;


    LCOM(ClassData c) {
        methods = new ArrayList<>();
        fields = c.getFields();
        methodsUsedFields = c.getNamesInMethods();
        calculateLCOM();
    }

    private void calculateLCOM() {
        int p = 0, q = 0;
        findFieldsInMethods();
        for (int i = 0; i < methods.size()-1; i++) {
            HashSet set1 = (HashSet) methodsUsedFields.get(methods.get(i)).clone();
            for (int j = i+1; j < methods.size(); j++) {
                HashSet set2 = (HashSet) methodsUsedFields.get(methods.get(j)).clone();
                set2.retainAll(set1);
                if (set2.size() == 0) { p++; } else { q++; }
            }
        }

        int lcom;
        if (Math.abs(p) > Math.abs(q)) { lcom = Math.abs(p) - Math.abs(q); }
        else { lcom = 0; }
        System.out.println("LCOM: " + lcom);
    }

    private void findFieldsInMethods() {
        methodsUsedFields.forEach((X,Y)->{
            HashSet<String> variables = methodsUsedFields.get(X);
            variables.retainAll(fields);
            methodsUsedFields.put(X, variables);
            if (variables.size() > 0) { methods.add(X); }
        });
    }
}
