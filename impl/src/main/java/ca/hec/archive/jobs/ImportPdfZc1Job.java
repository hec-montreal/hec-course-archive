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
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.event.api.NotificationService;
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
    protected ContentHostingService contentHostingService;
    private static Log log = LogFactory.getLog(ImportPdfZc1Job.class);

    private static final String ZC1_REQUEST =
	    "select PLANCOURS.KOID,PLANCOURS.SESSIONCOURS, PLANCOURS.PERIODE, PLANCOURS.CODECOURS,PLANCOURS.SECTIONCOURS,PLANCOURS.LANG from PLANCOURS where rownum < 2";

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	Connection connex = getZC1Connection();
	PreparedStatement ps = null;
	ByteArrayOutputStream pdfStream = null;
	contentHostingService =
		(ContentHostingService) ComponentManager
			.get(ContentHostingService.class);
	ContentResourceEdit newResource = null;
	ContentCollectionEdit newCollection = null;

	try {
	    ps = connex.prepareStatement(ZC1_REQUEST);
	    URL customCssUrl = this.getClass().getResource("/zc1_custom_style.css");

	    ResultSet rs = ps.executeQuery();

	    log.error("------------------------------   URLs PLANS de COURS  ---------------------------------------");

	    while (rs.next()) {

		String koid = rs.getString(1);
		String sessioncours = rs.getString(2);
		String periode = rs.getString(3);
		String codecours = rs.getString(4);
		String sectioncours = rs.getString(5);
		String lang = rs.getString(6);

		String urlCoHTML =
			"http://zonecours.hec.ca/af1CodexImp.jsp?instId="
				+ koid + "&lang=" + lang;
		String courseId =
			formatCourseId(codecours) + "." + sessioncours;

		log.error(urlCoHTML);

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
		pd4ml.enableDebugInfo();

		pd4ml.addStyle(customCssUrl, true);
		pd4ml.render(urlCoHTML, pdfStream);

		/********************** Importing PDF Stream to Sakai Resources ********************/

		newCollection =
			contentHostingService.addCollection("/attachement/"
				+ courseId + "/");
		
		contentHostingService.commitCollection(newCollection);
		
		newCollection =
			contentHostingService.addCollection("/attachement/"
				+ courseId + "/OpenSyllabus/");
		contentHostingService.commitCollection(newCollection);
		newResource =
			contentHostingService.addResource(
				newCollection.getId(), courseId
					+ ".00_public.pdf", ".pdf", 1);
		newResource.setContent(pdfStream.toByteArray());
		contentHostingService.commitResource(newResource,
			NotificationService.NOTI_NONE);
	    }// end while
	    log.error("----------------------------------------------------------------------------------");
	    log.error("FIN DE LA JOB");

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private Connection getZC1Connection() {

	String driverName =
		ServerConfigurationService
			.getString("hec.zonecours.conn.portail.driver.name.test");
	String url =
		ServerConfigurationService
			.getString("hec.zonecours.conn.portail.url.test");
	String user =
		ServerConfigurationService
			.getString("hec.zonecours.conn.portail.user.test");
	String password =
		ServerConfigurationService
			.getString("hec.zonecours.conn.portail.password.test");

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

}
