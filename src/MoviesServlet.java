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
        String sortBy1 = (String)request.getParameter("sortBy1");
        String order1 = (String)request.getParameter("order1");
        String sortBy2 = (String)request.getParameter("sortBy2");
        String order2 = (String)request.getParameter("order2");

        System.out.println(stars);

        //sort information to session
        HttpSession session = request.getSession();
        String current_url = "movie.html?name=" + name + "&director=" + director + "&stars=" +
            stars + "&year=" + year + "&genre=" + genre + "&AZ=" +
            az + "&numRecords=" + numRecords + "&startIndex=" +
            startIndex + "&totalResults=" + totalResults + "&sortBy1=" + sortBy1 + "&order1=" + order1 +
            "&sortBy2=" + sortBy2 + "&order2=" + order2;
        session.setAttribute("prev_url", current_url);

        if(sortBy1.equals("rating")){ sortBy1 = "ISNULL(rating), "+sortBy1;}
        if(sortBy2.equals("rating")){ sortBy2 = "ISNULL(rating), "+sortBy2;}

        // System.out.println(current_url);
        
        String query = "";
        //start building query
        if(!(genre.equals("null") || genre == null || genre.equals(""))) {
            // query += "SELECT m.id, m.title, m.year, m.director \n" +
            // "from movies as m, stars as s, stars_in_movies as sim \n" +
            // "where m.title LIKE '%sth%'' \n" +
            // "and m.year like '%'' \n" +
            // "and m.director LIKE '%%'' \n" +
            // "and sim.movieId = m.id and s.id = sim.starId and s.name LIKE '%%' \n" +
            // "group by m.id";
            query += "SELECT m.id, m.title, m.year, m.director, r.rating \n" +
            "from movies as m LEFT join ratings as r on r.movieId = m.id, genres as g, genres_in_movies as gim \n" +
            "where g.name like ? and gim.genreId = g.id and m.id = gim.movieId \n" +
            "group by m.id";
        }
        else if(!(az.equals("null") || az == null || az.equals(""))){
            if (az.equals("*"))
            {                
                query += "SELECT m.id, m.title, m.year, m.director, r.rating \n" +
                "from movies as m LEFT join ratings as r on r.movieId = m.id \n" +
                "where m.title not REGEXP \'^[0-9A-Za-z]\' " +
                "group by m.id";
            }
            else{
                query += "SELECT m.id, m.title, m.year, m.director, r.rating \n" +
                "from movies as m LEFT join ratings as r on r.movieId = m.id \n" +
                "where m.title like ? \n" +
                "group by m.id";
            }
        }
        else{
            if(!(stars.equals("null") || stars == null || stars.equals(""))){
                query += "SELECT m.id, m.title, m.year, m.director, r.rating \n" +
                "from movies as m LEFT join ratings as r on r.movieId = m.id, (SELECT movieId FROM stars as s, stars_in_movies as sim \n" +
                "where sim.starId = s.id and s.name LIKE ? group by movieId) as mid \n" +
                "where mid.movieId = m.id \n" +
                "and m.title LIKE ? \n" +
                "and m.year like ? \n" +
                "and m.director LIKE ? \n" +
                "group by m.id";
            }
            else{
                query += "SELECT m.id, m.title, m.year, m.director, r.rating \n" +
                    "from movies as m LEFT join ratings as r on r.movieId = m.id \n" +
                    "where m.title LIKE ? \n" +
                    "and m.year like ? \n" +
                    "and m.director LIKE ? \n" +
                    "group by m.id";
            }
        }

        if (year.equals("")){year = "%";}
        // if (genre.equals("null"))
        // {
        //     genre = "";

        // }

        // if (az.equals("null"))
        // {
        //     az = "";
        // }

        if (!(sortBy1 == null || sortBy1.equals("null"))) {
            query += " order by ? " + sortBy1;
            if(!(order1 == null || order1.equals("null"))){
                query += " ? " + order1;
            }
        }

        if (!(sortBy2 == null || sortBy2.equals("null"))) {
            query += ", ? " + sortBy2;
            if(!(order2 == null || order2.equals("null"))){
                query += " ? " + order2;
            }
        }

        // String queryResultLimit = " limit " + numRecords + " offset " + startIndex + " ";
        String queryResultLimit = " limit ? " + " offset ? " + " ";

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            /*String query;

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



            }*/

            //try to get total results at first time
            if(totalResults == null || totalResults.equals("null") || totalResults.equals("")){
                String count_query = "select count(*) as c from (" + query + ") as fc";
                System.out.println(count_query); 
                PreparedStatement count_statement = conn.prepareStatement(count_query);
                int ps_idex = 0;
                if (!(genre.equals("null") || genre == null || genre.equals(""))){
                    count_statement.setString(1, "%"+genre+"%");
                    ps_idex = 1;
                }
                else if(!(az.equals("null") || az == null || az.equals(""))){
                    if (!az.equals("*"))
                    {
                        count_statement.setString(1, az+"%");
                        ps_idex = 1;
                    }
                }
                else
                {            
                    if(!(stars.equals("null") || stars == null || stars.equals(""))){
                        count_statement.setString(1, "%"+stars+"%");
                        count_statement.setString(2, "%"+name+"%");
                        count_statement.setString(3, year);
                        count_statement.setString(4, "%"+director+"%");
                        ps_idex = 4;
                    }
                    else{
                        count_statement.setString(1, "%"+name+"%");
                        count_statement.setString(2, year);
                        count_statement.setString(3, "%"+director+"%");
                        ps_idex = 3;
                    }
                }

                if (!(sortBy1 == null || sortBy1.equals("null"))) {
                    count_statement.setString(++ps_idex,sortBy1);
                    System.out.print("pi: ");
                    System.out.println(ps_idex);
                    if(!(order1 == null || order1.equals("null"))){
                        count_statement.setString(++ps_idex,order1);
                        System.out.print("pi: ");
                        System.out.println(ps_idex);
                    }
                }
        
                if (!(sortBy2 == null || sortBy2.equals("null"))) {
                    count_statement.setString(++ps_idex,sortBy2);
                    if(!(order2 == null || order2.equals("null"))){
                        count_statement.setString(++ps_idex,order2);
                    }
                }   

                ResultSet rs1 = count_statement.executeQuery();
                while (rs1.next()) {
                    totalResults = rs1.getString("c");
                }
                rs1.close();
            }

            System.out.println(totalResults);
   
            query += queryResultLimit;

            System.out.println(query);

            PreparedStatement statement = conn.prepareStatement(query);

            int ps_idex = 0;
            if (!(genre.equals("null") || genre == null || genre.equals(""))){
                statement.setString(1, "%"+genre+"%");
                ps_idex = 1;
            }
            else if(!(az.equals("null") || az == null || az.equals(""))){
                if (!az.equals("*"))
                {
                    statement.setString(1, az+"%");
                    ps_idex = 1;
                }
            }
            else
            {            
                if(!(stars.equals("null") || stars == null || stars.equals(""))){
                    statement.setString(1, "%"+stars+"%");
                    statement.setString(2, "%"+name+"%");
                    statement.setString(3, year);
                    statement.setString(4, "%"+director+"%");
                    ps_idex = 4;
                }
                else{
                    statement.setString(1, "%"+name+"%");
                    statement.setString(2, year);
                    statement.setString(3, "%"+director+"%");
                    ps_idex = 3;
                }
            }

            if (!(sortBy1 == null || sortBy1.equals("null"))) {
                statement.setString(++ps_idex,sortBy1);
                if(!(order1 == null || order1.equals("null"))){
                    statement.setString(++ps_idex,order1);
                }
            }
    
            if (!(sortBy2 == null || sortBy2.equals("null"))) {
                statement.setString(++ps_idex,sortBy2);
                if(!(order2 == null || order2.equals("null"))){
                    statement.setString(++ps_idex,order2);
                }
            }

            System.out.print("pi: ");
            System.out.println(ps_idex);
            statement.setInt(++ps_idex,Integer.parseInt(numRecords));
            System.out.print("pi: ");
            System.out.println(ps_idex);
            statement.setInt(++ps_idex,Integer.parseInt(startIndex));

            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year= rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");
                // System.out.println("movie_rating: "+movie_rating);
                
                // String sub_query = "select substring_index(group_concat(DISTINCT g.name order by g.name asc separator ', '), ', ' , 3) as gnames, \n" +
                // "substring_index(group_concat(DISTINCT CONCAT_WS('-', s.id, s.name) order by s.name asc separator ', '), ', ' , 3) as snames, \n" +
                // "r.rating as rating\n" +
                // "from genres as g, genres_in_movies as gim, ratings as r, stars as s, stars_in_movies as sim \n" +
                // "where gim.genreId = g.id and gim.movieId = ? \n" +
                // "and sim.starId = s.id and sim.movieId = ? \n" +
                // "and r.movieId = ?";

                // PreparedStatement single_info_statement = conn.prepareStatement(sub_query);
                // single_info_statement.setString(1, movie_id);
                // single_info_statement.setString(2, movie_id);
                // single_info_statement.setString(3, movie_id);

                // ResultSet sub_rs = single_info_statement.executeQuery();
                // String movie_gnames = "";
                // String movie_snames = "";
                // String movie_rating = "";
                // if(sub_rs.next()){
                //     movie_gnames= sub_rs.getString("gnames");
                //     movie_snames= sub_rs.getString("snames");
                //     movie_rating= sub_rs.getString("rating");
                // }
                // sub_rs.close();

                String sub_query_gnames = "select substring_index(group_concat(DISTINCT g.name order by g.name asc separator ', '), ', ' , 3) as gnames \n" +
                "from genres as g, genres_in_movies as gim \n" +
                "where gim.genreId = g.id and gim.movieId = ? ";
                PreparedStatement gnames_statement = conn.prepareStatement(sub_query_gnames);
                gnames_statement.setString(1, movie_id);
                ResultSet gnames_rs = gnames_statement.executeQuery();
                String movie_gnames = "null";
                if(gnames_rs.next()){
                    movie_gnames= gnames_rs.getString("gnames");
                }
                gnames_rs.close();

                String sub_query_snames = "select substring_index(group_concat(DISTINCT CONCAT_WS('-', s.id, s.name) order by s.name asc separator ', '), ', ' , 3) as snames \n" +
                "from stars as s, stars_in_movies as sim \n" +
                "where sim.starId = s.id and sim.movieId = ? ";
                PreparedStatement snames_statement = conn.prepareStatement(sub_query_snames);
                snames_statement.setString(1, movie_id);
                ResultSet snames_rs = snames_statement.executeQuery();
                String movie_snames = "null";
                if(snames_rs.next()){
                    movie_snames= snames_rs.getString("snames");
                }
                snames_rs.close();

                // String sub_query_rating = "select r.rating as rating\n" +
                // "from ratings as r \n" +
                // "where r.movieId = ?";
                // PreparedStatement rating_statement = conn.prepareStatement(sub_query_rating);
                // rating_statement.setString(1, movie_id);
                // ResultSet rating_rs = rating_statement.executeQuery();
                // String movie_rating = "null";
                // if(rating_rs.next()){
                //     movie_rating= rating_rs.getString("rating");
                // }
                // rating_rs.close();

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
            jsonObject.addProperty("sortBy1", sortBy1);
            jsonObject.addProperty("order1", order1);
            jsonObject.addProperty("sortBy2", sortBy2);
            jsonObject.addProperty("order2", order2);
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
