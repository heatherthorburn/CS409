public class Variable {

    public String type;
    public String name;

    public Variable (String t, String n) {
        this.name = n;
        this.type = t;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
