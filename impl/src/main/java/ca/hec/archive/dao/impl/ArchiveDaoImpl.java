package ca.hec.archive.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ca.hec.archive.dao.ArchiveDao;
import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.archive.util.ArchiveUtils;
import ca.hec.cdm.model.CatalogDescription;
import ca.hec.cdm.util.Stopwords;

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
	    String titleWords, String instructor) {

	List<CatalogDescription> listCatalogDescriptions =
		new ArrayList<CatalogDescription>();

	DetachedCriteria dc =
		DetachedCriteria.forClass(ArchiveCourseSection.class);

	DetachedCriteria dcCatalogDescription =
		dc.createCriteria("catalogDescription");
	try {

	    /***************************** TITLE CRITERIA ****************************************************************************/
	    // We create a disjunction because we want to return course sections
	    // that have one or severall of the keywords in the title
	    Disjunction searchCourseIdDisjunction = Restrictions.disjunction();

	    List<String> listTitleWords = Arrays.asList(titleWords.split(" "));
	    for (String titleWord : listTitleWords) {
		if (!stopWordList.isStopword(titleWord)) { // we don't add
							   // stopWords to the
							   // search
		    searchCourseIdDisjunction.add(Restrictions.ilike("title",
			    "%" + titleWord + "%"));
		}
		dcCatalogDescription.add(searchCourseIdDisjunction);
	    }

	    /***************************** COURSE ID CRITERIA ****************************************************************************/
	    if (course_id != null && !course_id.isEmpty()) {
		// If the user type a course id without dash, we format it
		// before the search
		if (!course_id.contains("-") && course_id.length() >= 6
			&& course_id.length() <= 8) {
		    dcCatalogDescription.add(Restrictions.ilike("courseId",
			    ArchiveUtils.formatCourseId(course_id) + "%"));
		} else {
		    dcCatalogDescription.add(Restrictions.ilike("courseId",
			    course_id + "%"));
		}

	    }

	    /***************************** INSTRUCTOR CRITERIA ****************************************************************************/
	    if (instructor != null) {
		dc.add(Restrictions.ilike("instructor", "%" + instructor + "%"));
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

}
