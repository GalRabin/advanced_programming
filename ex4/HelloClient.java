package ex4;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class HelloClient {
	public static final String ERR_MESSAGE = "IO Error!";
	public static final String LISTEN_MESSAGE = "Listening on port: ";
	public static final String HELLO_MESSAGE = "hello ";
	public static final String BYE_MESSAGE = "bye";
	Socket clientSocket;

	public static final int COUNT = 10;

	/**
	 * Connect to a remote host using TCP/IP and set {@link #clientSocket} to be the
	 * resulting socket object.
	 * 
	 * @param host remote host to connect to.
	 * @param port remote port to connect to.
	 * @throws IOException
	 */
	public void connect(String host, int port) throws IOException {
		clientSocket = new Socket(host, port);
	}

	/**
	 * Perform the following actions {@link #COUNT} times in a row: 1. Connect
	 * to the remote server (host:port). 2. Write the string in myname (followed
	 * by newline) to the server 3. Read one line of response from the server,
	 * write it to sysout (without the trailing newline) 4. Close the socket.
	 * 
	 * Then do the following (only once): 1. send
	 * {@link HelloServer#BYE_MESSAGE} to the server (followed by newline). 2.
	 * Read one line of response from the server, write it to sysout (without
	 * the trailing newline)
	 * 
	 * If there are any IO Errors during the execution, output {@link HelloServer#ERR_MESSAGE}
	 * (followed by newline) to sysout. If the error is inside the loop,
	 * continue to the next iteration of the loop. Otherwise exit the method.
	 * 
	 * @param sysout
	 * @param host
	 * @param port
	 * @param myname
	 */
	public void run(PrintStream sysout, String host, int port, String myname) {
		DataOutputStream outToServer;
		BufferedReader inFromServer;
		String sentence;
		for (int i = 0; i < COUNT; i++) {
			try {
				connect(host, port);
				outToServer = new DataOutputStream(clientSocket.getOutputStream());
				inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				outToServer.writeBytes(myname + '\n');
				sentence = inFromServer.readLine();
				if (sentence == null){
					sysout.println(HelloServer.ERR_MESSAGE);
					clientSocket.close();
					continue;
				}
				sysout.println(sentence);
				clientSocket.close();
			} catch (IOException e) {
				sysout.println(HelloServer.ERR_MESSAGE);
				continue;
			}
		}

		try {
			connect(host, port);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outToServer.writeBytes(HelloServer.BYE_MESSAGE + '\n');
			sentence = inFromServer.readLine();
			if (sentence == null){
				sysout.println(HelloServer.ERR_MESSAGE);
				return;
			}
			sysout.println(sentence);
			clientSocket.close();
		} catch (IOException e) {
			sysout.println(HelloServer.ERR_MESSAGE);
			return;
		}
	}

	public static void main(String[] args) {
		HelloClient x = new HelloClient();
		x.run(System.err, "localhost", 1, "Gal Rabin");
	}
}
