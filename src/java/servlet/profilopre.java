package servlet;

import classi.controlli;
import classi.utente;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utilita.Database;
import utilita.FreeMarker;
import utilita.Message;

/**
 *
 * @author caruso
 */
public class profilopre extends HttpServlet {
     @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        HttpSession session = request.getSession(false);
        Map<String, Object> data = new HashMap<>();
        
        
        //Se è stata generata la sessione
        if(session == null){ 

            String action = (String) request.getAttribute("action");
            //String email1 = (String) request.getAttribute("email1");
            String email2 = (String) request.getParameter("email1");
            // Se l'azione non è stata definita o non è valida, impostala come l'azione di login
            if (action == null || (action.equals("login") && action.equals("signup"))) {
                action = "login";
            }
            // Inserisci l'azione nel data-model
            data.put("action", action);

            data.put("message", new Message(request.getParameter("msg"), true));

            data.put("script", "login");
            
            data.put("email",email2);

            FreeMarker.process("profilopre.html", data, response, getServletContext());
        }else {
            response.sendRedirect("login?msg=" + URLEncoder.encode("log", "UTF-8"));
        }
        
    
    }
     /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @return
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if(session == null){
            
            String password        = request.getParameter("password").trim();
            String email           = request.getParameter("email").trim();
            
        
            Message check = null;
        
            if(password.equals("")){
                check = new Message("fld", true);
            }else{
                // Controllo della password
                check = controlli.controllopassword(password);
            }
            
            
        if(!check.isError()){
            
        
            Map<String, Object> data = new HashMap<>();
            data.put("password", password);
            data.put("email", email);
            
                try {
                    Database.updateRecord("user", data, "email='"+ email+"'");
                } catch (SQLException ex) {
                    Logger.getLogger(profilopre.class.getName()).log(Level.SEVERE, null, ex);
                }
            
        
            utente loggato= utente.getUserByEmail(email);
            loggato.initSession2(request.getSession());
             
            response.sendRedirect("profilo");
        
        }else{
            
            response.sendRedirect("profilopre?msg=" + URLEncoder.encode(check.getCode(), "UTF-8"));
            
        }
        
        }else {
            response.sendRedirect("login?msg=" + URLEncoder.encode("log", "UTF-8"));
        }    
    }
   
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet per la gestione del Profilo Precompilato";
    }
}
