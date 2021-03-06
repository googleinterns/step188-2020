import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login-url")
public class LoginServlet extends HttpServlet {
  /**
   * Sends login url in response.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws IOException if Input/Output error occurs
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    String loginUrl =
        UserServiceFactory.getUserService()
            .createLoginURL(/* urlToRedirectToAfterLogin= */ "/profile.html");
    response.getWriter().println(loginUrl);
  }
}
