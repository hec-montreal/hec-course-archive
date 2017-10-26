package ca.hec.archive.dao.impl;

import ca.hec.archive.dao.ArchiveDao;
import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.commons.utils.FormatUtils;
import ca.hec.portal.model.OfficialCourseDescription;
import ca.hec.portal.util.Stopwords;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.SessionFactoryImplementor;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
	    
	    if (o != null){
		try {
			if (((String) o).contains("&")){
			    String[] listInstructorsCurrentRow = ((String) o).split("&");
				for (int i = 0; i < listInstructorsCurrentRow.length; i++) {
				    String inst = listInstructorsCurrentRow[i].trim();
				    if (!listInstructors.contains(inst)) {
					listInstructors.add(inst);
				    }
				}
			}
			else{
			    String inst = ((String) o).trim();
			    if (!listInstructors.contains(inst)) {
				listInstructors.add(inst);
			    }
			}
			
		    } catch (Exception e) {
			log.error("Exception while retrieving instructor " + o
				+ " . Exception is: " + e);
		    }
	    }	    
	}
	return listInstructors;
    }

    public List<OfficialCourseDescription> getListOfficialCourseDescription(String course_id,
																			String titleWords, String instructor, String courseCareerGroup, String courseLanguage) {

	List<OfficialCourseDescription> listOfficialCourseDescriptions =
		new ArrayList<OfficialCourseDescription>();

	DetachedCriteria dc =
		DetachedCriteria.forClass(ArchiveCourseSection.class);

	DetachedCriteria dcOfficialCourseDescription =
		dc.createCriteria("officialCourseDescription");
	try {

	    /***************************** TITLE CRITERIA ****************************************************************************/
	    

	    List<String> listTitleWords = Arrays.asList(titleWords.split(" "));
	    for (String titleWord : listTitleWords) {
		if (!stopWordList.isStopword(titleWord)) { // we don't add
							   // stopWords to the
							   // search
		    dcOfficialCourseDescription.add(Restrictions.sqlRestriction("convert(lower({alias}.COURSE_TITLE_LONG), 'US7ASCII') like convert(lower(?), 'US7ASCII')","%" + titleWord + "%", Hibernate.STRING));
		}
	    }

	    /***************************** COURSE ID CRITERIA ****************************************************************************/
	    if (course_id != null && !course_id.isEmpty()) {
		// If the user type a course id without dash, we format it
		// before the search
		
		if (!course_id.contains("-") && course_id.length() >= 6
			&& course_id.length() <= 8) {
		    dcOfficialCourseDescription.add(Restrictions.ilike("courseId",
			    FormatUtils.formatCourseId(course_id) + "%"));
		} else {
		    dcOfficialCourseDescription.add(Restrictions.ilike("courseId",
			    course_id + "%"));
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
		dcOfficialCourseDescription.add(Restrictions.in("career", listPossibleValues));
	    }
	    
	    /***************************** LANGUAGE CRITERIA ****************************************************************************/
	    if (courseLanguage != null && !courseLanguage.isEmpty()) {
		dcOfficialCourseDescription.add(Restrictions.eq("language", courseLanguage));
	    }

	    
	dc.setProjection(Projections.distinct(Projections.property("officialCourseDescription")));

	    for (Object o : getHibernateTemplate().findByCriteria(dc)) {
		listOfficialCourseDescriptions.add(((OfficialCourseDescription) o));
	    }

	} catch (Exception e) {
	    log.error("Exception while getting course sections : " + e);
	}

	return listOfficialCourseDescriptions;
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
	    dc.createCriteria("officialCourseDescription").add(Restrictions.eq("courseId", course_id));
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
	
	dc.createCriteria("officialCourseDescription").add(Restrictions.eq("courseId", courseId));
	dc.add(Restrictions.eq("session", session));
	dc.add(Restrictions.eq("section", section));
	dc.add(Restrictions.eq("period", period));
	
	List<ArchiveCourseSection> results = (List<ArchiveCourseSection>)getHibernateTemplate().findByCriteria(dc);	    
	if (results.size() == 1) {
	    return results.get(0);
	}
	
	return null;
    }

    /**
     * format the instructors as a string for the HEC_COURSE_ARCHIVE table
     * 
     * @param instructorIds
     * @return A string of instructors, formatted.
     * @throws SQLException 
     */
	public String getInstructors(Set<String> instructorIds) 
	    throws SQLException {
    	String instructors = "";

    	Connection connection = null;
    	PreparedStatement ps = null;
    	SessionFactoryImplementor sfi = null;

    	try {

    		sfi = (SessionFactoryImplementor)getHibernateTemplate().getSessionFactory();
    		connection = sfi.getConnectionProvider().getConnection();
    		
    		for (String id : instructorIds) {
    			String request = null;
    			ResultSet rset = null;

    			request = "SELECT DISTINCT name FROM psftcont.zonecours2_ps_n_nature_emploi WHERE emplid = ?";
	    
    			ps = connection.prepareStatement(request);
    			ps.setString(1, id);
    			rset = ps.executeQuery();

    			while (rset.next()) {
    				if (!instructors.equals(""))
    					instructors += " & ";
    				instructors += rset.getString(1).replace(",", ", ");
    			}
    		}	
    	} catch (SQLException e) {
    		throw e;
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		if (sfi != null && connection != null) 
    			sfi.getConnectionProvider().closeConnection(connection);
    	}
    	return instructors;
    }
}
