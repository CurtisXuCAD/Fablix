import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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


        String name = request.getParameter("name");
        String director = request.getParameter("director");
        String stars = request.getParameter("stars");
        String year = request.getParameter("year");
        String genre = request.getParameter("genre");
        String az = request.getParameter("AZ");

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
        System.out.println("goodsdads" + genre);
        System.out.println(year);
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

            PreparedStatement statement = conn.prepareStatement(query);

            if (!genre.equals(""))
            {
                statement.setString(1, "%"+genre+"%");
            }
            else
            {
                statement.setString(1, "%"+stars+"%");
                statement.setString(2, "%"+name+"%");
                statement.setString(3, year);
                statement.setString(4, "%"+director+"%");
                statement.setString(5, "%"+stars+"%");
                statement.setString(6,  az +"%");
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
}
