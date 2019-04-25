package ca.hec.archive.dao.impl;


import ca.hec.archive.dao.OfficialCourseDescriptionDao;
import ca.hec.archive.model.OfficialCourseDescription;
import ca.hec.archive.util.Stopwords;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.criterion.*;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import java.util.*;

public class OfficialCourseDescriptionDaoImpl extends HibernateDaoSupport implements
        OfficialCourseDescriptionDao {

    private static final String CERTIFICATE = "CERT";

    private static Log log = LogFactory.getLog(OfficialCourseDescriptionDaoImpl.class);

    @Setter
    @Getter
    private Stopwords stopWordList = null;

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

    public List<OfficialCourseDescription> getOfficialCourseDescriptionsByDepartment(
            String department) {
        HashMap<String, String> criteria = new HashMap<String, String>();
        criteria.put("department", department);

        return getOfficialCourseDescriptions(criteria);
    }


    public List<OfficialCourseDescription> getOfficialCourseDescriptionsByCareer(String career) {
        HashMap<String, String> criteria = new HashMap<String, String>();
        criteria.put("career", career);

        return getOfficialCourseDescriptions(criteria);
    }

    public List<OfficialCourseDescription> getAllOfficialCourseDescriptionsForCertificate() {
        return getOfficialCourseDescriptionsByCareer(CERTIFICATE);
    }


    public List<OfficialCourseDescription> getAllOfficialCourseDescriptions() {
        List<OfficialCourseDescription> officialCourseDescriptions =
                new ArrayList<OfficialCourseDescription>();

        DetachedCriteria dc =
                DetachedCriteria.forClass(OfficialCourseDescription.class);

        for (Object o : getHibernateTemplate().findByCriteria(dc)) {
            officialCourseDescriptions.add((OfficialCourseDescription) o);
        }

        return officialCourseDescriptions;
    }

    public List<OfficialCourseDescription> getOfficialCourseDescriptions(
            Map<String, String> eqCriteria) {
        return getOfficialCourseDescriptions(eqCriteria, null);
    }

    /**
     * get catalog descriptions according to criterias passed in parameter
     *
     * @param eqCriteria:     criterias such as (department = XX) or (career = YY)
     *                        used to filter results
     * @param searchCriteria: criterias from the search toolbar: (description
     *                        like %KEYWORD%) or (title like %KEYWORD%)
     * @return
     */
    public List<OfficialCourseDescription> getOfficialCourseDescriptions(
            Map<String, String> eqCriteria, Map<String, String> searchCriteria) {

        // add each of the search criteria in the map to the Hibernate
        // DetachedCriteria object
        if (eqCriteria != null && !eqCriteria.isEmpty()) {
            return getOfficialCourseDescriptionsByItem(eqCriteria);
        }

        if (searchCriteria != null) {
            return getOfficialCourseDescriptionsSearch(searchCriteria);
        }

        return new ArrayList<OfficialCourseDescription>();
    }

    public List<OfficialCourseDescription> getOfficialCourseDescriptionsSearch(
            Map<String, String> criteria) {
        List<OfficialCourseDescription> officialCourseDescriptions =
                new ArrayList<OfficialCourseDescription>();

        DetachedCriteria dc =
                DetachedCriteria.forClass(OfficialCourseDescription.class);

        // We create a disjunction because the search return catalog
        // descriptions that have at least one keyword in their
        // description/title (OR operator between criterias)
        Disjunction searchCriteriasDisjunction = Restrictions.disjunction();

        // We create a projection that will store an accuracy integer for the
        // result set (the higher the accuracy the higher the number)
        StringBuilder accuracyProjection = new StringBuilder();

        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            List<String> listPossibleValues =
                    Arrays.asList(entry.getValue().split(","));
            for (String searchValue : listPossibleValues) {
                //remove these bad characters.
                searchValue = searchValue.replaceAll("[*()]", "");

                if (!stopWordList.isStopword(searchValue)) { // we don't add
                    // stopWords to the
                    // search
                    // we don't have the same matching rule if the search word is the beginning of an course id
                    if (isCourseIdSearch(searchValue)) {
                        searchCriteriasDisjunction.add(Restrictions.ilike(
                                "courseId", searchValue.replace("-","") + "%"));
                    } else {
                        searchCriteriasDisjunction.add(Restrictions.ilike(
                                entry.getKey(), "%" + searchValue + "%"));
                         /* DEPRECATED because PSFTCONT.ZONECOURS2_PS_N_COUR_OFFER_VW.COURSE_TITLE_LONG is not indexed
                         if ("title".equals(entry.getKey())) { // we calculate the
                            // accuracy with the
                            // number of search
                            // words that are in
                            // the title

                            accuracyProjection.append("+ (CONTAINS({alias}.COURSE_TITLE_LONG,'%"
                                    + searchValue + "%')) ");
                        }*/
                    }
                }
            }
        }


        dc.add(searchCriteriasDisjunction);

        ProjectionList projectList = Projections.projectionList();

        projectList.add(Projections.property("courseId"));
        projectList.add(Projections.property("title"));
        projectList.add(Projections.property("department"));
        projectList.add(Projections.property("career"));
        projectList.add(Projections.property("language"));
        projectList.add(Projections.property("description"));

        if (accuracyProjection.length() != 0) {
            accuracyProjection.append("  as accuracy ");
            accuracyProjection.deleteCharAt(0);
            projectList.add(Projections.alias(Projections.sqlProjection(
                    accuracyProjection.toString(), new String[]{"accuracy"},
                    new Type[]{ StandardBasicTypes.INTEGER }), "accuracy"));
            // We sort the result set by accuracy
            dc.addOrder(Order.desc("accuracy"));
        } else {
            dc.addOrder(Order.asc("courseId"));
        }


        dc.setProjection(projectList);


        for (Object cdproperties : getHibernateTemplate().findByCriteria(dc)) {
            OfficialCourseDescription cd = new OfficialCourseDescription();
            cd.setCourseId("" + ((Object[]) cdproperties)[0]);
            cd.setTitle("" + ((Object[]) cdproperties)[1]);
            cd.setDepartment("" + ((Object[]) cdproperties)[2]);
            cd.setCareer("" + ((Object[]) cdproperties)[3]);
            cd.setLanguage("" + ((Object[]) cdproperties)[4]);
            officialCourseDescriptions.add(cd);
        }

        return officialCourseDescriptions;
    }

    //we consider that if the first charcter of the searc word is a number, then it is a course id search
    private boolean isCourseIdSearch(String searchWord) {
        return searchWord.matches("^[\\d].*");
    }

    public List<OfficialCourseDescription> getOfficialCourseDescriptionsByItem(
            Map<String, String> criteria) {
        List<OfficialCourseDescription> officialCourseDescriptions =
                new ArrayList<OfficialCourseDescription>();

        DetachedCriteria dc =
                DetachedCriteria.forClass(OfficialCourseDescription.class);

        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            // comma separated values
            String values = entry.getValue();

            dc.add(Restrictions.in(entry.getKey(), Arrays.asList(values.split(","))));

        }

        dc.addOrder(Order.asc("courseId"));
        for (Object o : getHibernateTemplate().findByCriteria(dc)) {
            officialCourseDescriptions.add((OfficialCourseDescription) o);
        }

        return officialCourseDescriptions;
    }

    public List<String> getListCourseId() {
        return (List<String>) getHibernateTemplate().find(
                "select distinct cd.courseId from OfficialCourseDescription cd");
    }

    public OfficialCourseDescription getLastVersionOfficialCourseDescription(String courseId) {
        DetachedCriteria dc =
                DetachedCriteria.forClass(OfficialCourseDescription.class)
                        .add(Restrictions.eq("courseId", courseId))
                        .addOrder(Order.desc("id"));

        return (OfficialCourseDescription) getHibernateTemplate().findByCriteria(dc,
                0, 1).get(0);
    }

    public List<OfficialCourseDescription> getAllOfficialCourseDescriptionsForCertificatesWithNoDescription() {

        DetachedCriteria dc =
                DetachedCriteria.forClass(OfficialCourseDescription.class)
                        .add(Restrictions.eq("career", CERTIFICATE))
                        .add(Restrictions.isNull("description"));

        List<OfficialCourseDescription> officialCourseDescriptions =
                new ArrayList<OfficialCourseDescription>();

        for (Object o : getHibernateTemplate().findByCriteria(dc)) {
            officialCourseDescriptions.add((OfficialCourseDescription) o);
        }

        return officialCourseDescriptions;
    }

    public List<OfficialCourseDescription> getOfficialCourseDescriptionsByDepartmentWithNoDescription(
            String department) {

        DetachedCriteria dc =
                DetachedCriteria
                        .forClass(OfficialCourseDescription.class)
                        .add(Restrictions.eq("department",
                                department.toUpperCase()))
                        .add(Restrictions.ne("career", CERTIFICATE))
                        .add(Restrictions.isNull("description"));

        List<OfficialCourseDescription> officialCourseDescriptions =
                new ArrayList<OfficialCourseDescription>();

        for (Object o : getHibernateTemplate().findByCriteria(dc)) {
            officialCourseDescriptions.add((OfficialCourseDescription) o);
        }

        return officialCourseDescriptions;
    }

    public List<String> getDepartmentNameWithAtLeastOneCaWithNoDescription() {
        return (List<String>) getHibernateTemplate()
                .find("select distinct cd.department from OfficialCourseDescription cd where cd.description is null");
    }

    public boolean descriptionExists(String course_id) {
        DetachedCriteria dc =
                DetachedCriteria.forClass(OfficialCourseDescription.class)
                        .add(Restrictions.eq("courseId", course_id));

        if (getHibernateTemplate().findByCriteria(dc).isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
