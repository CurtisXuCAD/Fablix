import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "IndexServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<>();
        }
        // Log to localhost log

        request.getServletContext().log("getting " + previousItems.size() + " items");
        JsonArray previousItemsJsonArray = new JsonArray();
        previousItems.forEach(previousItemsJsonArray::add);
        responseJsonObject.add("previousItems", previousItemsJsonArray);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String item = request.getParameter("id");
        String condition = request.getParameter("condition");
        System.out.println("what is id:"+item);
        HttpSession session = request.getSession();
        System.out.println(condition);
        // get the previous items in a ArrayList

        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");


            if (previousItems == null) {
                previousItems = new ArrayList<>();
                System.out.println("before"+item);
                item = item + "-"+"1";
                previousItems.add(item);
                System.out.println("after"+item);
                session.setAttribute("previousItems", previousItems);
            } else {
                // prevent corrupted states through sharing under multi-threads
                // will only be executed by one thread at a time
                synchronized (previousItems) {
                    int check = 0;
                    for (int i  = 0; i < previousItems.size(); i++)
                    {
                        if ((previousItems.get(i)).contains(item))
                        {
                            String[] splited = (previousItems.get(i)).split("-");
                            int count = Integer.parseInt(splited[2]);
                            if (condition.equals("decrease"))
                            {
                                count--;
                                if (count ==0)
                                {
                                    previousItems.remove(i);
                                }
                                item = item + "-"+count;
                                previousItems.set(i,item);

                            }
                            else if (condition.equals("delete"))
                            {
                                previousItems.remove(i);
                            }
                            else {
                                count ++;
                                item = item + "-"+count;
                                previousItems.set(i,item);
                            }



                            check = 1;
                            break;
                        }
                    }

                    if (check ==0)
                    {
                        item = item + "-" + "1" ;
                        System.out.println(item);
                        previousItems.add(item);
                    }

                }
            }





        JsonObject responseJsonObject = new JsonObject();

        JsonArray previousItemsJsonArray = new JsonArray();
        previousItems.forEach(previousItemsJsonArray::add);
        responseJsonObject.add("previousItems", previousItemsJsonArray);

        response.getWriter().write(responseJsonObject.toString());



    }
}
