package CapDocument;

public class StringUtility {
	/**
	 * 設定內容
	 * @param sb 字串容器
	 * @param tagName Tag名稱
	 * @param content 內容
	 * @return 處裡完成字串
	 */
	public static StringBuilder SetCotent(StringBuilder sb, String tagName, String content)
    {
        if (!isNullOrBlank(content))
            sb.append(String.format("<%1$s>%2$s</%1$s>", tagName, content));
        return sb;
    }

	/**
	 * 置換內容
	 * @param temp 置換字串
	 * @param target 目標變數
	 * @param content 內容
	 * @return 處裡完成字串
	 */
    public static String ReplaceContent(String temp, String target, String content)
    {
        return temp.replaceAll(String.format("\\{\\{%1$s\\}\\}", target), content);
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
}
