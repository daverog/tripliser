package org.daverog.tripliser.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileService {

	public InputStream loadFile(String filename) throws FileNotFoundException {
		return new FileInputStream(new File(filename));
	}

	public void writeStringToNewFile(String filename, String text) throws IOException {
		FileOutputStream output = new FileOutputStream(new File(filename));
		output.write(text.getBytes());
		output.flush();
		output.close();
	}

	public FileOutputStream loadOutputStream(String filename) throws FileNotFoundException {
		return new FileOutputStream(new File(filename));
	}

}
