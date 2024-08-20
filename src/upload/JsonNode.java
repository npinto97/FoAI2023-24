package upload;

import java.util.HashMap;
import java.util.Map;

public class JsonNode {
	private String jtype;
    private String identity;
    private String label;
    private Map<String, String> properties = new HashMap<>();

    public String getJtype() {
        return jtype;
    }
    
    public String getIdentity() {
        return identity;
    }
    
    public String getLabel() {
       return label;
    }
    
    public Map<String, String> getProperties() {
        return properties;
    }
    
    public void setJtype(String jtype) {
        this.jtype = jtype;
    }
    
    public void setIdentity(String identity) {
        this.identity = identity;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
