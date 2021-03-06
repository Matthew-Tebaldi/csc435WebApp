package mypkg;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.*;
import javax.servlet.http.*;
 
public class LoginServlet extends HttpServlet {    	

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
     	throws IOException, ServletException {
            doPost(request, response);
        }
        
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
       	throws IOException, ServletException {
       
	String pass = request.getParameter("pswrd");
        String login = request.getParameter("loginText");   
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
	Connection conn = null;
        Statement stmt = null;
 
    try {     
        
        conn = DriverManager.getConnection("jdbc:mysql://localhost/csc435WebApp", "myuser", "xxxx");
        stmt = conn.createStatement();
   
        
//See if the user exists
        String sqlStr = "select name from users where name = '" + login + "'";
        ResultSet rset = stmt.executeQuery(sqlStr);  
            
        boolean inDatabase = false;
        while(rset.next()){
            if(rset.getString("name").equals(login)){
                inDatabase = true;
                break;
            }
        }  
        if(!inDatabase){
             request.setAttribute("logCheck", "loginNameNotFound");
         	RequestDispatcher rqds = request.getRequestDispatcher("/loginJsp.jsp");
         	rqds.forward(request, response);
		
         } else { 

//check to see if password matches        
        sqlStr = "select password from users where name = '" + request.getParameter("loginText") + "'";
        rset = stmt.executeQuery(sqlStr);  
              
        while(rset.next()) {       
            if(rset.getString("password").equals(pass)){       
               
                session.setAttribute("userName", login);
                request.setAttribute("logCheck", "loggedIn");
         	RequestDispatcher rqds = request.getRequestDispatcher("/loginJsp.jsp");
         	rqds.forward(request, response);
            } else {
                request.setAttribute("logCheck", "wrongPass");
         	RequestDispatcher rqds = request.getRequestDispatcher("/loginJsp.jsp");
         	rqds.forward(request, response);
              
            } 
	}
        } 
        
        
    } catch (SQLException ex) {
        ex.printStackTrace();
    } finally {
        out.close();  
        try {        
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    }  
}
