package com.tsyang.movie.renametool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeleteNote {

	public static void listf(String directoryName, List<File> files) {
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				if (file.getName().endsWith(".txt")||file.getName().endsWith(".torrent")||file.getName().startsWith("._")||file.getName().endsWith(".html"))
					files.add(file);
			} else if (file.isDirectory()) {
				listf(file.getAbsolutePath(), files);
			}
		}
	}

	public static void createNote(String... message) {
		String path = "files.txt";
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
		List<File> l = new ArrayList<File>();
//		listf("D:\\新增資料夾", l);
		listf("G:\\我的雲端硬碟\\整理完的電影", l);
		int count = 0;
		for (File f : l) {
			try {
				System.out.println(f.getName());
//				createNote(f.getName());
				f.delete();
				count++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(String.format("已刪除%d個檔案", count));
	}

}
