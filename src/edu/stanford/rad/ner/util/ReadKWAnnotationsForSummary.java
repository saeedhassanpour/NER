package edu.stanford.rad.ner.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
 
public class ReadKWAnnotationsForSummary {
	public Map<String, KWAnnotation> read(String XmlfileName) {
 
	  SAXBuilder builder = new SAXBuilder();
	  File xmlFile = new File(XmlfileName);
	  Map<String, KWAnnotation> KWAnnotations = new HashMap<String, KWAnnotation>();
 
	  try {
 
		Document document = (Document) builder.build(xmlFile);
		Element rootNode = document.getRootElement();
		List<Element> list = rootNode.getChildren("annotation");
		for (int i = 0; i < list.size(); i++) {
 
		   Element node = list.get(i);
 
		   
		   String mention = node.getChild("mention").getAttributeValue("id");
		   String annotator = node.getChildText("annotator");
		   int start = Integer.parseInt(node.getChild("span").getAttributeValue("start"));
		   int end = Integer.parseInt(node.getChild("span").getAttributeValue("end"));
		   String spannedText = node.getChildText("spannedText");
		   String creationDate = node.getChildText("creationDate");
		   
		   KWAnnotations.put(mention, new KWAnnotation(mention, annotator, start, end, spannedText, creationDate));
		   //System.out.println("mention: " + node.getChild("mention").getAttributeValue("id"));
		   //System.out.println("annotator: " + node.getChildText("annotator"));
		   //System.out.println("start: " + node.getChild("span").getAttributeValue("start"));
		   //System.out.println("end: " + node.getChild("span").getAttributeValue("end"));
		   //System.out.println("spannedText : " + node.getChildText("spannedText"));
		   //System.out.println("creationDate : " + node.getChildText("creationDate"));
		   //System.out.println("-----------------------------------------------------");
 
		}
		
		List<Element> list2 = rootNode.getChildren("classMention");
		for (int i = 0; i < list2.size(); i++) {
 
		   Element node = list2.get(i);
 
		   String mention = node.getAttributeValue("id");
		   String mentionClass = node.getChild("mentionClass").getAttributeValue("id");
		   String mentionspan = node.getChildText("mentionClass");
		   

		   KWAnnotation kw = KWAnnotations.get(mention);
		   if(!kw.spannedText.equals(mentionspan)){
			   System.out.println(kw.spannedText + " does not match " + mentionspan);
		   }
		   kw.mentionClass = mentionClass;
		   KWAnnotations.put(mention, kw);


		   
//		   System.out.println("mention: " + node.getAttributeValue("id"));
//		   System.out.println("mentionClass: " + node.getChild("mentionClass").getAttributeValue("id"));
//		   System.out.println("mentionspan: " + node.getChildText("mentionClass"));
//		   System.out.println("-----------------------------------------------------");
 
		}
		
	  } catch (IOException io) {
		System.out.println(io.getMessage());
	  } catch (JDOMException jdomex) {
		System.out.println(jdomex.getMessage());
	  }
	  
	  return KWAnnotations;
	}
}