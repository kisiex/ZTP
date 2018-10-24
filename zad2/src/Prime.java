import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "Prime131726")
public class Prime extends HttpServlet {

    //3k + 7 >= n && isPrime( 3k + 7 )
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int number = Integer.parseInt(request.getParameter("n"));
        int first = (number - 7) / 3;

        first = 3 * first + 7;
        while (first > number) {
            first -= 3;
        }

        while (true) {
            if (isPrime(first)) {
                break;
            }
            first -= 3;
        }

        ServletOutputStream out = response.getOutputStream();
        out.print(first);
    }

    //3k + 7 < n && isPrime( 3k + 7 )
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int number = Integer.parseInt(request.getParameter("n"));
        int first = (number - 7) / 3;

        first = 3 * first + 7;
        while (first < number) {
            first += 3;
        }

        while (true) {
            if (isPrime(first)) {
                break;
            }
            first += 3;
        }

        ServletOutputStream out = response.getOutputStream();
        out.print(first);
    }

    private boolean isPrime(int number) {
        if (number % 2 == 0) {
            return false;
        }
        for (int i = 3; i * i <= number; i += 2) {
            if (number % i == 0)
                return false;
        }
        return true;
    }
}

