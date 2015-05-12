package WebServiceJeremy;

import javax.servlet.http.HttpSession;

public class LoginStatusTemp {
	public static String displayLoginWidget(HttpSession session){
		String status = (String) session.getAttribute("status");
		
		if(status != null && status.equals("Registered")){
			return loggedIn();
		} else {
			return loggedOut();
		}
	}
	
	public static String loggedIn(){
		String html = "<li><a href='logout'>Logout</a>";
		return html;
	}
	
	public static String loggedOut(){
		String html = "<li><a href='login'>Login</a>";
		return html;
	}
}
