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
@WebServlet(name = "rDashboardServlet", urlPatterns = "/api/dashboard")
public class rDashboardServlet extends HttpServlet
{

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
            throws IOException
    {

        response.setContentType("application/json");

        String titlename = request.getParameter("name");
        String director = request.getParameter("director");
        String star = request.getParameter("stars");
        String year = request.getParameter("year");
        String genre = request.getParameter("genre");

        String star_name = request.getParameter("star_name");
        String birth_year = request.getParameter("birth_year");

        System.out.println("1" + titlename);
        System.out.println("2" + director);
        System.out.println("3" + star);
        System.out.println("4" + year);
        System.out.println("5" + genre);
        System.out.println("6" + star_name);
        System.out.println("7" + birth_year);

        request.getServletContext().log("add movie: " + titlename);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();


        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection())
        {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            System.out.println("why??");
//           if (!star_name.equals("null"))
//           {
//               System.out.println("why??");
//               Statement statement = conn.createStatement();
//               String query = "(select concat( 'nm',  LPAD((convert((SELECT SUBSTRING_INDEX((select max(id) from stars),'m',-1)), unsigned ) + 1), 7 ,0)) as max);";
//               ResultSet rs = statement.executeQuery(query);
//
//               if (rs.next())
//               {
//                   String maxid = rs.getString("max");
//
//
//                    if (birth_year.equals(""))
//                    {
//                        String insquery = "INSERT INTO stars\n" +
//                                "VALUES (?,?,null);";
//                        PreparedStatement insstatement = conn.prepareStatement(insquery);
//
//
//                        insstatement.setString(1, maxid);
//
//                        insstatement.setString(2, star_name);
//                        insstatement.executeUpdate();
//                        insstatement.close();
//                    }
//                    else
//                    {
//                        String insquery = "INSERT INTO stars\n" +
//                                "VALUES (?,?,?);";
//                        PreparedStatement insstatement = conn.prepareStatement(insquery);
//
//
//                        insstatement.setString(1, maxid);
//
//                        insstatement.setString(2, star_name);
//                        insstatement.setInt(3, Integer.parseInt(birth_year));
//                        insstatement.executeUpdate();
//                        insstatement.close();
//                    }
//
//
//
//
//                   String result = "The star has been successfully added.\n Star ID : " + maxid;
//
//                   responseJsonObject.addProperty("status", "success");
//                   responseJsonObject.addProperty("message",result);
//                   out.write(responseJsonObject.toString());
//
//                   rs.close();
//                   statement.close();
//
//
//               }
//
//           }


//           if (!titlename.equals("null"))
//           {
               String query = "{CALL CreateMovies  (?,?,?,?,?, ?,?,?,?)}";

               System.out.println("1sad" + titlename);
               System.out.println("2sad" + director);
               System.out.println("sad3" + star);
               System.out.println("asd4" + year);
               System.out.println("sad5" + genre);
               System.out.println("sad6" + star_name);
               System.out.println("sad7" + birth_year);


               CallableStatement stmt = conn.prepareCall(query);

               // Set the parameter represented by "?" in the query to the id we get from url,
               // num 1 indicates the first "?" in the query
               stmt.setString(1, titlename);
               stmt.setInt(2, Integer.parseInt(year));
               stmt.setString(3, director);
               stmt.setString(4, star);
               stmt.setString(5, genre);
               stmt.setString(6,"@outvalue");
               stmt.setString(6,"@outvalue1");
               stmt.setString(6,"@outvalue2");
               stmt.setString(6,"@outvalue3");

               // statement.setString(2, password);


               // Perform the query
               stmt.executeQuery();

               Statement statement = conn.createStatement();

               query = "SELECT @com_mysql_jdbc_outparam_status,@com_mysql_jdbc_outparam_generatedmovie,@com_mysql_jdbc_outparam_generatedgenre,@com_mysql_jdbc_outparam_generatedstar";

               ResultSet rs = statement.executeQuery(query);

               System.out.println(rs);

               if (rs.next()) {
                   System.out.println(rs);
                   String result = "";
                   String status = rs.getString("@com_mysql_jdbc_outparam_status");
                   String movieid = rs.getString("@com_mysql_jdbc_outparam_generatedmovie");
                   String genreid = rs.getString("@com_mysql_jdbc_outparam_generatedgenre");
                   String starid = rs.getString("@com_mysql_jdbc_outparam_generatedstar");


                   System.out.println(result);
                   result = status;
                   if (status.equals("The movie is successfully added."))
                   {
                       result = result + " " + "Movie ID : " + movieid + "\n" + "Star ID : " + starid + "\n"
                               + "Genre ID : " + genreid + "\n";
                   }

                   responseJsonObject.addProperty("status", "success");
                   responseJsonObject.addProperty("message",result);

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
               stmt.close();
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
        out.close();
    }
}