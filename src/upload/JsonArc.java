package upload;

import java.util.HashMap;
import java.util.Map;

public class JsonArc{
	private String jtype;
    private String subject;
    private String object;
    private String name;
    private Map<String, String> properties = new HashMap<>();

    public String getJtype() {
        return jtype;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public String getObject() {
       return object;
    }
    
    public String getName() {
        return name;
    }
    
    public Map<String, String> getProperties() {
        return properties;
    }
    
    public void setJtype(String jtype) {
        this.jtype = jtype;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public void setObject(String object) {
        this.object = object;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
