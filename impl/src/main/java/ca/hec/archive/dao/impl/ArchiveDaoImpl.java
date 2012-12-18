package ca.hec.archive.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.hibernate.Hibernate;
//import org.hibernate.criterion.DetachedCriteria;
//import org.hibernate.criterion.Disjunction;
//import org.hibernate.criterion.Order;
//import org.hibernate.criterion.ProjectionList;
//import org.hibernate.criterion.Projections;
//import org.hibernate.criterion.Restrictions;
//import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ca.hec.archive.dao.ArchiveDao;
import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.cdm.api.CatalogDescriptionDao;
import ca.hec.cdm.exception.DatabaseException;
import ca.hec.cdm.exception.StaleDataException;
import ca.hec.cdm.model.CatalogDescription;
import ca.hec.cdm.util.Stopwords;

public class ArchiveDaoImpl extends HibernateDaoSupport implements ArchiveDao {


    private static Log log = LogFactory.getLog(ArchiveDaoImpl.class);

    public void init() {
	log.info("init");
    }

    public void saveArchiveCourseSection(ArchiveCourseSection acs)
	    throws StaleDataException, DatabaseException {
	try {
	    getHibernateTemplate().saveOrUpdate(acs);
	}
	// TODO why aren't these caught?
	catch (HibernateOptimisticLockingFailureException e) {
	    e.printStackTrace();
	    throw new StaleDataException();
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new DatabaseException();
	}
    }

}
