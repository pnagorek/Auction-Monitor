package com.nagorek.auctionmonitor.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AuctionMonitorEngine {

	public static final String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
	public static final String url = "https://www.allegro.pl";
	
	private Map<String, String> departments = new HashMap<>();
	private Document doc;
	
	public AuctionMonitorEngine() throws IOException{

		Connection.Response res = Jsoup.connect(url)
				.userAgent(userAgent)
				.method(Method.GET)
				.execute();
		doc = res.parse();	
		mapDepartments();
		System.out.println("**Visiting page: " + doc.location() + "**\n");				
	}
	
	public Elements quickSearch(String phrase) throws Exception{

		Element form = doc.getElementById("main-search");
		String[] tab = form.toString().split(" ");

		Method method = findData(tab, "method").toUpperCase().equals("GET") ? Method.GET : Method.POST;

		String actionUrl = "https:" + findData(tab, "action");

		form = doc.getElementById("main-search-text");
		tab = form.toString().split(" ");

		String name = findData(tab, "name");

		Connection.Response res = Jsoup.connect(actionUrl)
				.userAgent(userAgent)
				.data(name, phrase)
				.method(method)
				.execute();
		doc = res.parse();	
		System.out.println("Result page: " + doc.location() + "\n");
		
		Elements resultLinks = doc.select("h2 > a[href]:not(.itemprop)");
		/*for(Element s : resultLinks){
			System.out.println(s);
		}*/
		return resultLinks;
	}
	
	private void mapDepartments(){
		Elements links = doc.getElementsByClass("nav-link");
		String linkHref, linkText;
		for (Element link : links) {
			  linkHref = link.attr("href");
			  linkText = link.text();
			  if(linkHref.contains("dzial")){				 
				 departments.put(linkText, linkHref);
			  }		  			  
		}	
	}
	
	public String grabData(String url){
		Connection.Response res;
		StringBuilder sb = new StringBuilder("");
		try {
			res = Jsoup.connect(url)
					.userAgent(userAgent)
					.method(Method.GET)
					.execute();
			Document doc = res.parse();
			Element data = doc.select("h1 > small").first();
			sb.append(data.text().substring(1, data.text().length() - 1) + "<>");		
			data = doc.select("h1.title").first();
			sb.append(data.text() + "<>");
			if(doc.select("div:matchesOwn(cena kup teraz)").first() != null){
				data = doc.select("div:matchesOwn(cena kup teraz)").first().nextElementSibling();
				sb.append(data.text() + "<>");
			} else {
				sb.append("-<>");
			}
			
			if(doc.select("div:matchesOwn(aktualna cena)").first() != null){
				data = doc.select("div:matchesOwn(aktualna cena)").first().nextElementSibling();
				sb.append(data.text() + "<>");
			} else {
				sb.append("-<>");
			}
			data = doc.select("span:matchesOwn(nowy|używany)").first();
			sb.append(data.text() + "<>");
			data = doc.select("li:contains(lokalizacja)").first();
			sb.append(data.text().substring("lokalizacja: ".length(), data.text().length()) + "<>");
			sb.append("-<>");
			sb.append("-<>");
			data = doc.select("div.list").first(); //zdjecie
			sb.append(findData(data.toString().split(" "), "src") + "<>");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();


	}
	
	public String[] getDepartments(){
		
		String[] tab = new String[departments.size()+1];
		tab[0] = "All";
		int i = 1;
		for(String s : departments.keySet()){
			tab[i] = s;
			i++;
		}
		return tab;				
	}
	
	public String getLink(String depo){		
		return departments.get(depo);
	}
	
	
	private String findData(String[] values, String phrase){
		
		for(int i = 0; i < values.length; i++){
			if(values[i].startsWith(phrase)){
				return values[i].substring(phrase.length() + 2, values[i].length() - 1);
			}
		}
		
		return "result not found";
	}

}
