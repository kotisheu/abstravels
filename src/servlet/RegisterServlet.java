package servlet;
 
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
 
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.UserAccount;
import utils.DBUtils;
import utils.MyUtils;
 
@WebServlet(urlPatterns = { "/register" })
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
 
    public RegisterServlet() {
        super();
    }
 
    // Show register page
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
 
        RequestDispatcher dispatcher = request.getServletContext()
                .getRequestDispatcher("/WEB-INF/views/registerView.jsp");
        dispatcher.forward(request, response);
    }
 
    // When the user enters the user information, and clicks Submit.
    // This method will be called.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection conn = MyUtils.getStoredConnection(request);
        
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        String gender = (String) request.getParameter("gender");

        UserAccount user = new UserAccount(userName, gender,password);
   
        String errorString = null;
 
        if (userName == null || password == null || userName.length() == 0 || password.length() == 0) {
         
            errorString = "Required username and password!";
        } 
        try{
        	if(DBUtils.findUser(conn, userName)!= null) {
        		errorString="Username taken";
        	}
        }catch (SQLException e) {
            e.printStackTrace();
      
            errorString = e.getMessage();
        }
        
      
        if (errorString==null) {// if no error, register
            try {
                DBUtils.register(conn, user);
            } catch (SQLException e) {
                e.printStackTrace();
                errorString = e.getMessage();
            }
        }
 
        // Store information to request attribute, before forward to views.
        request.setAttribute("errorString", errorString);
        request.setAttribute("user", user);
 
        // If error, forward to register page.
        if (errorString != null) {
        	
            RequestDispatcher dispatcher = request.getServletContext()
                    .getRequestDispatcher("/WEB-INF/views/registerView.jsp");
            dispatcher.forward(request, response);
        }
        // If everything nice.
        // Redirect to the login page
        else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
 
}