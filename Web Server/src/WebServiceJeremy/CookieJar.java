package WebServiceJeremy;
import javax.servlet.http.*;

/** Methods for handling cookies
 */
public class CookieJar {

    /** Search the request object for a cookie by name and return it (ie. the cookie)
     */
    public static Cookie getCookie(HttpServletRequest request, String cname) {
        Cookie[] cookies = request.getCookies();
        
        if (cookies == null)        // Check to see if there are any cookies
            return null;
        for (Cookie c : cookies) {
            if (cname.equals(c.getName()))
                return c;
        }
        return null;
    }
        
    /** Search the request object for a cookie and return its value
     */
    public static String getCookieValue(HttpServletRequest request, String cname) {
        String value = null;
        Cookie[] cookies = request.getCookies();
        
        for (Cookie c : cookies) {
            if (cname.equals(c.getName()))
                value = c.getValue();
        }
        return value;
    }
        
    public static int deleteCookies(Cookie[] cookies) {
        int count = 0;
        if (cookies == null)
            return 0;
        for (Cookie c : cookies) {
            c.setMaxAge(0);
            count++;
        }
        return count;
    }
}
