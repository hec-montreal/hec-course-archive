package ca.hec.archive.jobs;

import java.awt.Dimension;
import java.awt.Insets;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.zefer.pd4ml.PD4Constants;
import org.zefer.pd4ml.PD4ML;

/**
 * A one-time job to import the course outlines from ZC1 HTML format to ZC2 PDF
 * format
 */
public class ImportPdfZc1Job implements Job {

    protected Dimension format = PD4Constants.A4;
    protected int userSpaceWidth = 900;
    protected boolean landscapeValue = false;
    protected int topValue = 10;
    protected int leftValue = 10;
    protected int rightValue = 10;
    protected int bottomValue = 10;
    protected String unitsValue = "mm";
    protected String proxyHost = "";
    protected int proxyPort = 0;
    private static Log log = LogFactory.getLog(ImportPdfZc1Job.class);

    private static final String ZC1_REQUEST =
	    "select PLANCOURS.KOID,PLANCOURS.SESSIONCOURS, PLANCOURS.PERIODE, PLANCOURS.CODECOURS,PLANCOURS.SECTIONCOURS,PLANCOURS.LANG from PLANCOURS where SESSIONCOURS IS NOT NULL";

    // Fields and methods for spring injection
    protected AuthzGroupService authzGroupService;
    protected ContentHostingService contentHostingService;
    protected EventTrackingService eventTrackingService;
    protected SessionManager sessionManager;
    protected UsageSessionService usageSessionService;
    protected UserDirectoryService userDirectoryService;

    public void setAuthzGroupService(AuthzGroupService authzGroupService) {
	this.authzGroupService = authzGroupService;
    }

    public void setContentHostingService(
	    ContentHostingService contentHostingService) {
	this.contentHostingService = contentHostingService;
    }

    public void setEventTrackingService(
	    EventTrackingService eventTrackingService) {
	this.eventTrackingService = eventTrackingService;
    }

    public void setSessionManager(SessionManager sessionManager) {
	this.sessionManager = sessionManager;
    }

    public void setUsageSessionService(UsageSessionService usageSessionService) {
	this.usageSessionService = usageSessionService;
    }

    public void setUserDirectoryService(
	    UserDirectoryService userDirectoryService) {
	this.userDirectoryService = userDirectoryService;
    }

    // ENDOF spring injection

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	PreparedStatement ps = null;
	ByteArrayOutputStream pdfStream = null;
	ContentResourceEdit newResource = null;
	ContentCollection newCollection = null;
	Connection connex = null;

	try {

	    connex = getZC1Connection();
	    loginToSakai();
	    ps = connex.prepareStatement(ZC1_REQUEST);
	    URL customCssUrl =
		    this.getClass().getResource("/zc1_custom_style.css");

	    ResultSet rs = ps.executeQuery();
	    int nbCoursConverti = 0;

	    log.error("------------------------------   URLs PLANS de COURS  ---------------------------------------");

	    long start = System.currentTimeMillis();
	    while (rs.next()) {
		try {

		    String koid = rs.getString(1);
		    String sessioncours = rs.getString(2);
		    String periode = rs.getString(3);
		    String codecours = rs.getString(4);
		    String sectioncours = rs.getString(5);
		    String lang = rs.getString(6);
		    
		    String suffixPeriode = "";
		    
		    //Si la periode est différente de 1 (pour les MBA on a P1/P2/P3/P4/P5/P6)
		    if (!"1".equals(periode)){
			suffixPeriode = "." + periode;
		    }

		    String urlCoHTML =
			    "http://zonecours.hec.ca/af1CodexImp.jsp?instId="
				    + koid + "&lang=" + lang;
		    String courseId =
			    formatCourseId(codecours) + "." + sessioncours + suffixPeriode + "." + sectioncours;

		    String collection_id =
			    "/attachment/" + courseId + "." + "/OpenSyllabus/";

		    /********************** Check if resource already exists ********************/
		    try {
			contentHostingService
				.getResource(collection_id + courseId  + "_public.pdf");
			// if the previous function doesn't throw an exception
			// it means the course pdf is already created (so we can
			// go to the next iteration)
			log.error("Course pdf " + courseId + " is already created");
			continue;
		    } catch (IdUnusedException e) {
			// we use the catch in order to avoid the "continue"
			// statement
		    }

		    /********************** Creating PDF Stream from URL ********************/
		    pdfStream = new ByteArrayOutputStream();
		    PD4ML pd4ml = new PD4ML();
		    pd4ml.setPageSize(landscapeValue ? pd4ml
			    .changePageOrientation(format) : format);

		    if (unitsValue.equals("mm")) {
			pd4ml.setPageInsetsMM(new Insets(topValue, leftValue,
				bottomValue, rightValue));
		    } else {
			pd4ml.setPageInsets(new Insets(topValue, leftValue,
				bottomValue, rightValue));
		    }

		    pd4ml.setHtmlWidth(userSpaceWidth);
		    pd4ml.disableHyperlinks();

		    pd4ml.addStyle(customCssUrl, true);
		    pd4ml.render(urlCoHTML, pdfStream);

		    /********************** Importing PDF Stream to Sakai Resources ********************/

		    try {
			newCollection =
				contentHostingService
					.addCollection(collection_id);
			contentHostingService
				.commitCollection((ContentCollectionEdit) newCollection);
		    }

		    catch (IdUsedException e) {
			log.error("Collection " + collection_id
				+ " is already created");
			newCollection =
				(ContentCollection) contentHostingService
					.getCollection(collection_id);
		    }

		    newResource =
			    contentHostingService.addResource(
				    newCollection.getId(), courseId  + "_public", ".pdf",
				    1);
		    newResource.setContent(pdfStream.toByteArray());
		    contentHostingService.commitResource(newResource,
			    NotificationService.NOTI_NONE);
		    nbCoursConverti++;
		    log.info("url ZC1: " + urlCoHTML);
		    log.info("url SDATA: "
			    + "http://localhost:8080/sdata/c/attachment/"
			    + courseId +  "/OpenSyllabus/"
			    + courseId +  "_public.pdf");
		    log.info("************************** " + nbCoursConverti
			    + " **********************");

		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }// end while

	    long end = System.currentTimeMillis();
	    long time = (end - start) / 1000;
	    log.error("----------------------------------------------------------------------------------");
	    log.error("FIN DE LA JOB. CELA A PRIS " + time + " SECONDES POUR CONVERTIR " + nbCoursConverti + " PLANS DE COURS");
	    logoutFromSakai();

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private Connection getZC1Connection() {

	String driverName =
		ServerConfigurationService
			.getString("hec.zonecours.conn.portail.driver.name");
	String url =
		ServerConfigurationService
			.getString("hec.zonecours.conn.portail.url");
	String user =
		ServerConfigurationService
			.getString("hec.zonecours.conn.portail.user");
	String password =
		ServerConfigurationService
			.getString("hec.zonecours.conn.portail.password");

	Connection zc1con = null;

	try {
	    Class.forName(driverName);

	    zc1con = DriverManager.getConnection(url, user, password);
	} catch (ClassNotFoundException cnf) {
	    log.error("Driver not found !");
	} catch (SQLException sqlex) {
	    log.error("Database connection error:" + sqlex.toString());
	}

	return zc1con;
    }

    private String formatCourseId(String courseId) {
	String cheminement;
	String numero;
	String annee;
	String formattedCourseId;

	if (courseId.length() == 6) {
	    cheminement = courseId.substring(0, 1);
	    numero = courseId.substring(1, 4);
	    annee = courseId.substring(4);
	}

	else if (courseId.length() == 7) {
	    if (courseId.endsWith("A") || courseId.endsWith("E")
		    || courseId.endsWith("R")) {
		cheminement = courseId.substring(0, 1);
		numero = courseId.substring(1, 4);
		annee = courseId.substring(4);
	    } else {
		cheminement = courseId.substring(0, 2);
		numero = courseId.substring(2, 5);
		annee = courseId.substring(5);
	    }
	}

	else if (courseId.length() == 8) {
	    cheminement = courseId.substring(0, 2);
	    numero = courseId.substring(2, 5);
	    annee = courseId.substring(5);
	}

	else {
	    return courseId;
	}

	formattedCourseId = cheminement + "-" + numero + "-" + annee;
	return formattedCourseId;

    }

    protected void loginToSakai() {
	Session sakaiSession = sessionManager.getCurrentSession();
	sakaiSession.setUserId("admin");
	sakaiSession.setUserEid("admin");

	// establish the user's session
	usageSessionService.startSession("admin", "127.0.0.1",
		"ImportPdfZc1Job");

	// update the user's externally provided realm definitions
	authzGroupService.refreshUser("admin");

	// post the login event
	eventTrackingService.post(eventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGIN, null, true));
    }

    /**
     * Logs out of the sakai environment
     */
    protected void logoutFromSakai() {
	// post the logout event
	eventTrackingService.post(eventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGOUT, null, true));
	usageSessionService.logout();
    }

}
