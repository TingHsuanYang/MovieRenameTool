package com.tsyang.movie.renametool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DeleteNote {

	public static void listf(String directoryName, List<File> files) {
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				if ("note.txt".equals(file.getName()))
					files.add(file);
			} else if (file.isDirectory()) {
				listf(file.getAbsolutePath(), files);
			}
		}
	}

	public static void main(String[] args) {
		List<File> l = new ArrayList<File>();
		listf("G:\\我的雲端硬碟\\整理完的電影", l);
		int count = 0;
		for (File f : l) {
			try {
				f.delete();
				count++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(String.format("已刪除%d個檔案", count));
	}

}
