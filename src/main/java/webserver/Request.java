package webserver;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private String threadId;
    private String requestString;
    private String resource;
    private Map<String, String> params;
    private ByteArrayOutputStream outputStream;

    private String requestIp;

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getRequestString() {
        return requestString;
    }

    public void setRequestString(String requestString) {
        this.requestString = requestString;
        String[] lines = requestString.split("\n");
        String[] line1 = lines[0].split(" ");

        this.setResource(line1[1]);
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;

        if (resource.contains("?")) {
            setParams(resource);
        }
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public String getParam(String parameter) {
        if (this.params == null) {
            return null;
        }
        return this.params.get(parameter);
    }
    public void setParams(String reqResource) {
        reqResource = reqResource.substring(reqResource.indexOf("?"));
        reqResource = reqResource.replace("?", "");
        reqResource = reqResource.replace("+", " ");

        String[] params = reqResource.split("&");

        Map<String, String> paramsIndex = new HashMap<>();

        for (int i = 0; i < params.length; i++) {
            paramsIndex.put(params[i].split("=")[0], params[i].split("=")[1]);
        }

        this.setParams(paramsIndex);
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(ByteArrayOutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
