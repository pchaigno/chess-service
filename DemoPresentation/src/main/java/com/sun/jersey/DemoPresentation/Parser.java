package com.sun.jersey.DemoPresentation;

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

	public List<Move> getMoves(){
		NodeList listeMovesXML = docXML.getElementsByTagName("move");
		List<Move> l = new ArrayList<Move>();
		int nbMoves = listeMovesXML.getLength();

		for(int i=0; i<nbMoves; i++){
			Node Move = listeMovesXML.item(i);
			NodeList params = Move.getChildNodes();
			if(params instanceof Element){
				Element paramsMove = (Element)params;
				String nom = new String();
				int nombre = -1;
				float pourcentage = -1;
				

				nom = getTagValue("name", paramsMove);
				nombre = Integer.parseInt(getTagValue("number", paramsMove));
				pourcentage = Float.parseFloat(getTagValue("probability", paramsMove));
				
				try {
					l.add(new Move(nom, nombre, pourcentage));
				} catch (InvalidMoveException e) {
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
