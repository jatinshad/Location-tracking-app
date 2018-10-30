WebService.java

package book;

import java.sql.*;
import java.security.NoSuchAlgorithmException;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@Path("/path")
public class employee {
	
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/track/{tem}")
	public String foo(@PathParam("tem")String tem) throws NoSuchAlgorithmException
	{
		
		tem=tem.replace('@',' ');
		
		String t="Your address is "+tem;
        
	    
	
			// TODO Auto-generated method stub
			try {
				Class.forName("com.mysql.jdbc.Driver"); 
				Connection myConn=DriverManager.getConnection("jdbc:mysql://localhost:3306/track", "root", "");
			    Statement myst=myConn.createStatement();
			    myst.execute("insert into Addresses(Address) values('"+tem+"')");
			    ResultSet myRs=myst.executeQuery("select * from Addresses");
			    while(myRs.next())
			    {
			    	System.out.println(myRs.getString("S.No")+","+myRs.getString("Address"));
			    }
			}
			catch(Exception e) {
	         e.printStackTrace();

		}
			
		
	
	    
	    System.out.println(t);
		return (t);
	}
	
	

	
}

