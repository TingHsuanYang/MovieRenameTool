package movie;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameMovie {
	private final static String[] IGNORE_WORD = { "null", "chs", "srt", "torrent", "bluray", "aac", "sparks", "tigole", "mkv", "cht", "eng", "dl", "publichd", "bdrip" };

	/**
	 * extract keywords from file name
	 * 
	 * @param name
	 * @return
	 */
	public static List<String> getKeyword(String name) {
		String newName = name.toLowerCase();

		// split by symbols(only English words was left)
		String[] arr = newName.split("\\s*[^a-zA-Z0-9]+\\s*");
		List<String> list = new ArrayList<String>(Arrays.asList(arr));

		// remove unwanted words
		list.removeAll(Arrays.asList(IGNORE_WORD));

		// remove illegal words e.x.1080p
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String s = iterator.next();
			if (isErrorProne(s)) {
				iterator.remove();
			}
		}

		// get year
		String year = "";
		Pattern yearP = Pattern.compile("\\b(19|20)\\d{2}\\b");
		Matcher yearM = yearP.matcher(newName);
		if (yearM.find()) {
			year = yearM.group(0);
		}

		// get episode
		String episode = "";
		Pattern episodeP = Pattern.compile("\\b\\d\\b");
		Matcher episodeM = episodeP.matcher(newName);
		if (episodeM.find()) {
			episode = episodeM.group(0);
		}

		// add released year and movie episode as keyword
		list.add(year);
		list.add(episode);
		System.out.println(String.format("[keyword] %s", String.join(",", list)));
		return list;
	}

	/**
	 * remove the word which is not a word e.x.720p, 1080p
	 * 
	 * @param name
	 * @return
	 */
	private static boolean isErrorProne(String name) {
		return !name.matches("^[a-zA-Z \\-']+$");
	}

	/**
	 * search movie name from google search
	 * 
	 * @param keywords
	 * @return
	 */
	public static String searchName(String... keywords) {
		if (keywords.length <= 0) {
			return null;
		}
		String searchword = String.join("+", keywords);
		return GoogleTool.googleSearch(searchword);
	}

	/**
	 * get movie name by list of files, it will stop as soon as it has result
	 * 
	 * @param subfiles
	 * @return
	 */
	public static String getNameBySubfile(List<File> subfiles) {
		String result = null;
		for (File f : subfiles) {
			System.out.println(String.format("[Sub-Dir Files] : %s", f.getName()));
			List<String> keyword = getKeyword(f.getName());
			result = searchName(keyword.toArray(new String[0]));
			if (null != result) {
				return result;
			}
		}
		return result;
	}

	/**
	 * create note if there are errors or more than one result
	 * 
	 * @param path
	 * @param isAppend
	 * @param message
	 */
	public static void createNote(boolean isErr, String... message) {
		String path = "namelist.txt";
		if (isErr) {
			path = "error.txt";
		}
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

	/**
	 * rename the movie file name
	 * 
	 * @param dirPath
	 * @param newDirName
	 */
	public static void renameMovieFolder(String dirPath, String newDirName) {
		String n = newDirName.replaceAll(":", "：").replaceAll("\\/", "／").replaceAll("\\?", "？").replaceAll("\\*", "＊").replaceAll("\\|", "｜").replaceAll("\\<", "＜").replaceAll("\\>", "＞")
				.replaceAll("\"", "＂").replaceAll("\\\\", "＼ ");
		File dir = new File(dirPath);
		if (!dir.isDirectory()) {
			System.err.println("There is no directory @ given path");
		} else {
			// System.out.println("Enter new name of directory(Only Name and Not Path).");
			File newDir = new File(dir.getParent() + File.separator + n);
			dir.renameTo(newDir);
		}

	}

	/**
	 * add all files in sub-directory into list
	 * 
	 * @param directoryName
	 * @param files
	 */
	public static void listf(String directoryName, List<File> files) {
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				files.add(file);
			} else if (file.isDirectory()) {
				listf(file.getAbsolutePath(), files);
			}
		}
	}

	public static void main(String[] args) {
		String filePath = "C:\\MyMoviePath";
//		String filePath = System.getProperty("user.dir");
		List<String> searchList = new ArrayList<String>();
		// get movie folders
		File f = new File(filePath);
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File file : files) {
				if (file.isFile()) // skip if the file is not a folder
					continue;

				boolean renameFlag = true; // rename the folder or not

				String s = file.getName();
				System.out.println(String.format("[File Name] %s", s));

				String foldername = searchName(getKeyword(s).toArray(new String[0]));

				if (null == foldername) { // get file from sub directory files
					List<File> subFiles = new ArrayList<File>();
					listf(file.getAbsolutePath(), subFiles);
					foldername = getNameBySubfile(subFiles);
				}

				if (null != foldername) {
					searchList.add(foldername);
				} else {
					createNote(true, s);
					renameFlag = false;
				}

				if (renameFlag) {
					renameMovieFolder(file.getAbsolutePath(), foldername);
				}
			}
		}
		createNote(false, searchList.toArray(new String[0]));
	}
}
