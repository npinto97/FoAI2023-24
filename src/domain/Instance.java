package domain;
 import java.util.List;
//funziona
import java.util.Map;

public class Instance {
	private String type; 
	public String selectedInstanceId; //ste non puo' essere int?
	private Map<String,String> attributeValues;
	private String shortDescription;

	public Instance(String type, String selectedInstanceId, Map<String,String> attrVals, List<Attribute> fields) {
		this.type = type;
		this.selectedInstanceId = selectedInstanceId;
		this.shortDescription = buildShortDescription(selectedInstanceId, type, attrVals, fields);
		this.attributeValues = attrVals;
	}

	private String buildShortDescription(String id, String type, Map<String,String> myAttributeValues, List<Attribute> fields) {
		String shortDescription = "";
		for(Attribute a : fields)
			if(a.isDescriptive() && myAttributeValues.get(a.getName()) != null)
				shortDescription += myAttributeValues.get(a.getName()) + " ";
		return shortDescription += "  <" + id + ":" + type + ">";
	}

	public String getType() {
		return type;
	}
	public String getSelectedInstanceId() {
		return selectedInstanceId;
	}
	public Map<String, String> getAttributeValues() {
		return attributeValues;
	}
//	public void setAttributeValues(Map<String, String> attributeValues) {
//		this.attributeValues = attributeValues;
//	}
	public String getShortDescription() {
		return shortDescription;
	}
//	public void setShortDescription(String shortDescription) {
//		this.shortDescription = shortDescription;
//	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Instance objInstance = (Instance) obj;
		return this.selectedInstanceId.equals(objInstance.selectedInstanceId);
	}

}