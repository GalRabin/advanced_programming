package ex4;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileServer {

	static final String RESP_OK = "HTTP/1.0 200 OK\r\nConnection: close\r\n\r\n";
	static final String RESP_BADPATH = "HTTP/1.0 403 Forbidden\r\nConnection: close\r\n\r\nForbidden: ";
	static final String RESP_NOTFOUND = "HTTP/1.0 404 Not Found\r\nConnection: close\r\n\r\nNot found: ";
	static final String RESP_BADMETHOD = "HTTP/1.0 405 Method not allowed\r\nConnection: close\r\nAllow: GET\r\n\r\nBad";
	static final String RESP_EXIT = RESP_OK + "Thanks, I'm done.";

	static final String MSG_IOERROR = "There was an IO Error";
	static final String PATH_EXIT = "/exit";

	/**
	 * Check if a string is a well-formed absolute path in the filesystem. A well-formed
	 * absolute path must satisfy:
	 * <ul>
	 * <li>Begins with "/"
	 * <li>Consists only of English letters, numbers, and the special characters
	 * '_', '.', '-', '~' and '/'.
	 * <li>Does not contain any occurrences of the string "/../".
	 * </ul>
	 * 
	 * @param path
	 *            The path to check.
	 * @return true if the path is well-formed, false otherwise.
	 */
	public boolean isLegalAbsolutePath(String path) {
		// Check path legal with Regex
		Pattern p = Pattern.compile("^\\/[A-Za-z_.-~\\/]*");
		Matcher m = p.matcher(path);
		if (m.find()){
			return true;
		}
		return false;
	}


	/**
	 * This method should do the following things, given an open (already
	 * listening) server socket:
	 * <ol>
	 * 	<li>Do the following in a loop (this is the "main loop"):
	 * 	<ol>
	 * 		<li>Wait for a client to connect. When a client connects, read one line
	 * 		of input (hint: you may use {@link BufferedReader#readLine()} to read a
	 * 		line).
	 * 		<li>The client's first line of input should consist of at least two words
	 * 		separated by spaces (any number of spaces is ok). If it's less than two words,
	 * 		you may handle it in any reasonable way (e.g., just close the connection).
	 * 		<ul>
	 * 			<li>if the first word is not GET (case-insensitive), send the string
	 * 			{@link #RESP_BADMETHOD} to the client and close the connection.
	 * 			<li>otherwise, parse the second word as a full path (a filename,
	 * 			including directories, separated by '/' characters).
	 * 			<li>if the pathname is exactly {@value #PATH_EXIT} (case sensitive), send
	 * 			{@link #RESP_EXIT} to the client and close the connection. Then exit the
	 * 			main loop (do not close the server socket).
	 * 			<li>if the path is not a legal absolute path (use
	 * 			{@link #isLegalAbsolutePath(String)} to check) send
	 * 			{@link #RESP_BADPATH}, followed by the path itself, to the client and close the connection.
	 * 			<li>if the path is a legal path but the file does not exist or cannot be read,	
	 * 			{@link #RESP_NOTFOUND}, followed by the path itself, to the client and close the connection.
	 * 			<li>otherwise, send {@link #RESP_OK} to the client, then open the file
	 * 			and send its contents to the client. Finally, close the connection.
	 * 		</ul>
	 * 		<li>If there is an I/O error during communication with the client or
	 * 		reading from the file, output the string {@value #MSG_IOERROR} to sysErr
	 * 		and close the connection (ignore errors during close).
	 * 	</ol>
	 * </ol>
	 * 
	 * @param serverSock
	 *            the {@link ServerSocket} on which to accept connections.
	 * @param sysErr
	 *            the {@link PrintStream} to which error messages are written.
	 */
	public void runSingleClient(ServerSocket serverSock, PrintStream sysErr) {
		//  Run in a loop in each iteration of the loop, wait for a client to connect.
		while (true) {
			try {
				// Start accepting clients connections
				Socket connection = serverSock.accept();
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(connection.getOutputStream());
				// Waits for line sent from server
				String clientSentence = inFromClient.readLine();
				// If client disconnected in the middle
				if (clientSentence == null) {
					connection.close();
					continue;
				}
				else {
					String[] words = clientSentence.split("\\s+");
					// verify amount of argument is legal, more than 2
					if (words.length < 2) {
						connection.close();
						continue;
					}
					// Check legal get http method is perform
					if (!words[0].toLowerCase().equals("get")) {
						outToClient.writeBytes(RESP_BADMETHOD + "\n");
						connection.close();
						continue;
					}
					else {
						String path = words[1];
						// Give required response if second argument equals to /exit
						if (path.equals(PATH_EXIT)) {
							outToClient.writeBytes(RESP_EXIT + "\n");
							connection.close();
							break;
						}
						// Check if the path is legal path by required convention
						if (isLegalAbsolutePath(path)) {
							// Send to client response ok 200
							outToClient.writeBytes(RESP_OK + "\n");
							// Reading temp file created
							BufferedReader reader = new BufferedReader(new FileReader(path));
							String line = null;
							StringBuilder stringBuilder = new StringBuilder();
							String ls = System.getProperty("line.separator");
							try {
								while ((line = reader.readLine()) != null) {
									stringBuilder.append(line);
									stringBuilder.append(ls);
								}
								outToClient.writeBytes(stringBuilder.toString() + '\n');
								connection.close();
							}
							// Handle error in middle of reading file or writing to client, make sure connection closed
							catch (IOException e){
								sysErr.println(MSG_IOERROR);
								connection.close();
							}
							// Make sure file is closed
							finally {
								reader.close();
							}
						}

					}
				}
			}
			// Handle error if server failed due to IO error, not closing connection because if its failed here its mean that the connection is broken.
			catch (IOException e) {
				sysErr.println(MSG_IOERROR);
			}
		}

	}

	/**
	 * Convert a windows-style path (e.g. "C:\dir\dir2") to POSIX style (e.g. "/dir1/dir2")
	 */
	static String convertWindowsToPOSIX(String path) {
		return path.replaceFirst("^[a-zA-Z]:", "").replaceAll("\\\\", "/");
	}
	
	/**
	 * This is for your own testing.
	 * If you wrote the code correctly and run the program,
	 * you should be able to enter the "test URL" printed
	 * on the console in a browser, see  a "Yahoo!..." message, 
	 * and click on a link to exit.  
	 * @param args
	 */
	static public void main(String[] args) {
		FileServer fs = new FileServer();
		HelloServer serve = new HelloServer();

		File tmpFile = null;
		try {
			try {
				tmpFile = File.createTempFile("test", ".html");
				FileOutputStream fos = new FileOutputStream(tmpFile);
				PrintStream out = new PrintStream(fos);
				out.println("<!DOCTYPE html>\n<html>\n<body>Yahoo! Your test was successful! <a href=\""+ PATH_EXIT +"\">Click here to exit</a></body>\n</html>");
				out.close();

				int port = serve.listen();
				System.err.println("Test URL: http://localhost:" + port
						+ convertWindowsToPOSIX(tmpFile.getAbsolutePath()));
			} catch (IOException e) {
				System.err.println("Exception, exiting: " + e);
				return;
			}

			fs.runSingleClient(serve.getServerSocket(), System.err);
			System.err.println("Exiting due to client request");

			try {
				serve.getServerSocket().close();
			} catch (IOException e) {
				System.err.println("Exception closing server socket, ignoring:"
						+ e);
			}
		} finally {
			if (tmpFile != null)
				tmpFile.delete();
		}
	}

}
