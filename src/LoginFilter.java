import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String scheme = httpRequest.getScheme();
        String serverName = httpRequest.getServerName();
        int serverPort = httpRequest.getServerPort();
        String contextPath = httpRequest.getContextPath();  // includes leading forward slash

        String resultPath = scheme + "://" + serverName + ":" + serverPort + contextPath;
        System.out.println("Result path: " + resultPath);

        System.out.println("LoginFilter: " + httpRequest.getRequestURI());

        // Check if this URL is allowed to access without logging in
        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            System.out.println("isAllow");
            // Keep default action: pass along the filter chain
            // System.out.println(httpRequest.getSession().getAttribute("logged_in"));
            if(httpRequest.getSession().getAttribute("logged_in") != null) {
                if ((Boolean)httpRequest.getSession().getAttribute("logged_in") == true) {
                    httpResponse.sendRedirect(resultPath+"/main.html");
                    System.out.println("LoginFilter: to main");
                }
            }
            else{
                chain.doFilter(request, response);
            }
        }
        else{
            // System.out.println("NoAllow");
            // Redirect to login page if the "user" attribute doesn't exist in session
            if (httpRequest.getSession().getAttribute("user") == null) {
                System.out.println("LoginFilter: to login");
                httpResponse.sendRedirect(resultPath+"/login.html");
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");
        allowedURIs.add("fablix/");
        allowedURIs.add("login.css");
        allowedURIs.add("inception.jpg");
        allowedURIs.add("pic/login.html");
    }

    public void destroy() {
        // ignored.
    }

}
