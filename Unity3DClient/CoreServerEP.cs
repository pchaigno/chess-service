// *******************************************************************************
// *                                                                             *
// *            Etude Pratique - Chess Services - INSA 3INFO - 2013              *
// *                   Client Side - Call to Core Server                         *
// *                                                                             *
// *******************************************************************************

using UnityEngine;
using System.Collections;

public class CoreServerEP : MonoBehaviour {
	public string url;
	public string request_FEN;
	public int levelDeep; // Strength of search
	public string answer;

	public enum EngineStatus { Initializing, Sleep, SendRequest, GetResponse, TimeoutError };
	public EngineStatus status;
	public bool accessible; // True if CoreServer is available

	public string lastRequestAnswer;
	public float lastRequestTime = 0;
	public float lastRequestDuration = 0;
	public float requestTimeout = 0; // Time to wait before launch a timeout exception

	
	// Test WWW (renvoi contenu page)
	void Start() {
		status = EngineStatus.Initializing;
		accessible = false;

		url = "http://localhost/testCS.html";
		StartCoroutine(TestConnexion(url));
	}



	void Update()
	{
		if (!accessible || status == EngineStatus.TimeoutError) return;

		switch (status)
		{
			case EngineStatus.Sleep:
				if (request_FEN.Length > 0)
				{
					status = EngineStatus.SendRequest;
					StartCoroutine(SendRequest(url));// + request_FEN));
					request_FEN = "";
				}
				break;

			case EngineStatus.SendRequest:
				// Just wait for answer from CoreServer
				break;

			case EngineStatus.GetResponse:
				if (answer.Length > 0)
				{
					Debug.Log("Response from CoreServer" + answer);
					((TextMesh)GetComponent(typeof(TextMesh))).text = answer;
					(GameObject.Find("MainScript")).SendMessage("EngineAnswer", answer);
					answer = "";
					status = EngineStatus.Sleep;
				}
				break;

			default:
				Debug.LogWarning("Update status CoreServer not implemented : "+status.ToString());
				break;
		}
	}


	// Send a simple request to CoreServer to check its availability
	// -------------------------------------------------------------
	IEnumerator TestConnexion(string uri)
	{
		WWW www = new WWW(uri);
		yield return www;
		
		// Connexion error => timeout
		if (www.error != null)
		{
			status = EngineStatus.TimeoutError;
			yield break; // <=> return;
		}
		
		accessible = true;
		LetKnow3D();
	}


	// Send a request to CoreServer
	// -------------------------------------------------------------
	IEnumerator SendRequest(string uri)
	{
		WWW www = new WWW(uri);
		yield return www;

		// No response => timeout
		if (www.error != null)
		{
			status = EngineStatus.TimeoutError;
			yield break; // <=> return;
		}

		answer = www.text;
		GetResponse();
	}


	// Get best move from CoreServer
	// -------------------------------------------------------------
	void GetResponse()
	{
		status = EngineStatus.GetResponse;
	}

	
	// Set strenght of search (called from MainScript)
	// -------------------------------------------------------------
	void SetDeepLevel(string st)	
	{
		levelDeep = 3 + (( System.Convert.ToInt32( st ) - 1 ));
	}

	// Set FEN to search (called from MainScript)
	// -------------------------------------------------------------
	void SetRequestFEN(string FENs)
	{
		request_FEN = FENs;
	}


	// Tell to the 3D client the availability of Core Server
	// -------------------------------------------------------------
	void LetKnow3D()
	{
		(GameObject.Find("MainScript")).SendMessage("CoreServerAccess", (accessible ? "YES" : "NO"));
		if (accessible)
		{
			status = EngineStatus.Sleep; request_FEN = ""; answer = "";
		}
		else
		{
			status = EngineStatus.TimeoutError; request_FEN = ""; answer = "";
		}
	}
}
