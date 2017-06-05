package com.nagorek.auctionmonitor.model;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class test {

	public static void main(String[] args) throws IOException {
		StringBuilder sb = new StringBuilder();
		Connection.Response res = Jsoup.connect("http://allegro.pl/msi-radeon-rx-470-8gb-ddr5-256bit-hdmi-dp-i6749915353.html")
				.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
				.method(Method.GET)
				.execute();
		Document doc = res.parse();

		//pagination
		/*Element data = doc.select("ul.opbox-pagination").first();
		System.out.println(data);
		System.out.println(findData(data.toString().split(" "), "value"));
		
		res = Jsoup.connect("https://allegro.pl/listing?string=i7+7700k")
				.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
				.data("p", "2")
				.method(Method.GET)
				.execute();
		doc = res.parse();
		System.out.println(doc.location());
		data = doc.select("ul.opbox-pagination").first();
		System.out.println(data);*/

		
		Element data = doc.select("div.list").first();
		String value = findData(data.toString().split(" "), "src");
		System.out.println(value);

	}
	
	private static String findData(String[] values, String phrase){
		
		for(int i = 0; i < values.length; i++){
			if(values[i].startsWith(phrase)){
				return values[i].substring(phrase.length() + 2, values[i].length() - 1);
			}
		}
		
		return "result not found";
	}

}
