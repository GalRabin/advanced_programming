// Gal Rabin 312473721


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Streams {
	/**
	 * Read from an InputStream until a quote character (") is found, then read
	 * until another quote character is found and return the bytes in between the two quotes. 
	 * If no quote character was found return null, if only one, return the bytes from the quote to the end of the stream.
	 * @param in
	 * @return A list containing the bytes between the first occurrence of a quote character and the second.
	 */
	public static List<Byte> getQuoted(InputStream in) throws IOException {
		List<Byte> quoted =  new ArrayList<>();
		boolean quotes_start = false;
		int current_byte = in.read();
		// Read until no Text
		while (current_byte != -1){
			// Find starting point by looking for quotes
			if (current_byte == '"' && !quotes_start){
				quotes_start = true;
			}
			// Find ending point and stop
			else if (current_byte == '"' && quotes_start){
				break;
			}
			// Append bytes if starting point init
			if (quotes_start && current_byte != '"') {
				quoted.add((byte)current_byte);
			}
			current_byte = in.read();
		}
		return quoted;
	}
	
	
	/**
	 * Read from the input until a specific string is read, return the string read up to (not including) the endMark.
	 * @param in the Reader to read from
	 * @param endMark the string indicating to stop reading. 
	 * @return The string read up to (not including) the endMark (if the endMark is not found, return up to the end of the stream).
	 */
	public static String readUntil(Reader in, String endMark) throws IOException {
		StringBuilder input = new StringBuilder();
		int current_byte = in.read();
		// Read until no text
		while (current_byte != -1){
			input.append((char) current_byte);
			current_byte = in.read();
		}
		// Look for end mark
		int end_index = input.indexOf(endMark);
		return input.substring(0, end_index);
	}
	
	/**
	 * Copy bytes from input to output, ignoring all occurrences of badByte.
	 * @param in
	 * @param out
	 * @param badByte
	 */
	public static void filterOut(InputStream in, OutputStream out, byte badByte) throws IOException {
		int current_byte = in.read();
		// Read until no text
		while (current_byte != -1){
			// write only if not match badByte
			if (current_byte != badByte){
				out.write(current_byte);
			}
			current_byte = in.read();
		}
	}
	
	/**
	 * Read a 48-bit (unsigned) integer from the stream and return it. The number is represented as five bytes, 
	 * with the most-significant byte first. 
	 * If the stream ends before 5 bytes are read, return -1.
	 * @param in
	 * @return the number read from the stream
	 */
	public static long readNumber(InputStream in) throws IOException {
		long number = 0;
		int num_of_bytes = 0;
		int move = 40;
		int current_byte = in.read();
		// Read until no text or 5 bytes read
		while (current_byte != -1 && num_of_bytes < 5) {
			// Insert bits in place
			move -= 8;
			if (move != 0) {
				number = number | (((long) current_byte) << move);
			}
			else {
				number = number | ((long) current_byte);
			}
			current_byte = in.read();
			num_of_bytes += 1;
		}
		// Check if less that 5 bytes
		if (num_of_bytes < 5){
			return -1;
		}

		return number;
	}
}
