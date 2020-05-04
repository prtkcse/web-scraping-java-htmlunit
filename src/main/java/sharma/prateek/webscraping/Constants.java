package sharma.prateek.webscraping;

import java.util.HashMap;

public class Constants {
	
	
	public static HashMap<String,String> numbersMap;

	public static HashMap<String, String> getNumbersmap() {
		setNumbersmap();
		return numbersMap;
	}

	public static void setNumbersmap() {
		numbersMap = new HashMap<>();
		
		numbersMap.put("mobilesv icon-dc","+");
		numbersMap.put("mobilesv icon-fe","(");
		numbersMap.put("mobilesv icon-hg",")");
		numbersMap.put("mobilesv icon-ba","-");
		numbersMap.put("mobilesv icon-ji","9");
		numbersMap.put("mobilesv icon-lk","8");
		numbersMap.put("mobilesv icon-nm","7");
		numbersMap.put("mobilesv icon-po","6");
		numbersMap.put("mobilesv icon-rq","5");
		numbersMap.put("mobilesv icon-ts","4");
		numbersMap.put("mobilesv icon-vu","3");
		numbersMap.put("mobilesv icon-wx","2");
		numbersMap.put("mobilesv icon-yz","1");
		numbersMap.put("mobilesv icon-acb","0");
	}
	
	

}
