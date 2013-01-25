package ca.hec.archive.jobs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.component.cover.ServerConfigurationService;

import ca.hec.archive.dao.ArchiveDao;
import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.cdm.api.CatalogDescriptionDao;
import ca.hec.cdm.model.CatalogDescription;

/**
 * A one-time job to import the course outlines from ZC1 HTML format to ZC2 PDF
 * format
 */
public class ImportZc1MetadatasJob implements Job {
    private static Log log = LogFactory.getLog(ImportZc1MetadatasJob.class);

    private static final String ZC1_REQUEST =
	    "select PLANCOURS.PROFESSEUR, PLANCOURS.SESSIONCOURS, PLANCOURS.PERIODE, PLANCOURS.CODECOURS,PLANCOURS.SECTIONCOURS from PLANCOURS where SESSIONCOURS IS NOT NULL and rownum < 10";

    @Getter
    @Setter
    private ArchiveDao archiveDao;
    
    @Getter
    @Setter
    private CatalogDescriptionDao catalogDescriptionDao;
    
    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	PreparedStatement ps = null;
	Connection connex = null;

	try {

	    connex = getZC1Connection();
	    ps = connex.prepareStatement(ZC1_REQUEST);

	    ResultSet rs = ps.executeQuery();
	    int nbCoursConverti = 0;

	    log.error("------------------------------   DONNEES ZC1  ---------------------------------------");

	    long start = System.currentTimeMillis();
	    while (rs.next()) {
		try {

		    String instructors = rs.getString(1);
		    String sessioncours = rs.getString(2);
		    String periode = rs.getString(3);
		    String codecours = rs.getString(4);
		    String sectioncours = rs.getString(5);
		    
		    String courseId = formatCourseId(codecours);

		    /********************** Check if resource already exists ********************/
			CatalogDescription catalogDescription = catalogDescriptionDao.getCatalogDescription(courseId);			
			ArchiveCourseSection acs = new ArchiveCourseSection();
			acs.setSection(sectioncours);
			acs.setSession(sessioncours);
			acs.setPeriod(periode);
			acs.setInstructor(instructors);
			acs.setCatalogDescription(catalogDescription);
			
			archiveDao.saveArchiveCourseSection(acs);
			
			log.error("saved course " + courseId);
			log.error("****************************************************");
		    
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }// end while

	    long end = System.currentTimeMillis();
	    long time = (end - start) / 1000;
	    log.error("----------------------------------------------------------------------------------");
	    log.error("FIN DE LA JOB. CELA A PRIS " + time + " SECONDES POUR CONVERTIR " + nbCoursConverti + " PLANS DE COURS");

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
}
