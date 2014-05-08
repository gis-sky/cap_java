package CapDocument;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			CapDocument capDocument = new CapDocument("/Users/gary/Desktop/fifows_extremely-rain_201307121028.cap");
			System.out.println(capDocument.ToCap());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
