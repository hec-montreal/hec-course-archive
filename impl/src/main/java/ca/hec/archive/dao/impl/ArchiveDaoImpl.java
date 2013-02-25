package ca.hec.archive.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ca.hec.archive.dao.ArchiveDao;
import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.cdm.model.CatalogDescription;
import ca.hec.cdm.util.Stopwords;
import ca.hec.commons.utils.FormatUtils;

public class ArchiveDaoImpl extends HibernateDaoSupport implements ArchiveDao {

    private static Log log = LogFactory.getLog(ArchiveDaoImpl.class);

    @Setter
    @Getter
    private Stopwords stopWordList = null;

    public void init() {
	log.info("init");
    }

    public List<String> getListIstructors() {
	List<String> listInstructors = new ArrayList<String>();
	for (Object o : getHibernateTemplate().find(
		"select instructor from ArchiveCourseSection")) {
	    try {
		String[] listInstructorsCurrentRow = ((String) o).split("&");
		for (int i = 0; i < listInstructorsCurrentRow.length; i++) {
		    String inst = listInstructorsCurrentRow[i].trim();
		    if (!listInstructors.contains(inst)) {
			listInstructors.add(inst);
		    }
		}
	    } catch (Exception e) {
		log.error("Exception while retrieving instructor " + o
			+ " . Exception is: " + e);
	    }
	}
	return listInstructors;
    }

    public List<CatalogDescription> getListCatalogDescription(String course_id,
	    String titleWords, String instructor, String courseCareerGroup, String courseLanguage) {

	List<CatalogDescription> listCatalogDescriptions =
		new ArrayList<CatalogDescription>();

	DetachedCriteria dc =
		DetachedCriteria.forClass(ArchiveCourseSection.class);

	DetachedCriteria dcCatalogDescription =
		dc.createCriteria("catalogDescription");
	try {

	    /***************************** TITLE CRITERIA ****************************************************************************/
	    

	    List<String> listTitleWords = Arrays.asList(titleWords.split(" "));
	    for (String titleWord : listTitleWords) {
		if (!stopWordList.isStopword(titleWord)) { // we don't add
							   // stopWords to the
							   // search
		    String escapedTitleWord = StringEscapeUtils.escapeJava(titleWord);
		    dcCatalogDescription.add(Restrictions.sqlRestriction("convert(lower(title), 'US7ASCII') like convert(lower('%" + escapedTitleWord + "%'), 'US7ASCII')"));
		}
	    }

	    /***************************** COURSE ID CRITERIA ****************************************************************************/
	    if (course_id != null && !course_id.isEmpty()) {
		// If the user type a course id without dash, we format it
		// before the search
		 String escapedCourseId = StringEscapeUtils.escapeJava(course_id);
		
		if (!course_id.contains("-") && course_id.length() >= 6
			&& course_id.length() <= 8) {
		    dcCatalogDescription.add(Restrictions.ilike("courseId",
			    FormatUtils.formatCourseId(escapedCourseId) + "%"));
		} else {
		    dcCatalogDescription.add(Restrictions.ilike("courseId",
			    escapedCourseId + "%"));
		}

	    }

	    /***************************** INSTRUCTOR CRITERIA ****************************************************************************/
	    if (instructor != null && !instructor.isEmpty()) {
		dc.add(Restrictions.ilike("instructor", "%" + instructor + "%"));
	    }
	    
	    /***************************** CAREER CRITERIA ****************************************************************************/
	    if (courseCareerGroup != null && !courseCareerGroup.isEmpty()) {
		
		List<String> listPossibleValues =
			    Arrays.asList(courseCareerGroup.split(" "));
		dcCatalogDescription.add(Restrictions.in("career", listPossibleValues));
	    }
	    
	    /***************************** LANGUAGE CRITERIA ****************************************************************************/
	    if (courseLanguage != null && !courseLanguage.isEmpty()) {
		dcCatalogDescription.add(Restrictions.eq("language", courseLanguage));
	    }

	    
	dc.setProjection(Projections.distinct(Projections.property("catalogDescription")));

	    for (Object o : getHibernateTemplate().findByCriteria(dc)) {
		listCatalogDescriptions.add(((CatalogDescription) o));
	    }

	} catch (Exception e) {
	    log.error("Exception while getting course sections : " + e);
	}

	return listCatalogDescriptions;
    }

    public void saveArchiveCourseSection(ArchiveCourseSection acs) {
	try {
	    getHibernateTemplate().saveOrUpdate(acs);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void deleteArchiveCourseSection(ArchiveCourseSection acs) {
	try {
	    getHibernateTemplate().delete(acs);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public List<ArchiveCourseSection> getSectionsByCourseId(String course_id) {
	List<ArchiveCourseSection> sections = new ArrayList<ArchiveCourseSection>();
	
	DetachedCriteria dc =
		DetachedCriteria.forClass(ArchiveCourseSection.class);
	
	if (course_id != null && !course_id.isEmpty()) {
	    dc.createCriteria("catalogDescription").add(Restrictions.eq("courseId", course_id));
	}
	
	dc.addOrder(Order.desc("year"));
	dc.addOrder(Order.asc("session_letter"));
	dc.addOrder(Order.asc("section"));

	for (Object o : getHibernateTemplate().findByCriteria(dc)) {
	    sections.add(((ArchiveCourseSection) o));
	}
	
	return sections;
    }

    public ArchiveCourseSection getArchiveCourseSection(String courseId, String session, String section, String period) {
	DetachedCriteria dc =
		DetachedCriteria.forClass(ArchiveCourseSection.class);
	
	if (courseId == null || courseId.isEmpty() ||
		session == null || session.isEmpty() ||
		section == null || section.isEmpty()) {
	    return null;
	}
	
	if (period == null || period.isEmpty()) {
	    period = "1";
	}
	
	dc.createCriteria("catalogDescription").add(Restrictions.eq("courseId", courseId));
	dc.add(Restrictions.eq("session", session));
	dc.add(Restrictions.eq("section", section));
	dc.add(Restrictions.eq("period", period));
	
	List<ArchiveCourseSection> results = getHibernateTemplate().findByCriteria(dc);	    
	if (results.size() == 1) {
	    return results.get(0);
	}
	
	return null;
    }

    /**
     * format the instructors as a string for the HEC_COURSE_ARCHIVE table
     * 
     * @param courseSection The section
     * @return A string of instructors, formatted.
     * @throws SQLException 
     */
    public String getInstructors(String courseId, String sessionEid, String section, String period) 
	    throws SQLException {
	String instructors = "";

	Connection peopleSoftConnection = null;
	PreparedStatement ps = null;

	try {
	    String driverName = ServerConfigurationService
		    .getString("hec.peoplesoft.conn.portail.driver.name");
	    String url = ServerConfigurationService
		    .getString("hec.peoplesoft.conn.portail.url");
	    String user = ServerConfigurationService
		    .getString("hec.peoplesoft.conn.portail.user");
	    String password = ServerConfigurationService
		    .getString("hec.peoplesoft.conn.portail.password");

	    Class.forName(driverName);
	    peopleSoftConnection = DriverManager.getConnection(url, user, password);

	    String request = null;
	    ResultSet rset = null;

	    request = "SELECT DISTINCT name FROM ps_n_nature_emploi T1, ps_n_crsection_vw T2 WHERE T1.emplid= T2.emplid AND catalog_nbr=? AND strm=? AND session_code=? AND class_section=?";

	    ps = peopleSoftConnection.prepareStatement(request);
	    ps.setString(1, courseId);
	    ps.setString(2, sessionEid);
	    ps.setString(3, period);
	    ps.setString(4, section);
	    rset = ps.executeQuery();

	    while (rset.next()) {
		if (!instructors.equals(""))
		    instructors += " & ";
		instructors += rset.getString(1).replace(",", ", ");
	    }
	} catch (SQLException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if (peopleSoftConnection != null)
		peopleSoftConnection.close();
	}

	return instructors;
    }
}
