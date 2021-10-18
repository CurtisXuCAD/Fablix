import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "MainServlet", urlPatterns = "/api/main")
public class MainServlet extends HttpServlet {


    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
                String action = request.getParameter("action");
                // System.out.println("run");
                if ("Logout".equals(action)) {
                    HttpSession session = request.getSession(true);
                    // System.out.println("run1");
    
                    // set the logged_in attribute
                    // Boolean logged_in = (Boolean) session.getAttribute("logged_in");
                    // logged_in = false;
                    session.setAttribute("logged_in", null);
                    // response.sendRedirect("login.html");
                }
    }
}