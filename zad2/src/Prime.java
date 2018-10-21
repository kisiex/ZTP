import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "Prime131726")
public class Prime extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int number = Integer.parseInt(request.getParameter("n"));

        System.out.println(number);
        ServletOutputStream out = response.getOutputStream();
        out.print(number);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int number = Integer.parseInt(request.getParameter("n"));

        System.out.println(number);
        ServletOutputStream out = response.getOutputStream();
        out.print(number);
    }
}
