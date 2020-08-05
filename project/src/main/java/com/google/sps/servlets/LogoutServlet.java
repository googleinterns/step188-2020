import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/logout-url")
public class LogoutServlet extends HttpServlet {
  /**
   * Sends logout url in response.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws IOException if Input/Output error occurs
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    String logoutUrl =
        UserServiceFactory.getUserService().createLogoutURL(/* urlToRedirectToAfterLogout= */ "/");
    response.getWriter().println(logoutUrl);
  }
}
