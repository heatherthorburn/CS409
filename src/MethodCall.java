public class MethodCall {

    private String fullCall;
    private String variable;
    private String method;

    MethodCall(String full, String variable, String method) {
        this.fullCall = full;
        this.variable = variable;
        this.method = method;
        parseMethodCall();
    }

    private void parseMethodCall() {
        method = method.substring(0, method.indexOf("("));
    }

    String getMethod(){
        return method;
    }


}
