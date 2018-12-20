package com.tsyang.movie.renametool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class FileChecker {

	/**
	 * create note if there are errors or more than one result
	 * 
	 * @param path
	 * @param isAppend
	 * @param message
	 */
	public static void createNote(String... message) {
		String path = "empty.txt";
		try {
			File f = new File(path);
			f.createNewFile();
			List<String> lines = Arrays.asList(message);
			Path file = Paths.get(f.getAbsolutePath());
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.out.println("[createNote]" + e.getMessage());
		}
	}

	public static void main(String[] args) {
		String path = "G:\\我的雲端硬碟\\整理完的電影";
		File folder = new File(path);
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File f : files) {
				if (f.isDirectory()) {
					File[] contents = f.listFiles();
					if (contents == null) {
						System.out.println(String.format("[File]%s", f.getName()));
					}
					// Folder is empty
					else if (contents.length == 0) {
						System.out.println(String.format("[Empty]%s", f.getName()));
						createNote(f.getName());
					}
					// Folder contains files
					else {
						System.out.println(String.format("[Filled]%s", f.getName()));
					}
				}

			}
		}

	}

}
