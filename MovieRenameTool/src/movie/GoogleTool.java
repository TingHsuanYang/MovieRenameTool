package movie;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * search key word on Google with Selenium
 * 
 * @author Evan
 *
 */
public class GoogleTool {

	public static String googleSearch(String keyword) {
		String enName = "";
		String chName = "";
		String originYear = "";
		String year = "";

		String path = ".";
//		String path = System.getProperty("user.dir");
		System.setProperty("webdriver.chrome.driver", path + File.separator + "chromedriver.exe");
		System.setProperty("webdriver.chrome.silentOutput", "true");
		// 英文名字
		ChromeOptions enOptions = new ChromeOptions();
		enOptions.addArguments("--lang=en-GB");
		enOptions.addArguments("--headless");
		WebDriver enDriver = new ChromeDriver(enOptions);
		enDriver.get("https://www.google.com/search?q=" + keyword + "+movie");
		try {
			enName = enDriver.findElement(By.cssSelector(".xpdopen .kp-header div[role='heading']>div:nth-child(1)")).getText().trim();
		} catch (Exception e) {
			enDriver.quit();
			System.out.println("-----cannot find English name-----\n");
			return null;
		}
		// 中文名字
		ChromeOptions chOptions = new ChromeOptions();
		chOptions.addArguments("--headless");
		WebDriver chDriver = new ChromeDriver(chOptions);
		chDriver.get("https://www.google.com/search?q=" + keyword + "+movie");
		try {
			chName = chDriver.findElement(By.cssSelector(".xpdopen .kp-header div[role='heading']>div:nth-child(1)")).getText().trim();
		} catch (Exception e) {
			chDriver.quit();
			System.out.println("-----cannot find Chinese name-----\n");
			return null;
		}
		// 年
		try {
			originYear = chDriver.findElement(By.cssSelector(".xpdopen .kp-header div[role='heading']>div:nth-child(2)")).getText();
		} catch (Exception e) {
			chDriver.quit();
			System.out.println("-----cannot find release year-----\n");
			return null;
		}
		Pattern p = Pattern.compile("\\b(19|20)\\d{2}\\b");
		Matcher m = p.matcher(originYear);
		if (m.find()) {
			year = m.group(0);
		}
		enDriver.quit();
		chDriver.quit();
		System.out.println(String.format("-----%s %s (%s)-----\n", enName, chName, year));
		return String.format("%s %s (%s)", enName, chName, year);
	}
	
	public static void main(String[] args) {
		googleSearch("Logan");
	}

}
