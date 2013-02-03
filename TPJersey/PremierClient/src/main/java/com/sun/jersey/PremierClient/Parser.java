package com.sun.jersey.PremierClient;

import java.util.List;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Parser {

	private DocumentBuilder docBuilder;
	private Document docXML;

	public Parser(String contenuXML){
		DocumentBuilderFactory builderFactory =
				DocumentBuilderFactory.newInstance();
		try {
			docBuilder = builderFactory.newDocumentBuilder();
		} 
		catch (ParserConfigurationException e) {
			e.printStackTrace();  
		}

		try {
			docXML = docBuilder.parse(new ByteArrayInputStream(contenuXML.getBytes()));
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Coup> getCoups(){
		NodeList listeCoupsXML = docXML.getElementsByTagName("move");
		List<Coup> l = new ArrayList<Coup>();
		int nbMoves = listeCoupsXML.getLength();

		for(int i=0; i<nbMoves; i++){
			Node coup = listeCoupsXML.item(i);
			NodeList params = coup.getChildNodes();
			if(params instanceof Element){
				Element paramsCoup = (Element)params;
				String nom = new String();
				int nombre = -1;
				float pourcentage = -1;
				

				nom = getTagValue("name", paramsCoup);
				nombre = Integer.parseInt(getTagValue("number", paramsCoup));
				pourcentage = Float.parseFloat(getTagValue("probability", paramsCoup));
				
				try {
					l.add(new Coup(nom, nombre, pourcentage));
				} catch (CoupInvalideException e) {
					e.printStackTrace();
				}
			}

		}

		return l;
	}
	
	  private static String getTagValue(String tag, Element e) {
			NodeList nl = e.getElementsByTagName(tag).item(0).getChildNodes();
		 
		        Node value = (Node) nl.item(0);
		 
			return value.getNodeValue();
		  }

}
