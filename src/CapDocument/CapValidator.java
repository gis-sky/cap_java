package CapDocument;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.text.Document;
import javax.xml.datatype.DatatypeConstants.Field;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CapValidator {
	private final static String ALERT = "alert";
    private final static String INFO = "info";

    private final static ArrayList<String> ALERT_TAGS = new ArrayList<String>
    	(
			Arrays.asList(
					"identifier", 
					"sender", 
					"sent", 
					"status", 
					"msgType", 
					"scope"
					)
		);

    private static ArrayList<String> INFO_TAGS = new ArrayList<String>
    	(
			Arrays.asList(
		            "language",
		            "category",
		            "event",
		            "responseType",
		            "urgency",
		            "severity",
		            "certainty",
		            "audience",
		            "eventCode",
		            "effective",
		            "onset",
		            "expires",
		            "senderName",
		            "headline",
		            "description",
		            "instruction",
		            "web",
		            "contact",
		            "parameter",
		            "resource",
		            "area"
					)
		);
    
    private static ArrayList<String> INFO_OPTION_TAGS = new ArrayList<String>
    	(
			Arrays.asList(
		            "language",
		            "responseType",
		            "audience",
		            "eventCode",
		            "effective",
		            "onset",
		            "expires",
		            "senderName",
		            "headline",
		            "description",
		            "instruction",
		            "web",
		            "contact",
		            "parameter",
		            "resource",
		            "area"
					)
		);

    private static Map<String, Pattern> INFO_REGEX_DIC = new HashMap<String, Pattern>()
        {{
            put("effective", Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d[-,+]\\d\\d:\\d\\d"));
            put("onset", Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d[-,+]\\d\\d:\\d\\d"));
            put("expires", Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d[-,+]\\d\\d:\\d\\d"));
        }};

    private static Map<String, ArrayList<String>> INFO_VALID_DIC = new HashMap<String, ArrayList<String>>()
        {{
        	put("category",  new ArrayList<String>(
						Arrays.asList(
		                    "Geo",
		                    "Met",
		                    "Safety",
		                    "Security",
		                    "Rescue",
		                    "Fire",
		                    "Health",
		                    "Env",
		                    "Transport",
		                    "Infra",
		                    "CBRNE",
		                    "Other"
	                    )
        			)
			);
        	put("responseType",  new ArrayList<String>(
						Arrays.asList(
		                    "Shelter",
		                    "Evacuate",
		                    "Prepare",
		                    "Execute",
		                    "Avoid",
		                    "Monitor",
		                    "Assess",
		                    "AllClear",
		                    "None"
	                    )
	    			)
			);
        	put("urgency",  new ArrayList<String>(
						Arrays.asList(
		                    "Immediate",
		                    "Expected",
		                    "Future",
		                    "Past",
		                    "Unknown"
	                    )
	    			)
			);
        	put("severity",  new ArrayList<String>(
    					Arrays.asList(
		                    "Extreme",
		                    "Severe",
		                    "Moderate",
		                    "Minor",
		                    "Unknown"
	                    )
	    			)
			);
        	put("certainty",  new ArrayList<String>(
        				Arrays.asList(
		                    "Observed",
		                    "Likely",
		                    "Possible",
		                    "Unlikely",
		                    "Unknown"
	                    )
	    			)
			);
        }};

    private static ArrayList<String> STATUS = new ArrayList<String>
        (
    		Arrays.asList(
				"Actual",
	            "Exercise",
	            "System",
	            "Test",
	            "Draft"
            )
        );

    private static ArrayList<String> MSG_TYPE = new ArrayList<String>
		(
    		Arrays.asList(
	            "Alert",
	            "Update",
	            "Cancel",
	            "Ack",
	            "Error"
            )
        );

    private static ArrayList<String> SCOPE = new ArrayList<String>
        (
    		Arrays.asList(
	            "Public",
	            "Restricted",
	            "Private"
            )
        );
    
    /**
     * 驗證Cap物件
     * @param capDocument Cap物件
     * @return 驗證結果
     */
    public static ArrayList<CapValidateResult> Validate(CapDocument capDocument)
    {
    	ArrayList<CapValidateResult> capValidateResults = new ArrayList<CapValidateResult>();
        for (String name : ALERT_TAGS)
        {
            switch (name)
            {
                case "sent":
                	Pattern pattern = Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d[-,+]\\d\\d:\\d\\d");
                    if (!pattern.matcher(capDocument.sent).matches())
                    {
                        capValidateResults.add(new CapValidateResult(name, String.format("%1$s資料格式錯誤.", name)));
                    }
                    break;
                case "status":
                    if (!STATUS.contains(capDocument.status))
                    {
                        capValidateResults.add(new CapValidateResult(name, String.format("%1$s資料格式錯誤.", name)));
                    }
                    break;
                case "msgType":
                    if (!MSG_TYPE.contains(capDocument.msgType))
                    {
                        capValidateResults.add(new CapValidateResult(name, String.format("%1$s資料格式錯誤.", name)));
                    }
                    break;
                case "scope":
                    if (!SCOPE.contains(capDocument.scope))
                    {
                        capValidateResults.add(new CapValidateResult(name, String.format("%1$s節點資料格式錯誤.", name)));
                    }
                    break;
            }
        }
        ArrayList<Info> infos = capDocument.info;
        if (infos.size() > 0)
        {
            for (Info info : infos)
            {
                for (String name : INFO_TAGS)
                {
                	for (CapValidateResult capValidateResult : capValidateResults) {
                		if(capValidateResult.GetSubject() == name)
                			continue;
        			}
                	
                	java.lang.reflect.Field field = null;
					try {
						field = info.getClass().getDeclaredField(name);
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	try {
						if (field == null || field.get(info) == null)
						    continue;
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    String value = null;
					try {
						value = field.get(info).toString();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    if (value == "")
                        continue;
                    if (INFO_REGEX_DIC.containsKey(name))
                    {
                        if (!INFO_REGEX_DIC.get(name).matcher(value).matches())
                        {
                            capValidateResults.add(new CapValidateResult(name, String.format("%1$s資料格式錯誤.", name)));
                            continue;
                        }
                    }
                    if (INFO_VALID_DIC.containsKey(name))
                    {
                        if (!INFO_VALID_DIC.get(name).contains(value))
                        {
                            capValidateResults.add(new CapValidateResult(name, String.format("%1$s資料格式錯誤.", name)));
                            continue;
                        }
                    }
                }
            }
        }
        return capValidateResults;
    }
    
    /**
     * 驗證Cap XML物件
     * @param doc Cap XML物件
     * @return 驗證結果
     */
    public static ArrayList<CapValidateResult> Validate(org.w3c.dom.Document doc)
    {
    	ArrayList<CapValidateResult> capValidateResults = new ArrayList<CapValidateResult>();
    	NodeList nodes = doc.getElementsByTagName("alert");
    	if (nodes.getLength() == 0) {
			capValidateResults.add(new CapValidateResult(ALERT, String.format("根節點必須是%1$s.", ALERT)));
			return capValidateResults;
    	}

    	Node rootNode = (Node) nodes.item(0);
        Element element = (Element) rootNode;
        for (String name : ALERT_TAGS) 
        {
        	for (CapValidateResult capValidateResult : capValidateResults) {
        		if(capValidateResult.GetSubject() == name)
        			continue;
			}
        	
        	NodeList tmpNodes = element.getElementsByTagName(name);
        	if(tmpNodes.getLength() == 0) {
              capValidateResults.add(new CapValidateResult(name, String.format("必須有%1$s節點.", name)));
              continue;
        	}
        	Node tmpNode = tmpNodes.item(0);
        	
            switch (name)
            {
                case "sent":
                    Pattern pattern = Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d[-,+]\\d\\d:\\d\\d");
                    if (!pattern.matcher(getValue(name, element)).matches())
                    {
                        capValidateResults.add(new CapValidateResult(name, String.format("%1$s節點資料格式錯誤.", name)));
                    }
                    break;
                case "status":
                    if (!STATUS.contains(getValue(name, element)))
                    {
                        capValidateResults.add(new CapValidateResult(name, String.format("%1$s節點資料格式錯誤.", name)));
                    }
                    break;
                case "msgType":
                    if (!MSG_TYPE.contains(getValue(name, element)))
                    {
                        capValidateResults.add(new CapValidateResult(name, String.format("%1$s節點資料格式錯誤.", name)));
                    }
                    break;
                case "scope":
                    if (!SCOPE.contains(getValue(name, element)))
                    {
                        capValidateResults.add(new CapValidateResult(name, String.format("%1$s節點資料格式錯誤.", name)));
                    }
                    break;
            }
        }
        
    	NodeList infoNodes = element.getElementsByTagName(INFO);
        for (int i = 0; i < infoNodes.getLength(); i++)
        {
            for (String name : INFO_TAGS)
            {
            	for (CapValidateResult capValidateResult : capValidateResults) {
            		if(capValidateResult.GetSubject() == name)
            			continue;
    			}
            	
            	Element info = (Element) infoNodes.item(i);
            	NodeList xelements = info.getElementsByTagName(name);
                if (!(xelements.getLength() > 0))
                {
                    if (!INFO_OPTION_TAGS.contains(name))
                        capValidateResults.add(new CapValidateResult(name, String.format("必須有%1$s節點.", name)));
                    continue;
                }
                if (INFO_REGEX_DIC.containsKey(name))
                {
                    if (!INFO_REGEX_DIC.get(name).matcher(getValue(name, info)).matches())
                    {
                        capValidateResults.add(new CapValidateResult(name, String.format("%1$s節點資料格式錯誤.", name)));
                        continue;
                    }
                }
                if (INFO_VALID_DIC.containsKey(name))
                {
                    if (!INFO_VALID_DIC.get(name).contains(getValue(name, info)))
                    {
                        capValidateResults.add(new CapValidateResult(name, String.format("%1$s節點資料錯誤.", name)));
                        continue;
                    }
                }
                switch (name)
                {
                    case "eventCode":
                    case "parameter":
                        for (String elementName : new ArrayList<String>(Arrays.asList("valueName", "value")))
                        {
                            if (!(info.getElementsByTagName(elementName).getLength() > 0))
                                capValidateResults.add(new CapValidateResult(name, String.format("%1$s節點缺少%2$s.", name, elementName)));
                        }
                        break;
                    case "resource":
                        for (String elementName : new ArrayList<String>(Arrays.asList("resourceDesc", "mimeType")))
                        {
                        	if (!(info.getElementsByTagName(elementName).getLength() > 0))
                                capValidateResults.add(new CapValidateResult(name, String.format("%1$s節點缺少%2$s.", name, elementName)));
                        }
                        break;
                    case "area":
                        for (String elementName : new ArrayList<String>(Arrays.asList("areaDesc")))
                        {
                        	if (!(info.getElementsByTagName(elementName).getLength() > 0))
                                capValidateResults.add(new CapValidateResult(name, String.format("%1$s節點缺少%2$s.", name, elementName)));
                        }
                        NodeList geocodes = info.getElementsByTagName("geocode");
                        for (int j = 0; j < geocodes.getLength(); j++)
                        {
                            for (String elementName : new ArrayList<String>(Arrays.asList("valueName", "value")))
                            {
                                if (!(((Element)geocodes.item(j)).getElementsByTagName(elementName).getLength() > 0))
                                    capValidateResults.add(new CapValidateResult(name, String.format("%1$s節點缺少%2$s.", "geocode", elementName)));
                            }
                        }
                        break;
                }
            }
        }
        return capValidateResults;
    }

	private static String getValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}
}
