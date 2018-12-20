package webcrawler;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class WebCrawler {
	
	private String proxyHost = "";
	private int proxyPort = 0;
	private String fileString = "";
	
 char[] replaceChar = {'<','>',':','"','/', '\\', '|', '?', '*'};

    public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
    	WebCrawler webCrawler = new WebCrawler();
    	
    	webCrawler.loadProp();
    	
    	try (final WebClient webClient = new WebClient()) {
    		String webString;
    		ProxyConfig proxyConfig = new ProxyConfig(webCrawler.getProxyHost(), webCrawler.getProxyPort());
    		webClient.getOptions().setProxyConfig(proxyConfig);
    		webClient.getOptions().setJavaScriptEnabled(false);
    		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    		webClient.getOptions().setThrowExceptionOnScriptError(false);
    		webClient.getOptions().setUseInsecureSSL(true);
    		
    		for(int c=0;c<args.length;c++) {
    			webString = args[c];
    			System.out.println("webstring: " + webString);
    			webCrawler.setFileString(webCrawler.removeChar(webString));
                final HtmlPage page = webClient.getPage(webString);
                
                /*
                Path path = Paths.get("Data\\" + webCrawler.getFileString());
                try {
        			System.out.println("! Deleting File From The Path !");
        			if(!Files.notExists(path)) {
        				Files.delete(path);
        			}
                	page.save(path.toFile());
                	System.out.println(webCrawler.getFileString() + " saved.");
        		} catch(IOException ioException) {
        			ioException.printStackTrace();
        		}
        		*/
                
                File fileDir = new File("Data\\" + webCrawler.getFileString());
                File file = new File("Data\\" + webCrawler.getFileString() + ".html");
                System.out.println("File String: " + webCrawler.getFileString());

            	if(file.exists()) {
            		file.delete();
            		System.out.println(file.getName() + "deleted.");
            		FileUtils.deleteDirectory(fileDir);
            		System.out.println(fileDir.getName() + "deleted.");
            	}     
            	page.save(file);
            	System.out.println(webCrawler.getFileString() + " saved.");
            	
    		}
    		
    		System.out.println("SUCCESS");
    		
        }
        
    }
    
    public String getProxyHost() {
		return proxyHost;
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
 
    private void loadProp() {
    	Properties prop = new Properties();
    	InputStream input = null;

    	try {

    		input = new FileInputStream("config.properties");

    		// load a properties file
    		prop.load(input);

    		// get the property value and print it out
    		proxyHost = prop.getProperty("proxyhost");
    		proxyPort = Integer.parseInt(prop.getProperty("proxyport"));

    	} catch (IOException ex) {
    		ex.printStackTrace();
    	} finally {
    		if (input != null) {
    			try {
    				input.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}

    }

    private String removeChar(String src){
        char[] srcArr = src.toCharArray(); 
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < srcArr.length; i++) {
            char foundChar = isFound(srcArr[i]);
            if(foundChar!='\0')
            sb.append(foundChar);           
        }
        return sb.toString();

    } 

    private char isFound(char src){      
        for (int i = 0; i < replaceChar.length; i++) {
            if(src==replaceChar[i]){
                return '\0';
            }
        }
        return src;
    }

}
