package ex4;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class HelloServer {

	public static final String ERR_MESSAGE = "IO Error!";
	public static final String LISTEN_MESSAGE = "Listening on port: ";
	public static final String HELLO_MESSAGE = "hello ";
	public static final String BYE_MESSAGE = "bye";
	public ServerSocket SERVER;


	public ServerSocket getServerSocket(){
        return SERVER;
	}


	private boolean isFreePort(int port) {
		try {
			// ServerSocket try to open a LOCAL port
			SERVER = new ServerSocket(port);
			return true;
		} catch(IOException e) {
			// local port cannot be opened, it's in use
			return false;
		}
	}

	/**
	 * Listen on the first available port in a given list.
	 *
	 * <p>Note: Should not throw exceptions due to ports being unavailable</p>
	 *
	 * @return The port number chosen, or -1 if none of the ports were available.
	 *
	 */
	public int listen(List<Integer> portList) throws IOException {
		for (int port: portList) {
			if (isFreePort(port)){
				return port;
			}
		}
        return -1;
	}


	/**
	 * Listen on an available port.
	 * Any available port may be chosen.
	 * @return The port number chosen.
	 */
	public int listen() {
		int MAX_PORT = 65535;
		int tested_port = 85;
		// Iterate until finding available port
		while (tested_port <= MAX_PORT){
			if (isFreePort(tested_port)){
				return tested_port;
			}
			tested_port++;
		}
        return -1;
	}


	/**
	 * 1. Start listening on an open port. Write {@link #LISTEN_MESSAGE} followed by the port number (and a newline) to sysout.
	 * 	  If there's an IOException at this stage, exit the method.
	 *
	 * 2. Run in a loop;
	 * in each iteration of the loop, wait for a client to connect,
	 * then read a line of text from the client. If the text is {@link #BYE_MESSAGE},
	 * send {@link #BYE_MESSAGE} to the client and exit the loop. Otherwise, send {@link #HELLO_MESSAGE}
	 * to the client, followed by the string sent by the client (and a newline)
	 * After sending the hello message, close the client connection and wait for the next client to connect.
	 *
	 * If there's an IOException while in the loop, or if the client closes the connection before sending a line of text,
	 * send the text {@link #ERR_MESSAGE} to sysout, but continue to the next iteration of the loop.
	 *
	 * *: in any case, before exiting the method you must close the server socket.
	 *
	 * @param sysout a {@link PrintStream} to which the console messages are sent.
	 *
	 *
	 */
	public void run(PrintStream sysout) {
		// Start listening on an open port. Write {@link #LISTEN_MESSAGE} followed by the port number (and a newline) to sysout.
		// If there's an IOException at this stage, exit the method.
		int port = listen();
		if (port == -1) {
			return;
		} else {
			sysout.println(LISTEN_MESSAGE + port);
		}

		//  Run in a loop in each iteration of the loop, wait for a client to connect.
		while (true) {
			try {
				// Start accepting clients connections
				Socket connection = getServerSocket().accept();

				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(connection.getOutputStream());
				// Get line from client
				String clientSentence = inFromClient.readLine();
				if (clientSentence == null){
					sysout.println(ERR_MESSAGE);
					connection.close();
					continue;
				}
				if (clientSentence.equals(BYE_MESSAGE)) {
					outToClient.writeBytes(BYE_MESSAGE + "\n");
					connection.close();
					getServerSocket().close();
					return;
				} else {
					outToClient.writeBytes(HELLO_MESSAGE + clientSentence + "\n");
					connection.close();
				}
			}
			catch (IOException e) {
				sysout.println(ERR_MESSAGE);
			}
		}
	}



	public static void main(String args[]) {
		HelloServer server = new HelloServer();
		server.run(System.err);
	}
}
