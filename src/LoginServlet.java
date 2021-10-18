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

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("Login Post");
        // The log message can be found in localhost log
        request.getServletContext().log("GetUser: " + username);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();


        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection())
        {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"

            String query = "SELECT * FROM moviedb.customers where email = ? and password = ?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, username);
            statement.setString(2, password);


            // Perform the query
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                // Login success:
                System.out.println("Correct");
                HttpSession session = request.getSession(true);

                // set this user into the session
                session.setAttribute("user", new User(username));

                // set the logged_in attribute
                Boolean logged_in = (Boolean) session.getAttribute("logged_in");
                logged_in = true;
                session.setAttribute("logged_in", logged_in);

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // Login fail

                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                if (!username.equals("anteater")) {
                    responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
                } else {
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }

            out.write(responseJsonObject.toString());
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
