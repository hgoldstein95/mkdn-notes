package util;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Sorter {

	public String makeOutline(String html) {
		
		StringBuilder outline = new StringBuilder();
		
		Document doc = Jsoup.parse(html);
		List<Element> elems = doc.getAllElements();
		for(Element elem: elems) {
			switch(elem.tagName()) {
			case "h1":
				outline.append("<ul><li>" + elem.text() + 
						"</li></ul>");
				break;
			case "h2":
				outline.append("<ul><ul><li>" + elem.text() + 
						"</li></ul></ul>");
				break;
			case "h3":
				outline.append("<ul><ul><ul><li>" + elem.text() + 
						"</li></ul></ul></ul>");
				break;
			case "h4":
				outline.append("<ul><ul><ul><ul><li>" + elem.text() + 
						"</li></ul></ul></ul></ul>");
				break;
			case "h5":
				outline.append("<ul><ul><ul><ul><ul><li>" + elem.text() + 
						"</li></ul></ul></ul></ul></ul>");
				break;
			case "h6":
				outline.append("<ul><ul><ul><ul><ul><ul><li>" + elem.text() + 
						"</li></ul></ul></ul></ul></ul></ul>");
				break;
			}
		}
		return outline.toString();
	}
	
	public String sortCode(String html) {
		
		StringBuilder code = new StringBuilder();
		
		Document doc = Jsoup.parse(html);
		List<Element> elems = doc.getAllElements();
		code.append("<ol>");
		for(Element elem: elems) {
			if(elem.tagName().equals("pre") && elem.child(0).tagName().equals("code")) {
				code.append("<li><pre>" + elem.toString() + "</pre></li>");
			}
		}
		code.append("</ol>");
		
		return code.toString();
	}
	
	public String sortMath(String html) {
		
		StringBuilder eqn = new StringBuilder();

		Document doc = Jsoup.parse(html);
		List<Element> elems = doc.getAllElements();
		eqn.append("<ol>");
		for(Element elem: elems) {
			if(elem.tagName().equals("span") && elem.className().equals("eqn")) {
				eqn.append("<li>" + elem.toString() + "</li>");
			}
		}
		eqn.append("</ol>");
		
		return eqn.toString();
	}
	
	public String sortDefinition(String html) {
		
		StringBuilder defn = new StringBuilder();

		Document doc = Jsoup.parse(html);
		List<Element> elems = doc.getAllElements();
		for(Element elem: elems) {
			if(elem.tagName().equals("dl")) {
				defn.append(elem.toString());
			}
		}
		
		return defn.toString();
	}
	
	public String sortQuote(String html) {
		
		StringBuilder quote = new StringBuilder();

		Document doc = Jsoup.parse(html);
		List<Element> elems = doc.getAllElements();
		for(Element elem: elems) {
			if(elem.tagName().equals("blockquote")) {
				quote.append(elem.toString());
			}
		}
		
		return quote.toString();
	}
	
	public String sortTheorem(String html) {
		
		StringBuilder thm = new StringBuilder();

		Document doc = Jsoup.parse(html);
		List<Element> elems = doc.getAllElements();
		thm.append("<ol>");
		for(Element elem: elems) {
			if(elem.tagName().equals("span") && elem.className().equals("thm")) {
				thm.append("<li>" + elem.toString() + "</li>");
			}
		}
		thm.append("</ol>");
		
		return thm.toString();
	}
}
