package CapDocument;

import java.util.ArrayList;

public class CapDocumentCollection {
	/**
	 * GeoRss ID
	 */
	public String id;
	/**
	 * GeoRss標題
	 */
    public String title;
    /**
     * GeoRss更新時間
     */
    public String updated;
    /**
     * GeoRss名稱
     */
    public String name;
    /**
     * GeoRss連結
     */
    public String link;
    /**
     * Cap物件
     */
    public ArrayList<CapDocument> CapDocuments = new ArrayList<CapDocument>();

    public CapDocumentCollection(String id, String title, String updated, String name, String link)
    {
        this.id = id;
        this.title = title;
        this.updated = updated;
        this.name = name;
        this.link = link;
    }

    /**
     * 讀取Cap檔案
     * @param path Cap檔案路徑
     */
    public void LoadCapFile(String path)
    {
        CapDocument capDocument = new CapDocument();
        try {
			capDocument.Load(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        CapDocuments.add(capDocument);
    }

    /**
     * 轉出GeoRss字串
     * @return GeoRss字串
     */
    public String ToGeoRss()
    {
        StringBuilder sbGeoRss = new StringBuilder();
        StringBuilder sbCap = new StringBuilder();
        if (!CapDocuments.isEmpty())
        {
            for (CapDocument capDocument : CapDocuments)
            {
                sbCap.append(capDocument.ToGeoRssItem());
            }
        }
        sbGeoRss.append(String.format(Templates.geoRssTemplate, id, title, updated, name, link, sbCap.toString()));
        return sbGeoRss.toString();
    }
}
