package extentreports;

import com.aventstack.extentreports.ExtentReports;


import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class Extentreports 
{

	  public static ExtentReports extent;
	  
	  public static ExtentReports getreport()
	  {
		  
		  String path=System.getProperty("user.dir")+"//Extentreports//index.html";
		  
		  ExtentSparkReporter spark=new ExtentSparkReporter(path);
		  
		  spark.config().setTheme(Theme.DARK);
		  
		spark.config().setDocumentTitle("Automation test");
				
		spark.config().setReportName("Ecommerce test");
		 
		 
		  
		  extent=new ExtentReports();
		  
		  extent.attachReporter(spark);
		  extent.setSystemInfo("API", "Ecommerce");
		  return extent;
		  
	  
		  
		
		  
	  }
	
	
}
