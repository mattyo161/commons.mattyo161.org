package org.mattyo161.isql;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.sql.*;

import javax.naming.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;

import net.cabot.lyris.LyrisServer;

import org.mattyo161.commons.db.*;
import org.mattyo161.commons.util.MyEnvironment;

@WebServlet("/LyrIsql")
public class LyrIsql extends HttpServlet {
    
    /** Holds value of property publication. */
    private String publication;    
    

    public LyrIsql() {
        super();
        // TODO Auto-generated constructor stub
    }
     public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        //print the HTML header </font>
        StringBuffer head = new StringBuffer();
        head.append("<head>")
        	.append("<title>LyrIsql Tester</title>")
        	.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + request.getContextPath() + "/css/styles.css\" />")
        	.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + request.getContextPath() + "/plugins/datatables/css/jquery.dataTables.css\" />")
        	.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + request.getContextPath() + "/plugins/scroller/css/dataTables.scroller.css\" />")
        	.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + request.getContextPath() + "/plugins/tabletools/css/TableTools.css\" />")
        	.append("<script src=\"http://code.jquery.com/jquery-1.9.1.min.js\"></script>")
        	.append("<script src=\"http://code.jquery.com/jquery-migrate-1.1.1.min.js\"></script>")
        	.append("</head>")
        	;
        out.println("<html>");
        out.println(head.toString());
        out.println("<body bgcolor=\"white\">");
        
        MyEnvironment env = MyEnvironment.getEnvironment();
        // find available Sources
        
        // Extract values from request
//        String reqSource = request.getParameter("source");
//        String reqType = request.getParameter("type");
//        String reqServer = request.getParameter("server");
//        String reqPort = request.getParameter("port");
//        String reqUser = request.getParameter("user");
//        String reqPassword = request.getParameter("password");
//        String reqDb = request.getParameter("db");
        String reqSql = request.getParameter("sql");
//        String reqDbUrl = request.getParameter("dburl");
        int reqRecStart = 1;
        int reqRecCount = 50;
        
//        if (reqSource == null) reqSource = "";
//        if (reqType == null) reqType = "";
//        if (reqServer == null) reqServer = "";
//        if (reqPort == null) reqPort = "";
//        if (reqUser == null) reqUser = "";
//        if (reqPassword == null) reqPassword = "";
//        if (reqDb == null) reqDb = "";
        if (reqSql == null) reqSql = "";
//        if (reqDbUrl == null) reqDbUrl = "";
        if (request.getParameter("recstart") != null) {
            reqRecStart = Integer.parseInt(request.getParameter("recstart"));
        }
        if (request.getParameter("reccount") != null) {
            reqRecCount = Integer.parseInt(request.getParameter("reccount"));
        }
        
        // If reqSource is not empty then we will setup this source and load the values
        // into the variables, it will also disable any of the other choices the user can
        // set the source to blank if they want to make modifications.
        DBConnection conn = null;
        String inputDisabled = "";
        
        
        // Lets create a basic form here.
        int leftWidth = 30;
        out.println("<form name=\"isql\" action=\"\" method=\"post\">");
        out.println("<table border=\"0\" width=\"90%\">");
            out.println("<tr>");
                out.println("<td valign=\"top\" align=\"right\"><b>SQL:</b><br /><a href=\"IsqlDocs.html\" target=\"IsqlDocs\">[Docs]</a></td>");
                out.println("<td colspan=\"5\">");
                    out.print("<textarea name=\"sql\" cols=\"80%\" rows=\"6\">");
                    out.print(reqSql);
                    out.println("</textarea>");
                out.println("</td>");
            out.println("</tr>");
            out.println("<tr>");
                out.println("<th width=\"" + leftWidth + "\">RecStart:</th>");
                out.println("<td width=\"" + leftWidth + "\"><input type=\"text\" size=\"10\" name=\"recstart\" value=\"" + reqRecStart + "\">");
                out.println("<th width=\"" + leftWidth + "\">RecCount:</th>");
                out.println("<td width=\"" + leftWidth + "\"><input type=\"text\" size=\"10\" name=\"reccount\" value=\"" + reqRecCount + "\">");
                out.println("<td width=\"" + leftWidth + "\">&nbsp</td>");
                out.println("<td width=\"" + leftWidth + "\">&nbsp</td>");
            out.println("</tr>");
            out.println("<tr><td colspan=\"6\"><center>");
                out.println("<input type=\"submit\">");
            out.println("</center></td></tr>");
         out.println("</table>");
        out.println("</form>");
        out.println("<hr>");

        
        if (!reqSql.equals("")) {
            try {
            	LyrisServer lyris = new LyrisServer();
                String[] sqlCmds = reqSql.split(";\\s*([\\n\\r]|$)");
                for (int line = 0; line < sqlCmds.length; line++) {
                    reqSql = sqlCmds[line];
                    out.println("<!-- " + reqSql + " -->");
	                out.println("<p><center>&lt;SQL: " + reqSql + "&gt;</center></p>");
	                // make sure that the sql begins with readonly code like select or sp_help
	                if (Pattern.compile("^(select|sp_help)",Pattern.CASE_INSENSITIVE).matcher(reqSql).find()) {
	                    // make sure that the top value is set
	                    if (!Pattern.compile("^select\\s+top\\s+\\d+\\s+",Pattern.CASE_INSENSITIVE).matcher(reqSql).find() &&
	                    		!Pattern.compile("row_number()",Pattern.CASE_INSENSITIVE).matcher(reqSql).find()) {
	                    	reqSql = reqSql.replaceFirst("select", "select top " + reqRecCount);
	                    }
	                	String[][] results = lyris.getSql(reqSql);
	                    printResultSet(results,reqRecCount, out);
	                } else {
	                    out.println("ERROR: this is not a valid Read-Only SQL statement, the statement must begin with select or sp_help");
	                }
                }
            } catch (Exception e) {
                out.println(e.toString());
                System.out.println(e.toString());
                e.printStackTrace();
            }

        
             if (conn != null) {
                 System.out.println("Disconnecting...");
                try {
                    conn.close();
                } catch (SQLException eSql) {
                    out.println(eSql.toString());
                    System.out.println(eSql.toString());
                    eSql.printStackTrace();
                }
            }
        }
        StringBuffer dataTables = new StringBuffer();
        dataTables
        	.append("<script type=\"text/javascript\" src=\"" + request.getContextPath() + "/plugins/datatables/js/jquery.dataTables.min.js\"></script>")
	        .append("<script type=\"text/javascript\" src=\"" + request.getContextPath() + "/plugins/tabletools/js/ZeroClipboard.js\"></script>")
	        .append("<script type=\"text/javascript\" src=\"" + request.getContextPath() + "/plugins/tabletools/js/TableTools.js\"></script>")
	        .append("<script type=\"text/javascript\" src=\"" + request.getContextPath() + "/plugins/scroller/js/dataTables.scroller.js\"></script>")
	     	.append("<script type=\"text/javascript\" charset=\"utf-8\">\n")
	     	.append("$(document).ready(function() {\n")
	     		.append("$('.datatable').dataTable( {\n")
		     		.append("\"oTableTools\": {\n")
		     			.append("\"sSwfPath\": \"" + request.getContextPath() + "/plugins/tabletools/swf/copy_csv_xls_pdf.swf\",\n")
		     		.append("}\n")
		     	.append("} );")
	     	.append("} );\n")
	     	.append("</script>\n")
     	;
         
        out.println(dataTables.toString());
        out.println("</body>");
        out.println("</html>");
        
    }
    private ArrayList getDbs(Connection conn) {
        ArrayList dbNames = new ArrayList();
        try {
            DatabaseMetaData dbMeta = conn.getMetaData();
            ResultSet rs = dbMeta.getCatalogs();
            while (rs.next()) {
                dbNames.add(rs.getString("TABLE_CAT"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbNames;
    }
    
     private void printResultSet(String[][] rs, int maxRows, PrintWriter out) {
        ResultSetMetaData rsmd = null;
        try {
            out.println("<table class=\"datatable\" border=\"1\">");
            int numCols = rs[0].length;
            out.print("<thead><tr>");
            out.print("<th>Row#</th>");
            for (int currCol = 0; currCol < numCols; currCol++) {
                out.print("<th>" + rs[0][currCol] + "</th>");
            }
            out.println("</tr></thead><tbody>");
            String currValue = null;
            for (int currRowNum = 1; currRowNum < rs.length; currRowNum++) {
            	String[] currRow = rs[currRowNum];
                out.print("<tr>");
                out.print("<td>" + currRowNum + "</td>");
                for (int currCol = 0; currCol < numCols; currCol++) {
                    currValue = currRow[currCol];
                    if (currValue == null) {
                        currValue = "&lt;NULL&gt;";
                    } else {
                        currValue = currValue.toString().trim().replaceAll("&","&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
                    }
                    out.print("<td>" + currValue + "</td>");
                }
                out.println("</tr>");
            }
            out.println("</tbody></table>");
        } catch (Exception e) {
            out.println(e.toString());
            System.out.println("ERROR: in Lyris Results");
            e.printStackTrace();
        }
        
         
     }
     
    public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        doGet(request, response);
    }    
    
    /** Getter for property publication.
     * @return Value of property publication.
     *
     */
    public String getPublication() {
        return this.publication;
    }
    
    /** Setter for property publication.
     * @param publication New value of property publication.
     *
     */
    public void setPublication(String publication) {
        this.publication = publication;
    }
    
}
