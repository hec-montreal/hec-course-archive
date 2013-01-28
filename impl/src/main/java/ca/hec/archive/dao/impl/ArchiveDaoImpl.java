package ca.hec.archive.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ca.hec.archive.dao.ArchiveDao;
import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.cdm.model.CatalogDescription;

public class ArchiveDaoImpl extends HibernateDaoSupport implements ArchiveDao {


    private static Log log = LogFactory.getLog(ArchiveDaoImpl.class);

    public void init() {
	log.info("init");
    }

    public void saveArchiveCourseSection(ArchiveCourseSection acs) {
//	    throws StaleDataException, DatabaseException 
	try {
	    getHibernateTemplate().saveOrUpdate(acs);
	}
	// TODO why aren't these caught?
	catch (Exception e) {
	    e.printStackTrace();
	}
    }

    //TODO: ceci ne fonctionne pas pour l'instant 
    // LazyInitializationException: could not initialize proxy - no Session
    public List<ArchiveCourseSection> getArchiveCourseSections() {
	List<ArchiveCourseSection> sections = new ArrayList<ArchiveCourseSection>();
	for (Object o : getHibernateTemplate().find("from ArchiveCourseSection"))
	{
	    sections.add((ArchiveCourseSection)o);
	}
	return sections;
    }

    public List<String> getListIstructors() {
	List<String> listInstructors = new ArrayList<String>();
	for (Object o : getHibernateTemplate().find("select instructor from ArchiveCourseSection"))
	{
	    String[] listInstructorsCurrentRow = ((String)o).split("&");
	    for (int i=0;i < listInstructorsCurrentRow.length;i++){
		String inst = listInstructorsCurrentRow[i].trim();
		if (!listInstructors.contains(inst)){
		    listInstructors.add(inst);
		}
	    }
	}
	return listInstructors;
    }

    public List<ArchiveCourseSection> getListCourseSection(String course_id,
	    String title, String instructor) {
	
	List<ArchiveCourseSection> listSections = new ArrayList<ArchiveCourseSection>();
	
	DetachedCriteria dc =
		DetachedCriteria.forClass(ArchiveCourseSection.class);
	if (course_id != null){
	    dc.add(Restrictions.ilike(
		    "catalogDescription.courseId", course_id + "%")); 
	}
	
	if (title != null){
	    dc.add(Restrictions.ilike(
		    "catalogDescription.title", "%" + course_id + "%")); 
	}
	
	if (instructor != null){
	    dc.add(Restrictions.ilike(
		    "instructor", "%" + instructor + "%")); 
	}
	
	for (Object o : getHibernateTemplate().findByCriteria(dc)) {
	    listSections.add((ArchiveCourseSection) o);
	}	

	return listSections;
    }

}
