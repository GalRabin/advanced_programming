// Gal Rabin 312473721

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class RandomAccess {
	
	/**
	 * Treat the file as an array of (unsigned) 8-bit values and sort them
	 * 	 * in-place using a bubble-sort algorithm.
	 * 	 * You may not read the whole file into memory!
	 * @param file
	 */
	public static void sortBytes(RandomAccessFile file) throws IOException {
		long len = file.length();
		for (long i = 0; i < len ; i++) {
			boolean change_iter = false;
			for (long j = 0; j < len - 1; j++) {
				// Check location j and j+1
				file.seek(j);
				byte index_j = file.readByte();
				file.seek(j+1);
				byte index_j_1 = file.readByte();
				if (((int)index_j & 0xFF) > ((int)index_j_1 & 0xFF)){
					file.seek(j);
					file.write(index_j_1);
					file.seek(j+1);
					file.write(index_j);
					change_iter = true;
				}
			}
			// check if last loop change anything if not break
			if (!change_iter) break;
		}
	}
	
	/**
	 * Treat the file as an array of unsigned 24-bit values (stored MSB first) and sort
	 * them in-place using a bubble-sort algorithm. 
	 * You may not read the whole file into memory! 
	 * @param file
	 * @throws IOException
	 */
	public static void sortTriBytes(RandomAccessFile file) throws IOException {
		long len = file.length() / 3;
		for (long i = 0; i < len ; i++) {
			boolean change_iter = false;
			for (long j = 0; j < len-1; j++) {
				long real_index = 3 * j;
				// First real_index 0-2 bits
				file.seek(real_index);
				byte index_j = file.readByte();
				file.seek(real_index+1);
				byte index_j_1 = file.readByte();
				file.seek(real_index+2);
				byte index_j_2 = file.readByte();
				int first_value = 0;
				// Calculate first 3 bits value
				first_value |= index_j << 16;
				first_value |= index_j_1 << 8;
				first_value |= index_j_2;

				// Second real_index 3-5 bits
				file.seek(real_index+3);
				byte index_j_3 = file.readByte();
				file.seek(real_index+4);
				byte index_j_4 = file.readByte();
				file.seek(real_index+5);
				byte index_j_5 = file.readByte();
				// Calculate second 3 bits value
				int second_value = 0;
				second_value |= index_j_3 << 16;
				second_value |= index_j_4 << 8;
				second_value |= index_j_5;

				// Check if need to replace places
				if (first_value > second_value){
					file.seek(real_index);
					file.writeByte(index_j_3);
					file.seek(real_index+1);
					file.writeByte(index_j_4);
					file.seek(real_index+2);
					file.writeByte(index_j_5);

					file.seek(real_index+3);
					file.writeByte(index_j);
					file.seek(real_index+4);
					file.writeByte(index_j_1);
					file.seek(real_index+5);
					file.writeByte(index_j_2);

					change_iter = true;
				}
			}
			// check if last loop change anything if not break
			if (!change_iter) break;
		}
	}
}
