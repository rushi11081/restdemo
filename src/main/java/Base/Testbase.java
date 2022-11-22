package Base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Testbase {

	 public Properties prop;
		
		public Testbase() 
		{
			
				
			prop=new Properties();
			try {
				
				String filepath="C:\\Users\\Rushikesh Patil\\eclipse-workspace2\\Recom\\src\\main\\java\\Config\\config.properties";

						FileInputStream ip=new FileInputStream(filepath);
								
								//System.getProperty("user.dir")+"./src/main/java/com.qa.config/config.properties");
								
			
				prop.load(ip);
			
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
			
			catch(IOException e)
			{
				e.printStackTrace();
			}
}
}