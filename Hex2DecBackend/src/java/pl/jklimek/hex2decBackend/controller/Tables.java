/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import pl.jklimek.hex2decBackend.model.ConvertedPair;
import pl.jklimek.hex2decBackend.model.JavaConverter;
import pl.jklimek.hex2decBackend.model.JdbcJavaConverter;
import pl.jklimek.hex2decBackend.model.OutOfBoundsException;

/**
 * Servlet displaying model data and changes history that happened during last session
 * @author Jakub Klimek
 * @version 1
 */
public class Tables extends HttpServlet {

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
                model = new JdbcJavaConverter(100,url, user, password );
                request.getServletContext().setAttribute("model", model);
            } catch (OutOfBoundsException ex) {
            }
            catch(SQLException ex){
                throw new ServletException("Couldn't establish db connection");
            }
        }
        ConvertedPair[] rows = null;
        try{
            rows = model.getAllPairs();
        }
        catch(SQLException e){
            throw new ServletException("Couldn't fetch data from db");
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
            out.println("<title>Servlet Tables</title>");            
            out.println("</head>");
            out.println("<body bgcolor=\"" + color + "\">");
            out.println("<h1>Amount of rows: " + rows.length + "</h1>");
            out.println("<a href=\"" + request.getContextPath() + "/settings\">Settings</a>");
            out.println("<table border=\"1\"");
            out.println("<tr><th>Hex</th><th>Dec</th><th>Change</th></tr>");
            int index = 0;
            for(ConvertedPair row : rows){
                out.println("<tr><td>" + row.getHex() + "</td><td>" + row.getDec() + "</td><td><a href=\"" + request.getContextPath() + "/settings?changeRow=" + index + "\">Change</a></td></tr>");
                index++;
            }
            out.println("</table><br><br>");
            if(!history.isEmpty()){
                out.println("<h2>History</h2>");
                for(String event: history){
                    out.println("<p>" + event + "</p>");
                }
            }
            out.println("</body>");
            out.println("</html>");
        }
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
        return "Data presenting servlet";
    }// </editor-fold>
    
    /**
     * Default constructor
     */
    public Tables(){
        
    }
}
