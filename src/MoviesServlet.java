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
import java.util.ArrayList;
import java.util.Enumeration;


// Declaring a WebServlet called MoviesServlet, which maps to url "/api/movies"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
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

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type


        String name = (String)request.getParameter("name");
        String director = (String)request.getParameter("director");
        String stars = (String)request.getParameter("stars");
        String year =(String) request.getParameter("year");
        String genre = (String)request.getParameter("genre");
        String az = (String)request.getParameter("AZ");
        String numRecords = (String)request.getParameter("numRecords");
        String startIndex = (String)request.getParameter("startIndex");
        String totalResults = (String)request.getParameter("totalResults");
        String sortBy = (String)request.getParameter("sortBy");
        String order = (String)request.getParameter("order");

        String queryResultLimit = " limit " + numRecords + " offset " + startIndex + " ";

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        if (year.equals(""))
        {
            year = "%";
        }



        if (genre.equals("null"))
        {
            genre = "";

        }

        if (az.equals("null"))
        {
            az = "";
        }




        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            String query;

            if (!genre.equals(""))
            {
                query = "SELECT m.id, m.title, m.year, m.director, \n" +
                        "substring_index(group_concat(DISTINCT g.name separator ', '), ', ' , 3) as gnames, \n" +
                        "            substring_index(group_concat(DISTINCT CONCAT_WS('-', s.id, s.name) separator ', '), ', ' , 3) as snames, \n" +
                        "            r.rating \n" +
                        "            from movies as m, genres as g, genres_in_movies as gim, ratings as r, stars as s, stars_in_movies as sim , \n" +
                        "            \n" +
                        "            (SELECT m.id\n" +
                        "\t\t\tFROM movies as m, stars_in_movies as sm, stars as s,genres as g, genres_in_movies as gim, ratings as r\n" +
                        "\t\t\twhere sm.movieId = m.id and s.id = sm.starId and g.name like ? and\n" +
                        "\t\t\tm.id = gim.movieId  and gim.genreId = g.id and m.id = r.movieId \n" +
                        "\t\t\tgroup by m.id) as m1\n" +
                        "            \n" +
                        "            \n" +
                        "            where m.id = m1.id and m1.id = gim.movieId and m1.id = sim.movieId and gim.genreId = g.id and sim.starId = s.id and m1.id = r.movieId \n" +
                        "            group by m.id ";
            }

            else
            {
                if (az.equals("*"))
                {
                    query = "SELECT m.id, m.title, m.year, m.director,\n" +
                            "                        substring_index(group_concat(DISTINCT g.name separator ', '), ', ' , 3) as gnames, \n" +
                            "                                    substring_index(group_concat(DISTINCT CONCAT_WS('-', s.id, s.name) order by case when s.name then -1 end separator ', '), ', ' , 3) as snames,\n" +
                            "                                  r.rating \n" +
                            "                        FROM movies as m, stars_in_movies as sm, stars as s,genres as g, genres_in_movies as gim, ratings as r\n" +
                            "                        where m.title not REGEXP '^[0-9A-Za-z]' and sm.movieId = m.id and s.id = sm.starId  and\n" +
                            "                       m.id = gim.movieId and gim.genreId = g.id and m.id = r.movieId \n" +
                            "                       group by m.id";
                }
                else
                {
                    query = "SELECT m.id, m.title, m.year, m.director, \n" +
                            "substring_index(group_concat(DISTINCT g.name separator ', '), ', ' , 3) as gnames, \n" +
                            "            substring_index(group_concat(DISTINCT CONCAT_WS('-', s.id, s.name) order by case when s.name like ? then -1 end separator ', '), ', ' , 3) as snames, \n" +
                            "            r.rating \n" +
                            "            from movies as m, genres as g, genres_in_movies as gim, ratings as r, stars as s, stars_in_movies as sim , \n" +
                            "            \n" +
                            "            (SELECT m.id\n" +
                            "\t\t\tFROM movies as m, stars_in_movies as sm, stars as s,genres as g, genres_in_movies as gim, ratings as r\n" +
                            "\t\t\twhere m.title LIKE ? and m.year like ? and m.director LIKE ? and sm.movieId = m.id and s.id = sm.starId and s.name LIKE ? and\n" +
                            "\t\t\tm.id = gim.movieId  and m.title LIKE ? and gim.genreId = g.id and m.id = r.movieId \n" +
                            "\t\t\tgroup by m.id) as m1\n" +
                            "            \n" +
                            "            \n" +
                            "            where m.id = m1.id and m1.id = gim.movieId and m1.id = sim.movieId and gim.genreId = g.id and sim.starId = s.id and m1.id = r.movieId \n" +
                            "            group by m.id ";
                }



            }

            if(totalResults == null || totalResults.equals("null")){
                String count_query = "select count(*) as c from (" + query + ") as fc";
                PreparedStatement count_statement = conn.prepareStatement(count_query);
                if (!genre.equals(""))
                {
                    count_statement.setString(1, "%"+genre+"%");
                }
                else
                {
                    if (!az.equals("*"))
                    {
                        count_statement.setString(1, "%"+stars+"%");
                        count_statement.setString(2, "%"+name+"%");
                        count_statement.setString(3, year);
                        count_statement.setString(4, "%"+director+"%");
                        count_statement.setString(5, "%"+stars+"%");
                        count_statement.setString(6,  az +"%");
                    }
                }
                ResultSet rs1 = count_statement.executeQuery();
                while (rs1.next()) {
                    totalResults = rs1.getString("c");
                }
                rs1.close();
            }

            if (!(sortBy == null || sortBy.equals("null"))) {
                query += "order by " + sortBy + " " + order;
                if(sortBy.equals("title")){
                    query += ", rating";
                }
                else if(sortBy.equals("rating")){
                    query += ", title";
                }
            }
            
            query += queryResultLimit;


            System.out.println(query);

            PreparedStatement statement = conn.prepareStatement(query);

            if (!genre.equals(""))
            {
                statement.setString(1, "%"+genre+"%");
            }
            else
            {
                if (!az.equals("*"))
                {
                    statement.setString(1, "%"+stars+"%");
                    statement.setString(2, "%"+name+"%");
                    statement.setString(3, year);
                    statement.setString(4, "%"+director+"%");
                    statement.setString(5, "%"+stars+"%");
                    statement.setString(6,  az +"%");
                }

            }

            ResultSet rs = statement.executeQuery();


            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year= rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_gnames= rs.getString("gnames");
                String movie_snames= rs.getString("snames");
                String movie_rating= rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_gnames", movie_gnames);
                jsonObject.addProperty("movie_snames", movie_snames);
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);
            }
            
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("numRecords", numRecords);
            jsonObject.addProperty("startIndex", startIndex);
            jsonObject.addProperty("totalResults", totalResults);
            jsonObject.addProperty("sortBy", sortBy);
            jsonObject.addProperty("order", order);
            jsonArray.add(jsonObject);
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


    }
}
