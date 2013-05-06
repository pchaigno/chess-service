package tests;

import org.json.JSONArray;
import org.json.JSONObject;

import core.MoveSuggestion;
import core.OpeningSuggestion;

import junit.framework.TestCase;

/**
 * Few tests of JSON parsing.
 * @author Paul Chaignon
 */
public class TestJSONParser extends TestCase {

	public void test() {
		String response = "[{\"move\":\"d5\",\"probatowin\":0,\"probatonull\":0.667,\"nb\":3},{\"move\":\"h6\",\"probatowin\":0.667,\"probatonull\":0,\"nb\":3},{\"move\":\"Qa5\",\"probatowin\":0.25,\"probatonull\":0.5,\"nb\":8},{\"move\":\"Qb6\",\"probatowin\":0,\"probatonull\":0,\"nb\":2}]";
		JSONArray jsonArray = new JSONArray(response);
		for(int i=0 ; i<jsonArray.length() ; i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			String move = json.getString("move");
			double probaWin = json.getDouble("probatowin");
			int nb = json.getInt("nb");
			double probaDraw = json.getDouble("probatonull");
			MoveSuggestion suggest = new OpeningSuggestion(move, nb, probaWin, probaDraw);
			System.out.println(suggest);
		}
	}
}