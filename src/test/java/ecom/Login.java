package ecom;

import static io.restassured.RestAssured.given;
import org.testng.ITestContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;



import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import Base.Testbase;
import Pojo.Loginrequest;
import Pojo.Orderdetail;
import Pojo.Orders;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Login extends Testbase
{
	Testbase testbase;
	Loginrequest lgnreq;
	public  String token;
	public String userId;

	public String prodid;
	public String prodorderid;

	String base;


	@BeforeMethod
	public void setup() throws IOException
	{

		testbase=new Testbase();

		base=prop.getProperty("baseurl");
	}

      // 1 login api 
	@Test(priority=1)
	public void logintest(ITestContext context) throws FileNotFoundException
	{
		// Create log file for login test
		PrintStream loginlog=new PrintStream(new FileOutputStream("./src/main/resource/loginlog.txt"));

		// create the object of testbase class 
		testbase=new Testbase();

		// Acessing pojo class for login email and password
		lgnreq=new Loginrequest();

		lgnreq.setUserEmail("mystudy@gmail.com");
		lgnreq.setUserPassword("Sangola@123");

		// use of requestspecification class
		RequestSpecification req=new RequestSpecBuilder().setBaseUri(base)
				.setContentType(ContentType.JSON).build();

		// collect response of login api
		String response= given().log().all().spec(req)
				.body(lgnreq) 
				.filter(RequestLoggingFilter.logRequestTo(loginlog))
				.filter(ResponseLoggingFilter.logResponseTo(loginlog))
				.when()
				.post("/api/ecom/auth/login").then().extract()
				.response().asString();

        // accessing token from login api for authorization in other api test cases
		JsonPath js=new JsonPath(response);
		
		token=js.getString("token");

		System.out.println("Autorization Token is = > " +token);

		 // accessing userid from login api for createproduct  api test cases
		
		userId=js.getString("userId");

		System.out.println("userid is =>" +userId);

       String msg=js.getString("message");		
		
     // 1 Test case to check whether login is success or not 
		
		Assert.assertEquals(msg,"Login Successfully");
		
		System.out.println("---------------------------------------------------------------------------");
	
        // Here use of ITestcontext interface for passing value of token and userid for other Test cases by setattribute 
		context.setAttribute("Authorization",token);
		
		context.setAttribute("productAddedBy",userId);

	}


   //--------- 2 api for create product -------------------------------------------------------
	@Test(priority=2)
	public void createproduct(ITestContext context) throws FileNotFoundException
	{
		System.out.println("------------------------product create-------------------------------------------");

		// here we accept those values of token and userid by getatttribute 
		
		String token=(String) context.getAttribute("Authorization");
		String userId=(String) context.getAttribute("productAddedBy");

		// Create log file for create-product
		PrintStream productlog=new PrintStream(new FileOutputStream("./src/main/resource/createproductlog.txt"));

		// use of requestspecification class
		RequestSpecification addproductreq=new RequestSpecBuilder().setBaseUri(base)
				.addHeader("Authorization",token).build();
       
		// here pass data by form-data method 
		RequestSpecification addproduct=given().log().all().spec(addproductreq)
				.filter(RequestLoggingFilter.logRequestTo(productlog))
				.filter(ResponseLoggingFilter.logResponseTo(productlog))
				.param("productName","Samsung tab")
				.param("productAddedBy",userId)
				.param("productCategory","electronics")
				.param("productSubCategory","display")
				.param("productPrice","15500")
				.param("productDescription", "samsung series")
				.param("productFor","men")
				.multiPart("productImage",new File("C:\\Users\\Rushikesh Patil\\Desktop\\2.jpg"));


		String produresp=addproduct.when()
				         .post("/api/ecom/product/add-product")
				         .then().log().all()
				         .extract().response().asString();


    // collect product id for create-order api

		JsonPath js=new JsonPath(produresp);

		prodid=js.get("productId");

		System.out.println("product id  is "+prodid);

		System.out.println("---------------------------------------------------------------------------------------");

		String msg=js.getString("message");
		
     // 1 to test whether product addded or not sucessfully 
		Assert.assertEquals(msg,"Product Added Successfully");

       // set attribute the prodid 
		context.setAttribute("productId",prodid);

		

		// To check status code
		Response response=addproduct.when().post("/api/ecom/product/add-product");

		Assert.assertEquals(response.statusCode(),201);
		
		System.out.println("The Response Time is " +response.time() + " ms");




	}

 // 3 API for create-order
	@Test(priority=3)
	public void createorder(ITestContext context) throws FileNotFoundException
	{
		System.out.println("------------------------order create-------------------------------------------");


		String token=(String) context.getAttribute("Authorization");
		String prodid=(String) context.getAttribute("productId");

		// it will create log file for create-order
		PrintStream orderlog=new PrintStream(new FileOutputStream("./src/main/resource/createorderlog.txt"));



		RequestSpecification createorderreq=new RequestSpecBuilder().setBaseUri(base)
				.addHeader("Authorization",token)
				.setContentType(ContentType.JSON).build();

		// here collect data from two different pojo classes 
		
		// child class
		Orderdetail orderdetail=new Orderdetail();

		orderdetail.setCountry("india");
		orderdetail.setProductOrderedId(prodid);

		List<Orderdetail> orderDetailList = new ArrayList<Orderdetail> ();
		orderDetailList.add(orderdetail);

		// parent pojo class orders
		Orders order=new Orders();
		order.setOrders(orderDetailList);



		RequestSpecification createorder=given().log().all().spec(createorderreq)
				                        .body(order)
				                        .filter(RequestLoggingFilter.logRequestTo(orderlog))
				                        .filter(ResponseLoggingFilter.logResponseTo(orderlog));

		String responseAddOrder =createorder .when().post("/api/ecom/order/create-order")
				                 .then().log().all()
				                 .extract().response().asString();

		System.out.println("order is " +responseAddOrder);

		JsonPath js=new JsonPath(responseAddOrder);
		
		String msg=js.getString("message");
		
		// to test order placed succfully or not
		Assert.assertEquals(msg,"Order Placed Successfully");

	
		
		String ord=js.getString("orders[0]");

		System.out.println("the order  id " +ord);

		// set attribute of orderid		
		context.setAttribute("id", ord);
		
		Response response=createorder.when().post("/api/ecom/order/create-order");

		Assert.assertEquals(response.statusCode(),201);
		
		System.out.println("The Response Time is " +response.time() + " ms");
		
		System.out.println("header is " +response.getHeaders());

		
		System.out.println("----------------------------------");
	}

	// 4 API for view-order 
	@Test(priority=4)
	public void getorder(ITestContext context) throws FileNotFoundException
	{
		System.out.println("-----------------------view -order-------------------------------------------");

		String token=(String) context.getAttribute("Authorization");

		String orderid=(String) context.getAttribute("id");

		
		// create log file for view order details
		PrintStream vieworderlog1=new PrintStream(new FileOutputStream("./src/main/resource/vieworderlog.txt"));

		
		RequestSpecification getorderreq=new RequestSpecBuilder().setBaseUri(base)
				                        .addHeader("Authorization",token)
				                        .build();
		
		RequestSpecification getorder = given().log().all().spec(getorderreq).queryParam("id",orderid)
				                       .filter(RequestLoggingFilter.logRequestTo(vieworderlog1))
				                       .filter(ResponseLoggingFilter.logResponseTo(vieworderlog1));	 

	 
		String response= getorder.when().get("https://rahulshettyacademy.com/api/ecom/order/get-orders-details")
				           .then().log().all()
				            .extract().response().asString();

		System.out.println("the ordered details are = "+response);


		JsonPath js=new JsonPath(response);

		String  productName=js.get("data.productName");
		
		System.out.println("product is ordered " +productName);

      // to check whether product is correcr or not 
		Assert.assertEquals(productName,"Samsung tab");
		
		Response response1=getorder.when().get("https://rahulshettyacademy.com/api/ecom/order/get-orders-details");

		Assert.assertEquals(response1.statusCode(),200);
		
		System.out.println("The Response Time is " +response1.time() + " ms");
		
	}

	// 5 API for delete product 
	@Test(priority=5)
	public void deleteproduct(ITestContext context) throws FileNotFoundException
	{
		System.out.println("------------------------product delete-------------------------------------------");

		// use of token and prodid
		String token=(String) context.getAttribute("Authorization");
		String prodid=(String) context.getAttribute("productId");
		
		// log file for delete product
		PrintStream proddeletelog=new PrintStream(new FileOutputStream("./src/main/resource/deleteproductlog.txt"));

		
		RequestSpecification deleteProdBaseReq=new RequestSpecBuilder().setBaseUri(base)
				.addHeader("Authorization",token).setContentType(ContentType.JSON)
				.build();

	RequestSpecification deleteProdReq =    given().log().all().spec(deleteProdBaseReq)
				                            .pathParam("productId",prodid)
				                            .filter(RequestLoggingFilter.logRequestTo(proddeletelog))
				                            .filter(ResponseLoggingFilter.logResponseTo(proddeletelog)); ;

				String deleteProductResponse = deleteProdReq.when()
						.delete("/api/ecom/product/delete-product/{productId}")
						.then().log().all().
						extract().response().asString();

				JsonPath js1 = new JsonPath(deleteProductResponse);

				String msg=js1.getString("message");
				
				Assert.assertEquals(msg,"Product Deleted Successfully");
	}


}
