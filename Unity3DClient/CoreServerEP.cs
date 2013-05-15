// *******************************************************************************
// *                                                                             *
// *            Etude Pratique - Chess Services - INSA 3INFO - 2013              *
// *                   Client Side - Call to Core Server                         *
// *                                                                             *
// *******************************************************************************

using UnityEngine;
using System.Collections;
using System.IO;
using System.Net;
using System.Text;

public class CoreServerEP : MonoBehaviour {
	public string gameId;				// Unique game Id allocated by the core server
	public string url;					// Adress of the server
	public string requestFEN;			// FEN representation of the board
	public string lastRequestFEN;		// Prior FEN representation
	public int levelDeep;				// Strength of search
	public string answer;				// Best move to pplay giving back by the server

	public enum EngineStatus { Initializing, Sleep, SendRequest, GetResponse, Error, Timeout, Ended };
	public EngineStatus status;
	public bool accessible;				// True if CoreServer is available

	public string lastRequestAnswer;
	public float lastRequestTime = 0;
	public float lastRequestDuration = 0;
	public float requestTimeout = 0;	// Time to wait before launch a timeout exception

	
	// Test WWW (renvoi contenu page)
	void Start() {
		gameId = "";
		status = EngineStatus.Initializing;
		accessible = false;

		using (StreamReader sr = new StreamReader("config.ini"))
		{
			while (sr.Peek() >= 0)
			{
				url = sr.ReadLine();
			}
			//Debug.Log(url);
		}

		StartCoroutine(SendRequest("POST", url));
	}


	string ConvertFEN(string fen)
	{
		fen = fen.Replace("/", "$").TrimEnd(); // Remove slash
		fen = fen.Replace(" ", "%20");
		fen = fen.Replace("QKqk", "KQkq"); // Small hack to handle incorrect FEN
		return fen;
	}


	void Update()
	{
		if (!accessible) return;

		switch (status)
		{
			case EngineStatus.Sleep:
				if (requestFEN.Length > 0)
				{
					status = EngineStatus.SendRequest;
					StartCoroutine(SendRequest("GET", url + gameId + "/" + ConvertFEN(requestFEN)));
					lastRequestFEN = requestFEN;
					requestFEN = "";
				}
				break;

			case EngineStatus.SendRequest:
				// Just wait for answer from CoreServer
				// TODO : add timeout
				break;

			case EngineStatus.GetResponse:
				if (answer.Length > 0)
				{
					Debug.Log("Response from CoreServer : " + answer);
					((TextMesh)GetComponent(typeof(TextMesh))).text = answer;
					(GameObject.Find("MainScript")).SendMessage("EngineAnswer", answer);
					answer = "";
					status = EngineStatus.Sleep;
				}
				break;

			case EngineStatus.Error:
				Debug.Log("No response from CoreServer : you may check URI");
				accessible = false;
				LetKnow3D();
				break;

			case EngineStatus.Ended:
				accessible = false;
				LetKnow3D();
				break;

			default:
				Debug.LogWarning("Update status CoreServer not implemented : "+status.ToString());
				break;
		}
	}



	// Send a request to CoreServer
	// -------------------------------------------------------------
	IEnumerator SendRequest(string method, string uri)
	{
		Debug.Log("SendRequest : " + uri);

		// Launch request
		HttpWebRequest httpWebRequest = (HttpWebRequest)WebRequest.Create(uri);
		httpWebRequest.Method = method;

		// Encode SAN info to create the new game
		if (method == "POST") {
			ASCIIEncoding encoding = new ASCIIEncoding();
			byte[] byte1 = encoding.GetBytes("san=false");
			httpWebRequest.ContentType = "application/x-www-form-urlencoded";
			httpWebRequest.ContentLength = byte1.Length;
			Stream newStream = httpWebRequest.GetRequestStream();
			newStream.Write(byte1, 0, byte1.Length);
		}

		// Get the response from the server
		HttpWebResponse response = (HttpWebResponse)httpWebRequest.GetResponse();
		Stream receiveStream = response.GetResponseStream();
		StreamReader readStream = new StreamReader(receiveStream);

		switch (method) {
			// Create a new game
			case "POST":
				gameId = readStream.ReadToEnd();
				Debug.Log("New game id allocated : " + gameId);
				accessible = true;
				LetKnow3D();
				break;

			// Request for the best move
			case "GET":
				answer = readStream.ReadToEnd();
				if (answer == "NULL") {
					status = EngineStatus.Error;
					yield break; // <=> return;
				}
				GetResponse();
				break;

			// End game
			case "DELETE":
				Debug.Log("Game '" + gameId + "' deleted");
				status = EngineStatus.Ended;
				break;
		}

		response.Close();
		readStream.Close();
		
		yield break;
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
		requestFEN = FENs;
	}


	// Close game connexion on server
	// -------------------------------------------------------------
	void OnApplicationQuit() {
		if (gameId.Length > 0) {
			if (requestFEN.Length > 0) {
				lastRequestFEN = requestFEN;
			}
			SendRequest("DELETE", url + gameId + "/" + lastRequestFEN);
		}
	}

	void OnDestroy() {
		OnApplicationQuit();
	}


	// Tell to the 3D client the availability of Core Server
	// -------------------------------------------------------------
	void LetKnow3D()
	{
		(GameObject.Find("MainScript")).SendMessage("CoreServerAccess", (accessible ? "YES" : "NO"));
		if (accessible)
		{
			status = EngineStatus.Sleep; requestFEN = ""; answer = "";
		}
		else
		{
			status = EngineStatus.Error; requestFEN = ""; answer = "";
		}
	}
}
