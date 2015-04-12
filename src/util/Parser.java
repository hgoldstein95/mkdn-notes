package util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

public class Parser {
	
	PegDownProcessor process;
	
	public Parser() throws IOException {
		process = new PegDownProcessor(	
				Extensions.FENCED_CODE_BLOCKS + 
			   	Extensions.DEFINITIONS +
			   	Extensions.QUOTES +
			   	Extensions.AUTOLINKS +
			   	Extensions.TABLES +
			   	Extensions.SMARTYPANTS
		);
	}
	
	public String parse(String markdown) {
		
		String head = "<!DOCTYPE HTML>\r\n" + 
					"<html>\r\n" + 
					"<head>\r\n" + 
					"<meta charset=\"utf-8\"/>\r\n" + 
					"<title>\r\n" + 
					"an\\note\r\n" + 
					"</title>\r\n" + 
					"<link href=\"http://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.1/styles/github.min.css\" rel=\"stylesheet\"/>\r\n" + 
					"<script src=\"http://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.1/highlight.min.js\">\r\n" + 
					"</script>\r\n" + 
					"<script>\r\n" + 
					"hljs.initHighlightingOnLoad();\r\n" + 
					"</script>\r\n" + 
					"<style type=\"text/css\">\r\n" + 
					"body{font:16px Helvetica,Arial,sans-serif;line-height:1.4;color:#333;word-wrap:break-word;background-color:#fff;padding:10px 15px}" +
					"strong{font-weight:700}h1{font-size:2em;margin:.67em 0;text-align:center}h2{font-size:1.75em}h3{font-size:1.5em}h4{font-size:1.25em}" +
					"h1,h2,h3,h4,h5,h6{font-weight:700;position:relative;margin-top:15px;margin-bottom:15px;line-height:1.1}" +
					"h1,h2{border-bottom:1px solid #eee}hr{height:0;margin:15px 0;overflow:hidden;background:0 0;border:0;border-bottom:1px solid #ddd}" +
					"a{color:#4183C4}a.absent{color:#c00}ol,ul{padding-left:15px;margin-left:5px}ol{list-style-type:lower-roman}table{padding:0}" +
					"table tr{border-top:1px solid #ccc;background-color:#fff;margin:0;padding:0}table tr:nth-child(2n){background-color:#aaa}" +
					"table tr th{font-weight:700;border:1px solid #ccc;text-align:left;margin:0;padding:6px 13px}" +
					"table tr td{border:1px solid #ccc;text-align:left;margin:0;padding:6px 13px}table tr td :first-child,table tr th :first-child{margin-top:0}" +
					"table tr td:last-child,table tr th :last-child{margin-bottom:0}img{max-width:100%}code{padding:0 5px;background-color:#d3d3d3}" +
					"blockquote{padding: 0 15px;border-left:4px solid #ccc}\r\n" + 
					"</style>\r\n" + 
					"<script type=\"text/javascript\"\r\n" + 
					"src=\"http://www.maths.nottingham.ac.uk/personal/drw/LaTeXMathML.js\">\r\n" + 
					"</script>\r\n" + 
					"</head>\r\n" + 
					"<body>";
		
		String tail = "</body>" +
					"</html>";
		
		return head + process.markdownToHtml(modify(markdown)) + tail;
	}
	
	private String modify(String markdown) {
		
		markdown = markdown.replaceAll(Pattern.quote("<eqn>"), Matcher.quoteReplacement("<span class='eqn'>$"));
		markdown = markdown.replaceAll(Pattern.quote("</eqn>"), Matcher.quoteReplacement("$</span>"));
		
		markdown = markdown.replaceAll(Pattern.quote("<thm>"), Matcher.quoteReplacement("<span class='thm'>"));
		markdown = markdown.replaceAll(Pattern.quote("</thm>"), Matcher.quoteReplacement("</span>"));
		
		return markdown;
	}
}
