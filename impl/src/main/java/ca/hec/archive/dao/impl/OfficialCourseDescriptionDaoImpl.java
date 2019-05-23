package ca.hec.archive.dao.impl;

import ca.hec.archive.dao.OfficialCourseDescriptionDao;
import ca.hec.archive.model.OfficialCourseDescription;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.*;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.*;

public class OfficialCourseDescriptionDaoImpl extends HibernateDaoSupport implements
        OfficialCourseDescriptionDao {


    private static Log log = LogFactory.getLog(OfficialCourseDescriptionDaoImpl.class);

    public void init() {
        log.info("init");
    }


    public OfficialCourseDescription getOfficialCourseDescription(String courseId) {

        OfficialCourseDescription catDesc = null;

        DetachedCriteria dc =
                DetachedCriteria
                        .forClass(OfficialCourseDescription.class)
                        .add(Restrictions.eq("courseId",
                                courseId.toUpperCase()));

        List descList = getHibernateTemplate().findByCriteria(dc);

        if (descList != null && descList.size() != 0) {
            catDesc = (OfficialCourseDescription) descList.get(0);
        }

        return catDesc;
    }

}
