package webcrawler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Savepoint;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Dient als Klasse für Web-Crawling, die eine Konfiguration benötigen
 * 
 * @author Ardilla Latifasari
 *
 */

public class WebCrawler {

	private String proxyHost;
	private int proxyPort;
	private String fileString;
	private String webString;

	public static final String DEFAULT_DOWNLOAD_DIR = "./sites/";
	public static final String PROPERTY_FILE = "config.properties";
	public static final String PROPERTY_PROXYHOST = "proxyhost";
	public static final String PROPERTY_PROXYPORT = "proxyport";

	private File fileDir;
	private File file;

	private static final WebClient webClient = new WebClient();
	// ungültige Sonderzeichen bei der URL
	private char[] replaceChar = { '<', '>', ':', '"', '/', '\\', '|', '?', '*' };

	public static void main(String[] args) throws IOException {
		WebCrawler webCrawler = new WebCrawler();
		webCrawler.loadProp();
		System.out.println("Property loaded");
		webCrawler.config();
		System.out.println("Web client configured");
		webCrawler.savePage(args);
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setFileString(String fileString) {
		this.fileString = fileString;
	}

	public String getFileString() {
		return fileString;
	}

	/**
	 * Zugreifen auf die Konfiguration-Datei
	 * 
	 * @throws IOException
	 */
	public void loadProp() throws IOException {
		Properties prop = new Properties();
		InputStream input = null;

		input = new FileInputStream(PROPERTY_FILE);
		prop.load(input);

		proxyHost = prop.getProperty(PROPERTY_PROXYHOST);
		proxyPort = Integer.parseInt(prop.getProperty(PROPERTY_PROXYPORT));
	}

	/**
	 * Entfernt die ungültige Sonderzeichen zum Speichern der Datei
	 * 
	 * @param src
	 * URL der Seite als String
	 * 
	 * @return
	 * gültige Sonderzeichen als String
	 */
	public String removeChar(String src) {
		char[] srcArr = src.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < srcArr.length; i++) {
			char foundChar = isFound(srcArr[i]);
			if (foundChar != '\0')
				sb.append(foundChar);
		}
		return sb.toString();
	}

	/**
	 * Suchen nach einem char
	 * 
	 * @param src
	 * @return
	 */
	public char isFound(char src) {
		for (int i = 0; i < replaceChar.length; i++) {
			if (src == replaceChar[i]) {
				return '\0';
			}
		}
		return src;
	}
	
	/**
	 * Konfigurieren des WebClients
	 */

	public void config() {
		ProxyConfig proxyConfig = new ProxyConfig(getProxyHost(), getProxyPort());
		webClient.getOptions().setProxyConfig(proxyConfig);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setUseInsecureSSL(true);
	}
	
	/**
	 * Speichern der Seite
	 * 
	 * @param args
	 * URL Seiten, die heruntergeladen würden
	 * 
	 * @throws IOException
	 */

	public void savePage(String[] args) throws IOException {
		for (int c = 0; c < args.length; c++) {
			webString = args[c];
			System.out.println("webstring: " + webString);
			setFileString(removeChar(webString));
			final HtmlPage page = webClient.getPage(webString);

			fileDir = new File(DEFAULT_DOWNLOAD_DIR + getFileString());
			if (getFileString().matches("^.*html$")) {
				file = new File(DEFAULT_DOWNLOAD_DIR + getFileString());
			} else {
				file = new File(DEFAULT_DOWNLOAD_DIR + getFileString() + ".html");
			}

			if (file.exists()) {
				file.delete();
				System.out.println(file.getName() + "deleted.");
				FileUtils.deleteDirectory(fileDir);
				System.out.println(fileDir.getName() + "deleted.");
			}
			page.save(file);
			System.out.println(getFileString() + " saved.");

		}

	}

}
