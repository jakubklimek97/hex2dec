package pl.jklimek.hex2decBackend.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import pl.jklimek.hex2decBackend.model.BadValueException;
import pl.jklimek.hex2decBackend.model.JdbcJavaConverter;
import pl.jklimek.hex2decBackend.model.OutOfBoundsException;

/**
 * Settings servlet to change app settings
 * @author Jakub Klimek
 * @version 2
 */
public class Settings extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(true);
        ArrayList<String> history = (ArrayList<String>)session.getAttribute("history");
        if(history == null){
            history = new ArrayList<String>();
        }
        
        Cookie[] cookies = request.getCookies();
        String color;
        if(cookies != null){
            Optional<String> colorO = Arrays.stream(cookies).filter(c -> c.getName().equals("H2color")).map(Cookie::getValue).findFirst();
            color = colorO.orElse("#ffffff");
        }
        else{
            color = "#ffffff";
        }
        
        
        JdbcJavaConverter model = (JdbcJavaConverter)request.getServletContext().getAttribute("model");
        
        String url, user, password;
        url = request.getServletContext().getInitParameter("dbUrl");       
        user = request.getServletContext().getInitParameter("dbUser");
        password = request.getServletContext().getInitParameter("dbPassword");

        
        if(model == null){
            try {
                model = new JdbcJavaConverter(100, url, user, password);
                request.getServletContext().setAttribute("model", model);
            } catch (OutOfBoundsException ex) {
                //can't happen
                throw new ServletException();
            }
            catch(SQLException ex){
                throw new ServletException("Couldn't establish db connection");
            }
        }
        
        String rowsNumber = request.getParameter("rowNo");
        Boolean rowsNumberChanged = rowsNumber == null ? false : true;
        
        String notification = new String();
        
        if(rowsNumberChanged){
            try {
                model.setQueueSize(rowsNumber);
                history.add("Queue size changed");
            } catch(SQLException e){
                notification = "Error ruding communication with db.";
            }catch (OutOfBoundsException ex) {
                notification = "Entered new table size is out of acceptable bounds.";
            } catch (NumberFormatException ex) {
                notification = "Entered new table size is not a number.";
            }
        }
        String rowToChange = request.getParameter("changeRow");
        String h2DConversion = request.getParameter("H2D");
        String d2HConversion = request.getParameter("D2H");
        String setDec = request.getParameter("setDec");
        String setHex = request.getParameter("setHex");
        String colorP = request.getParameter("setColor");
        if(colorP != null){
            color = colorP;
            history.add("Color changed");
        }
        if(h2DConversion != null){
            model.convertToDec();
            history.add("Hex->Dec conversion");
        }
        
        if(d2HConversion != null){
            model.convertToHex();
            history.add("Dec->Hex conversion");
        }
        
        if(setDec != null || setHex != null){
            String value = request.getParameter("value");
            String row = request.getParameter("row");
            if(value != null && row != null){
                    try {
                        if(setDec != null){
                            model.setDecValue(row, value);
                            history.add("Decimal value changed");
                        }
                        else{
                            model.setHexValue(row, value);
                            history.add("Hexadecimal value changed");
                        }
                    }catch(SQLException e){
                        notification = addError(notification, "Error during db comunication.");
                    }catch (OutOfBoundsException ex) {
                        notification = addError(notification, "Row index is invalid.");
                    } catch (BadValueException ex) {
                        notification = addError(notification, "Provided value was not correct number.");
                    } catch (NumberFormatException ex) {
                        notification = addError(notification, "Provided values were not numbers.");
                    } 
            }
            else{
                notification = addError(notification, "Not sufficient amount of paramenters to change cell value.");
            }
        }
        
        Cookie colorC = new Cookie("H2color", color);
        colorC.setMaxAge(7200);
        response.addCookie(colorC);
        session.setAttribute("history", history);
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Settings</title>");            
            out.println("</head>");
            out.println("<body bgcolor=\"" + color + "\">");
            if(!notification.isEmpty()){
                out.println("<div class=\"Error\">" + notification + "</div>");
            }
            out.println("<h1>Hex2Dec Settings</h1>");
            out.println("<a href=\"" + request.getContextPath() +"/tables\">View conversion table</a>");
            out.println("<form method=\"get\" action=\"" + request.getContextPath()+ "/settings\"");
            out.println("<label>Number of rows: </label>");
            out.println("<input type=\"text\" name=\"rowNo\" value=\"" + model.getQueueSize() +  "\">");
            out.println("<input type=\"submit\">");
            out.println("</form>");
            
            out.println("<form method=\"post\" action=\"" + request.getContextPath()+ "/settings\"");
            out.println("<label>Set Decimal value: </label>");
            out.println("<input type=\"number\" name=\"value\">");
            out.println("<label>Row number: </label>");
            out.println("<input type=\"number\" name=\"row\" value=\""+ (rowToChange == null ? "" : rowToChange) + "\">");
            out.println("<input type=\"submit\" name=\"setDec\" value=\"OK\">");
            out.println("</form>");
            
            out.println("<form method=\"post\" action=\"" + request.getContextPath()+ "/settings\"");
            out.println("<label>Set Hexadecimal value: </label>");
            out.println("<input type=\"text\" name=\"value\">");
            out.println("<label>Row number: </label>");
            out.println("<input type=\"number\" name=\"row\" value=\""+ (rowToChange == null ? "" : rowToChange) + "\">");
            out.println("<input type=\"submit\" name=\"setHex\" value=\"OK\">");
            out.println("</form>");
            
            out.println("<a href=\"" + request.getContextPath() +"/settings?H2D=1\">Perform Hex -> Dec conversion</a><br>");
            out.println("<a href=\"" + request.getContextPath() +"/settings?D2H=1\">Perform Dec -> Hex conversion</a><br>");
            
            
            out.println("<form method=\"get\" action=\"" + request.getContextPath()+ "/settings\"");
            out.println("<label>Background: </label>");
            out.println("<input type=\"color\" name=\"setColor\" value=\"" + color + "\">");
            out.println("<input type=\"submit\">");
            out.println("</form>");
            
            
            out.println("</body>");
            out.println("</html>");
        }
    }
    /**
     * Adds new error message to be displayed to user
     * @param notification reference to String object containing pending messages
     * @param message message to be displayed
     * @return appended notification
     */
    private String addError(String notification, String message){
        if(!notification.isEmpty()){
            notification = notification + "<br>" + message;
        }else{
            notification = notification +  message;
        }
        return notification;
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet to change settings";
    }// </editor-fold>
    
    /**
     * Default constructor
     */
    public Settings(){
        
    }
}
