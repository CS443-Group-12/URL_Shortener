package group12.shortener;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.apache.commons.validator.routines.UrlValidator;
import io.agroal.api.AgroalDataSource;
import javax.enterprise.context.ApplicationScoped;
// import io.quarkus.agroal.DataSource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.net.URI;
import java.sql.*;
// import javax.sql.DataSource;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;


@Path("/")
public class ShortenerResource  {
    @Inject
    AgroalDataSource defaultDataSource;

    private UrlValidator validator = new UrlValidator();
    private UniqueLinkGenerator g = new UniqueLinkGenerator();
    private Connection conn;
    private ResultSet rs = null;
    private Statement st = null;
    private boolean connectionFlag = false;

    @GET
    @Path("/favicon.ico")
    public Response favicon() throws Exception {
        return Response.seeOther(new URI("/src/main/resources/META-INF/resources/favicon.ico")).build();
    }

    @POST
    @Path("/b2c/short")
    @Consumes("application/json")
    @Produces("application/json")
    @Counted(name = "b2cShortening", description = "How many shortening requests came from B2C sources.")
    @Timed(name = "b2cShorteningTimer", description = "A measure of how long it takes to serve B2B shortening request.", unit = MetricUnits.MILLISECONDS)
    public Response generateShortLinkB2c(URL url) throws Exception {
        // If original URL is valid
        System.out.println("Received SHORTENING request :: B2C");
        System.out.println(url);
        if (validator.isValid(url.original)) {
            // If no custom link is provided
            if (url.custom.equals(""))
                url.shortened = g.generate();
            else {
                if (!connectionFlag) {
                    conn = defaultDataSource.getConnection();
                    connectionFlag = true;
                }
                // conn = DriverManager.getConnection(dbLink);
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
            if (!connectionFlag) {
                conn = defaultDataSource.getConnection();
                connectionFlag = true;
            }
            // conn = DriverManager.getConnection(dbLink);
            st = conn.createStatement();
            st.executeUpdate("INSERT INTO url_list (original, short, expiry, source) VALUES('" + url.original + "', '" + url.shortened + "', '" + new java.sql.Date(url.expiry.getTime()) + "', 'b2c');");

            String data = "{\n\"shortened_url\": \"" + url.shortened + "\",\n" + "\"expiration_date\": \"" + url.expiry.toString() + "\"\n}";
            return Response.ok(data, MediaType.APPLICATION_JSON).build();
        }
    }

    @POST
    @Path("/b2b/short")
    @Consumes("application/json")
    @Produces("application/json")
    @Counted(name = "b2bShortening", description = "How many shortening requests came from B2B sources.")
    @Timed(name = "b2bShorteningTimer", description = "A measure of how long it takes to serve B2B shortening request.", unit = MetricUnits.MILLISECONDS)
    public Response generateShortLinkB2b(URL url) throws Exception {
        // If original URL is valid
        System.out.println("Received SHORTENING request :: B2B");
        System.out.println(url);
        if (validator.isValid(url.original)) {
            // If no custom link is provided
            if (url.custom.equals(""))
                url.shortened = g.generate();
            else {
                if (!connectionFlag) {
                    conn = defaultDataSource.getConnection();
                    connectionFlag = true;
                }
                // conn = DriverManager.getConnection(dbLink);
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
            if (!connectionFlag) {
                conn = defaultDataSource.getConnection();
                connectionFlag = true;
            }
            // conn = DriverManager.getConnection(dbLink);
            st = conn.createStatement();
            st.executeUpdate("INSERT INTO url_list (original, short, expiry, source) VALUES('" + url.original + "', '" + url.shortened + "', '" + new java.sql.Date(url.expiry.getTime()) + "', 'b2b');");

            String data = "{\n\"shortened_url\": \"" + url.shortened + "\",\n" + "\"expiration_date\": \"" + url.expiry.toString() + "\"\n}";
            return Response.ok(data, MediaType.APPLICATION_JSON).build();
        }
    }

    @GET
    @Path("/{link}")
    @Produces("application/json")
    @Counted(name = "redirection", description = "How many redirection requests have been sent.")
    @Timed(name = "redirectionTimer", description = "A measure of how long it takes to serve redirection request.", unit = MetricUnits.MILLISECONDS)
    public Response redirectToOriginal(@PathParam String link) throws Exception {
        System.out.println("REDIRECT request\nShort link is: " + link);
        String original = "";
    
        if (!connectionFlag) {
            conn = defaultDataSource.getConnection();
            connectionFlag = true;
        }
        // conn = DriverManager.getConnection(dbLink);
        st = conn.createStatement();
        rs = st.executeQuery("SELECT * FROM url_list WHERE short = '" + link + "'");

        // Check if link exists in the database
        while(rs.next()) {
            if (rs.getString("short").equals(link));
                original = rs.getString("original");
        }

        if (original.length() > 0) {
            st = conn.createStatement();
            st.executeUpdate("INSERT INTO url_analytics (short, date) VALUES('" + link + "', '" + new java.sql.Date(new Date().getTime()) + "');");
            
            // rs.close();
            // st.close();
            
            return Response.seeOther(new URI(original)).build();
        } else {
            // rs.close();
            // st.close();
            return Response.status(Response.Status.NOT_FOUND).entity("Provided short link is expired or invalid.").build();
        }
    }

    @GET
    @Path("/b2c/analytics/{link}")
    @Produces("application/json")
    @Counted(name = "b2cAnalytics", description = "How many analytics requests came from B2C sources.")
    @Timed(name = "b2cAnalyticsTimer", description = "A measure of how long it takes to serve B2C analytics request.", unit = MetricUnits.MILLISECONDS)
    public Response getAnalyticsB2c(@PathParam String link) throws Exception {
        System.out.println("Received GET_ANALYTICS request :: B2C");
        boolean exists = false;
        if (!connectionFlag) {
            conn = defaultDataSource.getConnection();
            connectionFlag = true;
        }
        // conn = DriverManager.getConnection(dbLink);
        st = conn.createStatement();
        rs = st.executeQuery("SELECT * FROM url_list WHERE short = '" + link + "'");

        while(rs.next()) {
            if (rs.getString("short").equals(link));
                exists = true;
        }

        if (exists) {
            rs = st.executeQuery("SELECT COUNT( *) FROM url_analytics WHERE short = '" + link + "'");

            while(rs.next()) {
                System.out.println(rs.getString(1));
                if (rs.getString(1) != null) {
                    String data = "{\n\"all_clicks\": " + rs.getString(1) + "\n}";
                    
                    // rs.close();
                    // st.close();
                    
                    return Response.ok(data, MediaType.APPLICATION_JSON).build();
                }
            }
        }

        // rs.close();
        // st.close();
       
        return Response.status(Response.Status.NOT_FOUND).entity("Provided short link is expired or invalid.").build();
    }

    @GET
    @Path("/b2b/analytics/{link}")
    @Produces("application/json")
    @Counted(name = "b2bAnalytics", description = "How many analytics requests came from B2B sources.")
    @Timed(name = "b2bAnalyticsTimer", description = "A measure of how long it takes to serve B2B analytics request.", unit = MetricUnits.MILLISECONDS)
    public Response getAnalyticsB2b(@PathParam String link) throws Exception {
        System.out.println("Received GET_ANALYTICS request :: B2B");
        if (!connectionFlag) {
            conn = defaultDataSource.getConnection();
            connectionFlag = true;
        }
        // conn = DriverManager.getConnection(dbLink);
        st = conn.createStatement();
        rs = st.executeQuery("SELECT COUNT( *) FROM url_analytics WHERE short = '" + link + "'");

        while(rs.next()) {
            System.out.println(rs.getString(1));
            if (rs.getString(1) != null) {
                String data = "{\n\"all_clicks\": " + rs.getString(1) + "\n}";
                
                // rs.close();
                // st.close();
                
                return Response.ok(data, MediaType.APPLICATION_JSON).build();
            }
        }

        // rs.close();
        // st.close();

        return Response.status(Response.Status.NOT_FOUND).entity("Provided short link is expired or invalid.").build();
    }
}