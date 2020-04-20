package group12.rest.url;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.apache.commons.validator.routines.UrlValidator;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.net.URI;
import java.sql.*;

@Path("/")
public class URLShortenerResource  {
    private UrlValidator validator = new UrlValidator();
    private UniqueLinkGenerator g = new UniqueLinkGenerator();
    private String dbLink = "jdbc:mysql:///urls?cloudSqlInstance=group12-url-shortener:us-central1:urls&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=group12&password=group122020";
    private Connection conn;
    private ResultSet rs = null;
    private Statement st = null;

    @POST
    @Path("/short")
    @Consumes("application/json")
    @Produces("application/json")
    public Response generateShortLink(URL url) throws Exception {
        // If original URL is valid
        System.out.println("Received request");
        System.out.println(url);
        if (validator.isValid(url.original)) {
            // If no custom link is provided
            if (url.custom.equals(""))
                url.shortened = g.generate();
            else {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(dbLink);
                st = conn.createStatement();
                rs = st.executeQuery("SELECT * FROM url_list");

                while (rs.next()) {
                    if (rs.getString("short").equals(url.custom))
                        return Response.status(Response.Status.CONFLICT).entity("Provided custom link is in use.").build(); 
                }

                url.shortened = url.custom;        
            }
        } else {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Provided link is not valid.").build();
        }

        // Expiration date is passed
        if (url.expiry.compareTo(new Date()) < 0) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Provided expiration date is passed.").build();
        } else {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbLink);
            st = conn.createStatement();
            st.executeUpdate("INSERT INTO url_list (original, short, expiry) VALUES('" + url.original + "', '" + url.shortened + "', '" + new java.sql.Date(url.expiry.getTime()) + "');");

            String data = "{\n\"shortened_url\": " + url.shortened + ",\n" + "\"expiration_date\": " + url.expiry.toString() + "\n}";
            return Response.ok(data, MediaType.APPLICATION_JSON).build();
        }
    }

    @GET
    @Path("/{link}")
    @Produces("application/json")
    public Response redirectToOriginal(@PathParam String link) throws Exception {
        System.out.println("Short link is: " + link);
        String original = "";
        
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(dbLink);
        st = conn.createStatement();
        rs = st.executeQuery("SELECT * FROM url_list WHERE short = '" + link + "'");

        // Check if link exists in the database
        while(rs.next()) {
            if (rs.getString("short").equals(link));
                original = rs.getString("original");
        }

        if (original.length() > 0) {
            return Response.seeOther(new URI(original)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Provided short link is expired or invalid.").build();
        }
    }

    @GET
    @Path("/metrics/{link}")
    @Produces("application/json")
    public Response getMetrics(@PathParam String link) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(dbLink);
        st = conn.createStatement();
        rs = st.executeQuery("SELECT * FROM url_metrics");

        while(rs.next()) {
            if (rs.getString("short").equals(link)) {
                String data = "{'clicks': " + rs.getString("clicks") + "\n}";

                return Response.ok(data, MediaType.APPLICATION_JSON).build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND).entity("Provided short link is expired or invalid.").build();
    }
}