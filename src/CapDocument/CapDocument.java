package CapDocument;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

public class CapDocument {
	private String xmlns;
	/**
	 * 警報識別碼
	 */
    public String identifier;
    /**
     * 來源者識別碼
     */
    public String sender;
    /**
     * 發送日期與時間
     */
    public String sent;
    /**
     * 類別狀態碼
     */
    public String status;
    /**
     * 指令類別碼
     */
    public String msgType;
    /**
     * 來源簡述
     */
    public String source;
    /**
     * 接收者範圍
     */
    public String scope;
    /**
     * 說明接受者條件
     */
    public String restriction;
    /**
     * 接收者列表
     */
    public String addresses;
    /**
     * 特殊處理代碼
     */
    public ArrayList<String> code = new ArrayList<String>();
    /**
     * 描述說明
     */
    public String note;
    /**
     * 相關的識別碼
     */
    public String references;
    /**
     * 相關資訊列表
     */
    public String incidents;
    /**
     * Info
     */
    public ArrayList<Info> info = new ArrayList<Info>();
    
    private ArrayList<CapValidateResult> capValidateResults;

    public CapDocument()
    {

    }
    
    public CapDocument(String filePath) throws Exception
    {
    	Load(filePath);
    }

    /**
     * 檢查資料驗證是否通過
     * @return 驗證結果
     */
    public boolean IsValid()
    {
        return capValidateResults.size() == 0;
    }
    
    /**
     * 讀取Cap檔案
     * @param filePath Cap檔案路徑
     * @throws Exception
     */
    public void Load(String filePath) throws Exception
    {
        try 
        {
        	File stocks = new File(filePath);
        	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        	org.w3c.dom.Document doc = dBuilder.parse(stocks);
        	doc.getDocumentElement().normalize();

            capValidateResults = CapValidator.Validate(doc);
            if (IsValid())
            {
                perform(doc);
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }
    
    /**
     * 驗證CapDocument物件
     * @return 驗證結果
     */
    public ArrayList<CapValidateResult> Validate()
    {
        return CapValidator.Validate(this);
    }

    /**
     * 驗證Cap檔案
     * @param filePath Cap檔案路徑
     * @return 驗證結果
     */
    public static ArrayList<CapValidateResult> Validate(String filePath)
    {
    	File stocks = new File(filePath);
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	org.w3c.dom.Document doc = null;
		try {
			doc = dBuilder.parse(stocks);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return CapValidator.Validate(doc);
    }
    
    /**
     * 將Cap物件轉為Json
     * @return Json字串
     */
    public String ToJson()
    {
    	Gson gson = new Gson();
    	String json = gson.toJson(this);
    	return json;
    }
    
    /**
     * 將Cap物件轉為GeoRss
     * @return GeoRss字串
     */
    public String ToGeoRssItem()
    {
        StringBuilder sbGeoRssItem = new StringBuilder();
        String result = StringUtility.ReplaceContent(Templates.geoRssItemTemplate, "identifier", identifier);
        result = StringUtility.ReplaceContent(result, "sent", sent);
        result = StringUtility.ReplaceContent(result, "sender", sender);
        String event = "";
        StringBuilder sbMultiPolygon = new StringBuilder();
        if (!info.isEmpty())
        {
            result = StringUtility.ReplaceContent(result, "description", info.get(0).description);
            event = info.get(0).event;
            for (Info i : info)
            {
                if (!i.area.isEmpty())
                {
                    for (Area a : i.area)
                    {
                        if (!a.circle.isEmpty())
                            sbMultiPolygon.append(String.format(Templates.geoRssItemPolygonTemplate, join(" ", a.circle)));
                        if (!a.polygon.isEmpty())
                            sbMultiPolygon.append(String.format(Templates.geoRssItemPolygonTemplate, join(" ", a.polygon)));
                        if (!a.geocode.isEmpty())
                        {
                            for (Geocode g : a.geocode)
                            {
                                sbMultiPolygon.append(String.format(Templates.geoRssItemGeoCodeTemplate, GeoCodeUtility.GetPolygon(g.value, "gml")));
                            }
                        }
                    }
                }
            }
            result = StringUtility.ReplaceContent(result, "multipolygon", sbMultiPolygon.toString());
        }
        result = StringUtility.ReplaceContent(result, "event", event);
        sbGeoRssItem.append(result);

        return sbGeoRssItem.toString();
    }

    /**
     * 將Cap物件轉為Kml
     * @return Kml字串
     */
    public String ToKml()
    {
        // alert
        StringBuilder sbAlert = new StringBuilder();
        String tempStr = "<%1$s>%2$s</%1$s>";
        sbAlert.append(String.format(tempStr, "identifier", identifier));
        sbAlert.append(String.format(tempStr, "sender", sender));
        sbAlert.append(String.format(tempStr, "sent", sent));
        sbAlert.append(String.format(tempStr, "status", status));
        sbAlert.append(String.format(tempStr, "msgType", msgType));
        if(!isNullOrBlank(source))
            sbAlert.append(String.format(tempStr, "source", source));
        sbAlert.append(String.format(tempStr, "scope", scope));
        if(!isNullOrBlank(restriction))
            sbAlert.append(String.format(tempStr, "restriction", restriction));
        if(!isNullOrBlank(addresses))
            sbAlert.append(String.format(tempStr, "addresses", addresses));
        if (!code.isEmpty())
        {
            for (String c : code)
            {
            	sbAlert.append(String.format(tempStr, "code", c));
            }
        }
        if(!isNullOrBlank(note))
            sbAlert.append(String.format(tempStr, "note", note));
        if(!isNullOrBlank(references))
            sbAlert.append(String.format(tempStr, "references", references));
        if(!isNullOrBlank(incidents))
            sbAlert.append(String.format(tempStr, "incidents", incidents));
        String result = StringUtility.ReplaceContent(Templates.kmlTemplate, "alert", sbAlert.toString());

        // info
        StringBuilder sbInfo = new StringBuilder();
        if (info.isEmpty())
        {
            for (Info i : info)
            {
                StringBuilder sbInfoItem = new StringBuilder();
                if (!isNullOrBlank(i.language))
                    sbInfoItem.append(String.format(tempStr, "language", i.language));
                if (!i.category.isEmpty())
                {
                    for (String c : i.category)
                    {
                        sbInfoItem.append(String.format(tempStr, "category", c));
                    }
                }
                sbAlert.append(String.format(tempStr, "event", i.event));
                if (!i.responseType.isEmpty())
                {
                    for (String r : i.responseType)
                    {
                        sbInfoItem.append(String.format(tempStr, "responseType", r));
                    }
                }
                sbInfoItem.append(String.format(tempStr, "urgency", i.urgency));
                sbInfoItem.append(String.format(tempStr, "severity", i.severity));
                sbInfoItem.append(String.format(tempStr, "certainty", i.certainty));
                StringUtility.SetCotent(sbInfoItem, "audience", i.audience);
                if (!i.eventCode.isEmpty())
                {
                    for (EventCode e : i.eventCode)
                    {
                        String contentString = String.format(tempStr, "valueName", e.valueName) + String.format(tempStr, "value", e.value);
                        sbInfoItem.append(String.format(tempStr, "eventCode", contentString));
                    }
                }
                StringUtility.SetCotent(sbInfoItem, "effective", i.effective);
                StringUtility.SetCotent(sbInfoItem, "onset", i.onset);
                StringUtility.SetCotent(sbInfoItem, "expires", i.expires);
                StringUtility.SetCotent(sbInfoItem, "senderName", i.senderName);
                StringUtility.SetCotent(sbInfoItem, "headline", i.headline);
                StringUtility.SetCotent(sbInfoItem, "description", i.description);
                StringUtility.SetCotent(sbInfoItem, "instruction", i.instruction);
                StringUtility.SetCotent(sbInfoItem, "web", i.web);
                StringUtility.SetCotent(sbInfoItem, "contact", i.contact);
                StringUtility.SetCotent(sbInfoItem, "headline", i.headline);
                if (!i.parameter.isEmpty())
                {
                    for (Parameter p : i.parameter)
                    {
                        String contentString = String.format(tempStr, "valueName", p.valueName) + String.format(tempStr, "value", p.value);
                        sbInfoItem.append(String.format(tempStr, "parameter", contentString));
                    }
                }

                // area
                StringBuilder sbArea = new StringBuilder();
                if (!i.area.isEmpty())
                {
                    StringBuilder sbAreaItem = new StringBuilder();
                    for (Area a : i.area)
                    {
                        StringBuilder sbAreaPolygon = new StringBuilder();
                        if (!a.polygon.isEmpty())
                        {
                            sbAreaPolygon.append(String.format(Templates.kmlAreaPolygonTemplate, "polygon", join(" ", a.polygon)));
                            //foreach (var p in a.polygon)
                            //{
                            //    sbAreaPolygon.append(String.format(tempStr, "polygon", p));
                            //}
                        }
                        if (!a.circle.isEmpty())
                        {
                            sbAreaPolygon.append(String.format(Templates.kmlAreaPolygonTemplate, "circle", join(" ", a.circle)));
                            //foreach (var c in a.circle)
                            //{
                            //    sbAreaPolygon.append(String.format(tempStr, "circle", c));
                            //}
                        }
                        if (!a.geocode.isEmpty())
                        {
                            for (Geocode g : a.geocode)
                            {
                                sbAreaPolygon.append(GeoCodeUtility.GetPolygon(g.value, "kml"));
                                //String contentString = String.format(tempStr, "valueName", g.valueName) + String.format(tempStr, "value", g.value);
                                //sbAreaPolygon.append(String.format(tempStr, "geocode", contentString));
                            }
                        }
                        StringBuilder sbAreaOther = new StringBuilder();
                        StringUtility.SetCotent(sbAreaOther, "altitude", String.valueOf(a.altitude));
                        StringUtility.SetCotent(sbAreaOther, "ceiling", String.valueOf(a.ceiling));
                        sbAreaItem.append(String.format(Templates.kmlAreaTemplate, a.areaDesc,
                                                    sbAreaPolygon.toString(), sbAreaOther.toString()));
                    }
                    sbArea.append(String.format(Templates.kmlAreaScopeTemplate, sbAreaItem.toString()));
                }

                // resource
                StringBuilder sbResource = new StringBuilder();
                if (!i.resource.isEmpty())
                {
                    StringBuilder sbResourceItem = new StringBuilder();
                    for (Resource r : i.resource)
                    {
                        StringUtility.SetCotent(sbResourceItem, "resourceDesc", r.resourceDesc);
                        StringUtility.SetCotent(sbResourceItem, "mimeType", r.mimeType);
                        StringUtility.SetCotent(sbResourceItem, "size", String.valueOf(r.size));
                        StringUtility.SetCotent(sbResourceItem, "uri", r.uri);
                        StringUtility.SetCotent(sbResourceItem, "derefUri", r.derefUri);
                        StringUtility.SetCotent(sbResourceItem, "digest", r.digest);
                        sbResourceItem.append(String.format(Templates.kmlResourceTemplate, sbResourceItem.toString()));
                    }
                    sbResource.append(String.format(Templates.kmlResourceScopeTemplate, sbResourceItem.toString()));
                }

                sbInfo.append(String.format(Templates.kmlInfoTemplate, sbInfoItem.toString(), sbArea.toString(), sbResource.toString()));
            }
        }
        result = StringUtility.ReplaceContent(result, "info", sbInfo.toString());

        return result;
    }
    
    /**
     * 將Cap物件轉為Cap
     * @return Cap字串
     */
    public String ToCap()
    {
        // alert
        StringBuilder sbAlert = new StringBuilder();
        String tempStr = "<%1$s>%2$s</%1$s>";
        sbAlert.append(String.format(tempStr, "identifier", identifier));
        sbAlert.append(String.format(tempStr, "sender", sender));
        sbAlert.append(String.format(tempStr, "sent", sent));
        sbAlert.append(String.format(tempStr, "status", status));
        sbAlert.append(String.format(tempStr, "msgType", msgType));
        if(!isNullOrBlank(source))
            sbAlert.append(String.format(tempStr, "source", source));
        sbAlert.append(String.format(tempStr, "scope", scope));
        if(!isNullOrBlank(restriction))
            sbAlert.append(String.format(tempStr, "restriction", restriction));
        if(!isNullOrBlank(addresses))
            sbAlert.append(String.format(tempStr, "addresses", addresses));
        if (!code.isEmpty())
        {
            for (String c : code)
            {
            	sbAlert.append(String.format(tempStr, "code", c));
            }
        }
        if(!isNullOrBlank(note))
            sbAlert.append(String.format(tempStr, "note", note));
        if(!isNullOrBlank(references))
            sbAlert.append(String.format(tempStr, "references", references));
        if(!isNullOrBlank(incidents))
            sbAlert.append(String.format(tempStr, "incidents", incidents));
        String result = StringUtility.ReplaceContent(Templates.kmlTemplate, "alert", sbAlert.toString());

        // info
        StringBuilder sbInfo = new StringBuilder();
        if (info.isEmpty())
        {
            for (Info i : info)
            {
                StringBuilder sbInfoItem = new StringBuilder();
                if (!isNullOrBlank(i.language))
                    sbInfoItem.append(String.format(tempStr, "language", i.language));
                if (!i.category.isEmpty())
                {
                    for (String c : i.category)
                    {
                        sbInfoItem.append(String.format(tempStr, "category", c));
                    }
                }
                sbAlert.append(String.format(tempStr, "event", i.event));
                if (!i.responseType.isEmpty())
                {
                    for (String r : i.responseType)
                    {
                        sbInfoItem.append(String.format(tempStr, "responseType", r));
                    }
                }
                sbInfoItem.append(String.format(tempStr, "urgency", i.urgency));
                sbInfoItem.append(String.format(tempStr, "severity", i.severity));
                sbInfoItem.append(String.format(tempStr, "certainty", i.certainty));
                StringUtility.SetCotent(sbInfoItem, "audience", i.audience);
                if (!i.eventCode.isEmpty())
                {
                    for (EventCode e : i.eventCode)
                    {
                        String contentString = String.format(tempStr, "valueName", e.valueName) + String.format(tempStr, "value", e.value);
                        sbInfoItem.append(String.format(tempStr, "eventCode", contentString));
                    }
                }
                StringUtility.SetCotent(sbInfoItem, "effective", i.effective);
                StringUtility.SetCotent(sbInfoItem, "onset", i.onset);
                StringUtility.SetCotent(sbInfoItem, "expires", i.expires);
                StringUtility.SetCotent(sbInfoItem, "senderName", i.senderName);
                StringUtility.SetCotent(sbInfoItem, "headline", i.headline);
                StringUtility.SetCotent(sbInfoItem, "description", i.description);
                StringUtility.SetCotent(sbInfoItem, "instruction", i.instruction);
                StringUtility.SetCotent(sbInfoItem, "web", i.web);
                StringUtility.SetCotent(sbInfoItem, "contact", i.contact);
                StringUtility.SetCotent(sbInfoItem, "headline", i.headline);
                if (!i.parameter.isEmpty())
                {
                    for (Parameter p : i.parameter)
                    {
                        String contentString = String.format(tempStr, "valueName", p.valueName) + String.format(tempStr, "value", p.value);
                        sbInfoItem.append(String.format(tempStr, "parameter", contentString));
                    }
                }

                // resource
                StringBuilder sbResource = new StringBuilder();
                if (!i.resource.isEmpty())
                {
                    StringBuilder sbResourceItem = new StringBuilder();
                    for (Resource r : i.resource)
                    {
                        StringUtility.SetCotent(sbResourceItem, "resourceDesc", r.resourceDesc);
                        StringUtility.SetCotent(sbResourceItem, "mimeType", r.mimeType);
                        StringUtility.SetCotent(sbResourceItem, "size", String.valueOf(r.size));
                        StringUtility.SetCotent(sbResourceItem, "uri", r.uri);
                        StringUtility.SetCotent(sbResourceItem, "derefUri", r.derefUri);
                        StringUtility.SetCotent(sbResourceItem, "digest", r.digest);
                        sbResourceItem.append(String.format("<%1$s>%2$s</%1$s>", "resource", sbResourceItem.toString()));
                    }
                    sbResource.append(sbResourceItem.toString());
                }
                sbInfoItem.append(sbResource.toString());
                
                // area
                if (!i.area.isEmpty())
                {
                    StringBuilder sbAreaItem = new StringBuilder();
                    for (Area a : i.area)
                    {
                    	StringBuilder sbAreaPolygon = new StringBuilder();
                        StringUtility.SetCotent(sbAreaPolygon, "areaDesc", a.areaDesc);
                        if (!a.polygon.isEmpty())
                        {
                            for (String p : a.polygon)
                            {
                            	StringUtility.SetCotent(sbAreaItem, "polygon", p);
                            }
                        }
                        if (!a.circle.isEmpty())
                        {
                            for (String c : a.circle)
                            {
                            	StringUtility.SetCotent(sbAreaItem, "circle", c);
                            }
                        }
                        if (!a.geocode.isEmpty())
                        {
                            for (Geocode g : a.geocode)
                            {
                            	sbAreaPolygon.append(String.format("<%1$s>", "geocode"));
                            	sbAreaPolygon.append(String.format("<%1$s>%2$s</%1$s><%3$s>%4$s</%3$s>", "valueName", g.valueName, "value", g.value));
                            	sbAreaPolygon.append(String.format("</%1$s>", "geocode"));
                            }
                        }
                        StringUtility.SetCotent(sbAreaItem, "altitude", String.valueOf(a.altitude));
                        StringUtility.SetCotent(sbAreaItem, "ceiling", String.valueOf(a.ceiling));
                        StringUtility.SetCotent(sbAreaItem, "area", sbAreaPolygon.toString());
                    }
                    sbInfoItem.append(sbAreaItem.toString());
                }
                
                sbInfo.append(String.format("<%1$s>%2$s</%1$s>", "info", sbInfoItem.toString()));
            }
        }
        result = StringUtility.ReplaceContent(result, "info", sbInfo.toString());

        return result;
    }
    
    private void perform(Document xDocument)
    {
        xmlns = xDocument.getDocumentElement().getAttribute("xmlns");
        NodeList rootElements = xDocument.getDocumentElement().getChildNodes();
        Element element = xDocument.getDocumentElement();
        identifier = getValue("identifier", element);
        sender = getValue("sender", element);
        sent = getValue("sent", element);
        status = getValue("status", element);
        msgType = getValue("msgType", element);
        if (element.getElementsByTagName("source").getLength() > 0)
            source = element.getElementsByTagName("source").item(0).getNodeValue();
        if (element.getElementsByTagName("scope").getLength() > 0)
            scope = element.getElementsByTagName("scope").item(0).getNodeValue();
        if (element.getElementsByTagName("restriction").getLength() > 0)
            restriction = element.getElementsByTagName("restriction").item(0).getNodeValue();
        if (element.getElementsByTagName("addresses").getLength() > 0)
            addresses = element.getElementsByTagName("addresses").item(0).getNodeValue();
        if (element.getElementsByTagName("code").getLength() > 0) {
            for (int i = 0; i < element.getElementsByTagName("code").getLength(); i++)
            {
                code.add(element.getElementsByTagName("code").item(i).getNodeValue());
            }
        }
        if (element.getElementsByTagName("note").getLength() > 0)
            note = element.getElementsByTagName("note").item(0).getNodeValue();
        if (element.getElementsByTagName("references").getLength() > 0)
            references = element.getElementsByTagName("references").item(0).getNodeValue();
        if (element.getElementsByTagName("incidents").getLength() > 0)
            incidents = element.getElementsByTagName("incidents").item(0).getNodeValue();
        if (element.getElementsByTagName("info").getLength() > 0)
        {
        	NodeList infoNodes = element.getElementsByTagName("info");
            for (int j = 0; j < infoNodes.getLength(); j++)
            {;
                Info i = new Info();
                Element infoElement = (Element) infoNodes.item(j);
                if (infoElement.getElementsByTagName("language").getLength() > 0)
                	i.language = getValue("language", infoElement);
                for (int k = 0; k < infoElement.getElementsByTagName("category").getLength(); k++)
                { 
                    i.category.add(infoElement.getElementsByTagName("category").item(k).getNodeValue());
                }
                i.event = getValue("event", infoElement);
                if (infoElement.getElementsByTagName("responseType").getLength() > 0)
                {
                    for (int k = 0; k < infoElement.getElementsByTagName("responseType").getLength(); k++)
                    {
                        i.responseType.add(infoElement.getElementsByTagName("responseType").item(k).getNodeValue());
                    }
                }
                i.urgency = getValue("urgency", infoElement);
                i.severity = getValue("severity", infoElement);
                i.certainty = getValue("certainty", infoElement);
                if (infoElement.getElementsByTagName("audience").getLength() > 0)
                    i.audience = getValue("audience", infoElement);
                if (infoElement.getElementsByTagName("eventCode").getLength() > 0)
                {
                    for (int k = 0; k < infoElement.getElementsByTagName("eventCode").getLength(); k++)
                    {
                    	Element eventCodeElement = (Element) infoElement.getElementsByTagName("eventCode").item(k);
                        EventCode eventCodeObj = new EventCode(
                        		eventCodeElement.getElementsByTagName("valueName").item(0).getNodeValue(),
                        		eventCodeElement.getElementsByTagName("value").item(0).getNodeValue());
                        i.eventCode.add(eventCodeObj);
                    }
                }
                if (infoElement.getElementsByTagName("effective").getLength() > 0)
                	i.effective = getValue("effective", infoElement);
                if (infoElement.getElementsByTagName("onset").getLength() > 0)
                	i.onset = getValue("onset", infoElement);
                if (infoElement.getElementsByTagName("expires").getLength() > 0)
                	i.expires = getValue("expires", infoElement);
                if (infoElement.getElementsByTagName("senderName").getLength() > 0)
                	i.senderName = getValue("senderName", infoElement);
                if (infoElement.getElementsByTagName("headline").getLength() > 0)
                	i.headline = getValue("headline", infoElement);
                if (infoElement.getElementsByTagName("description").getLength() > 0)
                	i.description = getValue("description", infoElement);
                if (infoElement.getElementsByTagName("instruction").getLength() > 0)
                	i.description = getValue("instruction", infoElement);
                if (infoElement.getElementsByTagName("web").getLength() > 0)
                	i.web = getValue("web", infoElement);
                if (infoElement.getElementsByTagName("contact").getLength() > 0)
                	i.contact = getValue("contact", infoElement);
                if (infoElement.getElementsByTagName("parameter").getLength() > 0)
                {
                	NodeList parameterNodes = infoElement.getElementsByTagName("parameter");
                	for (int k = 0; k < parameterNodes.getLength(); k++)
                	{
                		Element parameterElement = (Element) parameterNodes.item(k);
	                    Parameter parameterObj = new Parameter(
	                    		parameterElement.getElementsByTagName("valueName").item(0).getNodeValue(),
	                    		parameterElement.getElementsByTagName("value").item(0).getNodeValue());
	                    i.parameter.add(parameterObj);
                	}
                }
                if (infoElement.getElementsByTagName("resource").getLength() > 0)
                {
                	NodeList resourceNodes = infoElement.getElementsByTagName("resource");
                    for (int k = 0; k < resourceNodes.getLength(); k++)
                    {
                		Element resourceElement = (Element) resourceNodes.item(k);
                        Resource resourceObj = new Resource(resourceElement.getElementsByTagName("resourceDesc").item(0).getNodeValue());
                        for (String resourceElementName : new ArrayList<String>(Arrays.asList("mimeType", "size", "uri", "derefUri", "digest")))
                        {
                            if (resourceElement.getElementsByTagName(resourceElementName).getLength() > 0) {
                            	try {
									resourceObj.getClass().getDeclaredField(resourceElementName).set(resourceObj, resourceElement.getElementsByTagName(resourceElementName).item(0).getNodeValue());
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NoSuchFieldException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (SecurityException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (DOMException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
                            }
                        }
                        i.resource.add(resourceObj);
                    }
                }
                if (infoElement.getElementsByTagName("area").getLength() > 0)
                {
                	NodeList areaNodes = infoElement.getElementsByTagName("area");
                    for (int k = 0; k < areaNodes.getLength(); k++)
                    {
                        Element areaElement = (Element) areaNodes.item(k);
                        Area areaObj = new Area(areaElement.getElementsByTagName("areaDesc").item(0).getNodeValue());
                        if (areaElement.getElementsByTagName("geocode").getLength() > 0)
                        {
                        	NodeList geocodeNodes = areaElement.getElementsByTagName("geocode");
                            for (int l = 0; l < geocodeNodes.getLength(); l++)
                            {
                            	Element geocodeElement = (Element) geocodeNodes.item(0);
                                Geocode geocodeObj = new Geocode(
	                        		geocodeElement.getElementsByTagName("valueName").item(0).getNodeValue(),
	                        		geocodeElement.getElementsByTagName("value").item(0).getNodeValue());
                                areaObj.geocode.add(geocodeObj);
                            }
                        }
                        if (areaElement.getElementsByTagName("polygon").getLength() > 0)
                        {
                        	NodeList polygonNodes = areaElement.getElementsByTagName("polygon");
                            for (int l = 0; l < polygonNodes.getLength(); l++)
                            {
                                areaObj.polygon.add(polygonNodes.item(l).getNodeValue());
                            }
                        }
                        if (areaElement.getElementsByTagName("circle").getLength() > 0)
                        {
                        	NodeList circleNodes = areaElement.getElementsByTagName("circle");
                            for (int l = 0; l < circleNodes.getLength(); l++)
                            {
                                areaObj.polygon.add(circleNodes.item(0).getNodeValue());
                            }
                        }
                        for (String areaElementName : new ArrayList<String>(Arrays.asList("altitude", "ceiling")))
                        {
                            if (areaElement.getElementsByTagName(areaElementName).getLength() > 0) {
                            	try {
									Area.class.getDeclaredField(areaElementName).set(areaObj, areaElement.getElementsByTagName(areaElementName).item(0).getNodeValue());
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (NoSuchFieldException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (SecurityException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (DOMException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
                            }
                        }
                        i.area.add(areaObj);
                    }
                }
                this.info.add(i);
            }
        }
    }

	private static String getValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}
	
	private static boolean isNull(String str) {
        return str == null;
    }
	
	private static boolean isNullOrBlank(String param) {
        if (isNull(param) || param.trim().length() == 0) {
            return true;
        }
        return false;
    }
	
	private static String join(String p, ArrayList<String> list){
		StringBuilder sb = new StringBuilder();
		for (String s : list)
		{
		    sb.append(s);
		    sb.append(p);
		}
		return sb.toString();
	}
}
