package extentreports;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class Listner implements ITestListener {

	
	public static ExtentTest test;
    ExtentReports extent = extentreports.Extentreports.getreport();
	
	
	public void onTestStart(ITestResult result)
	{
		test = extent.createTest(result.getMethod().getMethodName()).assignAuthor("Rushikesh").assignCategory("Functional").assignDevice("Chrome 99");
		
	}

	public void onTestSuccess(ITestResult result)
	{
		


        test.log(Status.PASS, "Test Passed");

	}

	public void onTestFailure(ITestResult result)
	{
		
		test.fail(result.getThrowable());

	}

	public void onTestSkipped(ITestResult result)
	{
		

        test.fail(result.getThrowable());

	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result)
	{
	}

	public void onStart(ITestContext context) 
	{
		
		
	}

	public void onFinish(ITestContext context) 
	{
		

        extent.flush();

	}

}
