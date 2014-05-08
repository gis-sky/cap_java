package CapDocument;

import java.util.ArrayList;

public class Info {
	/**
	 * 語言代碼
	 */
	public String language;
	/**
	 * 訊息種類
	 */
    public ArrayList<String> category = new ArrayList<String>();
    /**
     * 事件主題類型描述
     */
    public String event;
    /**
     * 應變代碼
     */
    public ArrayList<String> responseType = new ArrayList<String>();
    /**
     * 緊急代碼
     */
    public String urgency;
    /**
     * 嚴重代碼
     */
    public String severity;
    /**
     * 確定代碼
     */
    public String certainty;
    /**
     * 描述可能對象
     */
    public String audience;
    /**
     * 事件代碼
     */
    public ArrayList<EventCode> eventCode = new ArrayList<EventCode>();
    /**
     * 生效日期與時間
     */
    public String effective;
    /**
     * 預期影響日期與時間
     */
    public String onset;
    /**
     * 到期日期與時間
     */
    public String expires;
    /**
     * 發送者名稱
     */
    public String senderName;
    /**
     * 標題
     */
    public String headline;
    /**
     * 描述
     */
    public String description;
    /**
     * 建議採取應變方案
     */
    public String instruction;
    /**
     * 網頁資訊
     */
    public String web;
    /**
     * 聯絡資訊
     */
    public String contact;
    /**
     * 參數傳遞
     */
    public ArrayList<Parameter> parameter = new ArrayList<Parameter>();
    /**
     * Resource
     */
    public ArrayList<Resource> resource = new ArrayList<Resource>();
    /**
     * Area
     */
    public ArrayList<Area> area = new ArrayList<Area>();
}
