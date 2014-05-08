package CapDocument;

import java.util.ArrayList;

public class Area {
	/**
	 * 區域描述
	 */
	public String areaDesc;
	/**
	 * 多邊形各點的坐標
	 */
    public ArrayList<String> polygon = new ArrayList<String>();
    /**
     * 中心點坐標及半徑
     */
    public ArrayList<String> circle = new ArrayList<String>();
    /**
     * 區域代碼
     */
    public ArrayList<Geocode> geocode = new ArrayList<Geocode>();
    /**
     * 高度
     */
    public int altitude;
    /**
     * 區域的最高高度值
     */
    public int ceiling;

    public Area(String areaDesc)
    {
        this.areaDesc = areaDesc;
    }
}
