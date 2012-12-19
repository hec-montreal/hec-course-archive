package ca.hec.archive.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ca.hec.archive.dao.ArchiveDao;
import ca.hec.archive.model.ArchiveCourseSection;

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

    public List<ArchiveCourseSection> getArchiveCourseSections() {
	List<ArchiveCourseSection> sections = new ArrayList<ArchiveCourseSection>();
	for (Object o : getHibernateTemplate().find("from ArchiveCourseSection"))
	{
	    sections.add((ArchiveCourseSection)o);
	}
	return sections;
    }

}
