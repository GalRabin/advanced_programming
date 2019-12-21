// Gal Rabin 312473721

import java.io.*;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;


/**
 * Implements a persistent dictionary that can be held entirely in memory.
 * When flushed, it writes the entire dictionary back to a file.
 * 
 * The file format has one keyword per line:
 * word:def1
 * word2:def2
 * ...
 * 
 * Note that an empty definition list is allowed (in which case the entry would have the form: word:
 * 
 */
public class InMemoryDictionary extends TreeMap<String,String> implements PersistentDictionary  {
	private static final long serialVersionUID = 1L; // (because we're extending a serializable class)
	File file;

	public InMemoryDictionary(File dictFile) {
		super();
		this.file = dictFile;
	}
	
	@Override
	public void open() throws IOException {
		try {
			Scanner input = new Scanner(this.file);
			StringBuilder line = new StringBuilder();
			// Read each line until no new line
			while (input.hasNextLine()){
				// load line for parsing
				line.append(input.nextLine());
				// check key location in line
				int key_end_index = line.indexOf(":");
				// Check value location in line
				int value_start_index = line.indexOf(":") + 1;
				int value_end_index = line.length();
				// Load key,value to TreeMap object
				super.put(line.substring(0,key_end_index), line.substring(value_start_index, value_end_index));
				// Reset string builder
				line.setLength(0);
			}
			input.close();
		}
		catch (FileNotFoundException ex){
			// Catch required exception, as an empty dict
		}

	}

	@Override
	public void close() throws IOException {
		BufferedWriter output = new BufferedWriter(new FileWriter(this.file));
		StringBuilder line = new StringBuilder();
		// Get a set of the entries
		Set set = super.entrySet();
		// Get an iterator
		Iterator it = set.iterator();
		// Display elements
		while(it.hasNext()) {
			// Get key, value entry
			Map.Entry me = (Map.Entry) it.next();
			// Build string before writing to file
			line.append(me.getKey());
			line.append(":");
			line.append(me.getValue());
			line.append("\n");
			// Write to file
			output.write(line.toString());
			// Reset string builder
			line.setLength(0);
		}
		output.close();
	}
}
