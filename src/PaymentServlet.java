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
@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
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

    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");


        PrintWriter out = response.getWriter();
        String price = request.getParameter("price");
        System.out.println(price);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("price", price);
        out.write(jsonObject.toString());



    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fname = request.getParameter("fname");
        String lname = request.getParameter("lname");
        String card = request.getParameter("card");
        String exp = request.getParameter("exp");

        System.out.println(exp);
        request.getServletContext().log("pay with card: " + card);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();


        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection())
        {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"

            String query = "SELECT * FROM creditcards where id = ? and firstName = ? and lastname = ? and expiration = ?;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, card);
            statement.setString(2, fname);
            statement.setString(3, lname);
            statement.setString(4, exp);

            // statement.setString(2, password);

            System.out.println(statement);
            // Perform the query
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                // Have this user:

                    System.out.println("Correct");


                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");

            } else {
                // Login fail
                System.out.println("xx");
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Pay failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                // responseJsonObject.addProperty("message", "user with email " + email + " doesn't exist");
                responseJsonObject.addProperty("message", "Incorrect Information");
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