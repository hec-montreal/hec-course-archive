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
import ca.hec.commons.utils.FormatUtils;

/**
 * A one-time job to import the course outlines from ZC1 HTML format to ZC2 PDF
 * format
 */
public class ImportZc1MetadatasJob implements Job {
    private static Log log = LogFactory.getLog(ImportZc1MetadatasJob.class);

    private static final String ZC1_REQUEST =
	    "select PLANCOURS.PROFESSEUR, PLANCOURS.SESSIONCOURS, PLANCOURS.PERIODE, PLANCOURS.CODECOURS,PLANCOURS.SECTIONCOURS from PLANCOURS where SESSIONCOURS IS NOT NULL";

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
	    int nbMetadatasCoursImportees = 0;

	    log.error("------------------------------   IMPORT DES METADATAS ZC1  ---------------------------------------");

	    long start = System.currentTimeMillis();
	    while (rs.next()) {
		try {
		    String sessioncours = rs.getString(2);
		    String periode = rs.getString(3);
		    String codecours = rs.getString(4);
		    String sectioncours = rs.getString(5);

		    String courseId = FormatUtils.formatCourseId(codecours);

		    String instructors = rs.getString(1);
		    if (instructors != null) {
			instructors = instructors.replace(",", ", ");
		    } else {
			log.error("No instructor for course: " + courseId);
		    }

		    /********************** Check if resource already exists ********************/
		    CatalogDescription catalogDescription =
			    catalogDescriptionDao
				    .getCatalogDescription(courseId);
		    if (catalogDescription == null) {
			log.error("No description found for course: "
				+ courseId);
		    } else {
			ArchiveCourseSection acs = new ArchiveCourseSection();
			acs.setSection(sectioncours);
			acs.setSession(sessioncours);
			acs.setPeriod(periode);
			acs.setInstructor(instructors);
			acs.setCatalogDescription(catalogDescription);

			archiveDao.saveArchiveCourseSection(acs);

			log.error("saved course " + courseId);
			nbMetadatasCoursImportees++;
			log.error("********************* "
				+ nbMetadatasCoursImportees
				+ " *******************************");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }// end while

	    long end = System.currentTimeMillis();
	    long time = (end - start) / 1000;
	    log.error("----------------------------------------------------------------------------------");
	    log.error("FIN DE LA JOB. CELA A PRIS " + time
		    + " SECONDES POUR IMPORTER LES METADATAS DE "
		    + nbMetadatasCoursImportees + " PLANS DE COURS");

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

}
