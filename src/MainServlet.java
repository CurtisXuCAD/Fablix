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
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "MainServlet", urlPatterns = "/api/main")
public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // String item = request.getParameter("item");
        // System.out.println(item);
        HttpSession session = request.getSession();
        User u = (User)session.getAttribute("user");
        String username = u.getUsername();

        session.setAttribute("prev_url","main.html");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection())
        {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            Statement statement = conn.createStatement();

            String query = "SELECT * FROM genres;";


            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();
            while (rs.next())
            {
                String g = rs.getString("name");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("genres", g);

                jsonArray.add(jsonObject);
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("genres", username);
            jsonArray.add(jsonObject);
            out.write(jsonArray.toString());
            rs.close();
            statement.close();

            response.setStatus(200);
        }

        catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }



    }
}