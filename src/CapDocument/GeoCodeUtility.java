package CapDocument;

import java.io.*;

public class GeoCodeUtility {
	/**
	 * 取得Polygon
	 * @param geocode geocode
	 * @param type type
	 * @return Polygon字串
	 */
	public static String GetPolygon(String geocode, String type)
    {
        String path = new Config().getProperty("geocodePath");
        System.out.println(path);
        File f = new File(path);
    	String strLine = "";
        switch (type)
        {
            case "gml":
            	File[] gmlDs = f.listFiles(new FilenameFilter() {
				    public boolean accept(File dir, String name) {
				        return name.startsWith("gml");
				    }
            	});
                File gmlD = gmlDs[0];
                File[] gmlFs = gmlD.listFiles();
                for (File sf: gmlFs)
                {
                    if (sf.getName().startsWith(geocode + "_"))
                    {
                    	FileInputStream fstream = null;
						try {
							fstream = new FileInputStream(sf.getAbsolutePath());
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    	DataInputStream in = new DataInputStream(fstream);
                    	BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    	try {
							strLine = br.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    	try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        return strLine;
                    }
                }
                return "";
            case "kml":
            	File[] kmlDs = f.listFiles(new FilenameFilter() {
				    public boolean accept(File dir, String name) {
				        return name.startsWith("kml");
				    }
            	});
                File kmlD = kmlDs[0];
                File[] kmlFs = kmlD.listFiles();
                for (File sf: kmlFs)
                {
                    if (sf.getName().startsWith(geocode + "_"))
                    {
                    	FileInputStream fstream = null;
						try {
							fstream = new FileInputStream(sf.getAbsolutePath());
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    	DataInputStream in = new DataInputStream(fstream);
                    	BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    	try {
							strLine = br.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    	try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        return strLine;
                    }
                }
                return "";
            default:
                return "";
        }
    }
}
