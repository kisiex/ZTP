import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Adam Kisielewski
 * version 1.0
 */

@WebServlet(name = "servlet131726")
public class Prime extends HttpServlet {

    private static final long SERIAL_VERSION_UID = 42L;

    /**
     * @param request  HttpServletRequest contains parameter 'n', for which
     *                 the greatest prime number lower than value of 'n'
     *                 and can be represented as 3k + 7 is returned
     * @param response HttpServletResponse
     * @throws ServletException when something bad happened with servlet
     * @throws IOException      when could not get output stream
     */
    //3k + 7 >= n && isPrime( 3k + 7 )
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        int number = Integer.parseInt(request.getParameter("n"));
        int first = calculateFirstNumber(number, false);

        while (isNotPrime(first)) {
            first -= 3;
        }

        ServletOutputStream out = response.getOutputStream();
        out.print(first);
    }

    /**
     * @param request  HttpServletRequest contains parameter 'n', for which
     *                 the lowest prime number greater than value of 'n'
     *                 and can be represented as 3k + 7 is returned
     * @param response HttpServletResponse
     * @throws ServletException when something bad happened with servlet
     * @throws IOException      when could not get output stream
     */
    //3k + 7 < n && isPrime( 3k + 7 )
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        int number = Integer.parseInt(request.getParameter("n"));
        int first = calculateFirstNumber(number, true);

        while (isNotPrime(first)) {
            first += 3;
        }

        ServletOutputStream out = response.getOutputStream();
        out.print(first);
    }

    /**
     * @param start first number
     * @param inc   determine if program should increase or decrease start
     * @return first number that can be represented as '3k + 7',
     * lower (for inc = false) or greater (for inc = true) than 'n'
     */
    private int calculateFirstNumber(int start, boolean inc) {
        // to make sure number can be represented as '3k + 7'
        int first = (start - 7) / 3;
        first = 3 * first + 7;

        if (inc) {
            while (first < start) {
                first += 3;
            }
        } else {
            while (first > start) {
                first -= 3;
            }
        }

        return first;
    }

    /**
     * @param number given number to check
     * @return information, if givem number is not prime number
     */
    private boolean isNotPrime(int number) {
        if (number % 2 == 0) {
            return true;
        }
        for (int i = 3; i * i <= number; i += 2) {
            if (number % i == 0)
                return true;
        }
        return false;
    }
}

