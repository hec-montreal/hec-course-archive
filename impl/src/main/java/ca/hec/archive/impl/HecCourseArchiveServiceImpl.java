package ca.hec.archive.impl;

import java.util.Date;
import java.util.List;

import lombok.Setter;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.hec.archive.api.HecCourseArchiveService;
import ca.hec.archive.dao.ArchiveDao;
import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.archive.util.ArchiveUtils;
import ca.hec.cdm.api.CatalogDescriptionService;
import ca.hec.cdm.model.CatalogDescription;

import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;

import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

public class HecCourseArchiveServiceImpl implements HecCourseArchiveService {

    @Setter
    private ArchiveDao archiveDao;
    @Setter
    private AuthzGroupService authzGroupService;
    @Setter
    private CourseManagementService cmService;
    @Setter
    private CatalogDescriptionService catalogDescriptionService;
    @Setter
    private UserDirectoryService userDirectoryService;

    private static final String SITE_PREFIX = "/site/";
    private static final String SITE_SHAREABLE = "00";
    
    public final static String SUMMER = "E";
    public final static String WINTER = "H";
    public final static String FALL = "A";

    private static Log log = LogFactory.getLog(HecCourseArchiveServiceImpl.class);

    public List<String> getListInstructors() {
	return archiveDao.getListIstructors();
    }

    public List<ArchiveCourseSection> getSectionsByCourseId(String course_id) {
	return archiveDao.getSectionsByCourseId(course_id);
    }
    
    public List<CatalogDescription> getListCatalogDescription(String course_id,
	    String title, String instructor, String courseCareerGroup, String courseLanguage) {
	return archiveDao.getListCatalogDescription(course_id, title, instructor, courseCareerGroup, courseLanguage);
    }
    
    public void saveCourseMetadataToArchive(COSerialized serializedCO) {
	ArchiveCourseSection sectionToSave;
	try {
	    AuthzGroup realm =
		    authzGroupService.getAuthzGroup(SITE_PREFIX
			    + serializedCO.getSiteId());
	    String provider = realm.getProviderGroupId();
	    
	    if (provider == null || !cmService.isSectionDefined(provider)) {
		log.info("The course outline " + serializedCO.getSiteId()
			+ " is not associated to a section in the course management,"
			+ " it's details will not be saved to HEC_COURSE_ARCHIVE.");
		return;
	    }
	    /* Do we care?
	    if (provider.matches(".*[Dd][Ff][1-9]")) {
		log.info("The course outline "
			+ published.getSiteId()
			+ " will not be transferred to ZoneCours public because it is a deferred section.");
		return false;
	    }
	    */
	    if (provider.endsWith(SITE_SHAREABLE)){
		log.info("The course outline " + serializedCO.getSiteId()
			+ " will not be transferred to HEC_COURSE_ARCHIVE because it is a dummy section references the sharable site.");
		return;
	    }
	    
	    Section cmSection = cmService.getSection(provider);
	    CourseOffering courseOffering = cmService.getCourseOffering(cmSection.getCourseOfferingEid());
	    AcademicSession cmSession = courseOffering.getAcademicSession();
	    
	    String courseId = ArchiveUtils.formatCourseId(courseOffering.getCanonicalCourseEid());
	    String section = getSection(cmSection);
	    String session = getSession(cmSession);
	    String period = getPeriod(cmSession);
	    
	    sectionToSave = archiveDao.getArchiveCourseSection(courseId, session, section, period);
	    
	    if (sectionToSave != null) {
		sectionToSave.setInstructor(getInstructors(cmSection));
	    } else {
		CatalogDescription catalogDescription = 
			catalogDescriptionService.getCatalogDescription(courseId);			
		
		// prepare the ArchiveCourseSection for writing
		sectionToSave = new ArchiveCourseSection();
		sectionToSave.setSection(section);
		sectionToSave.setSession(session);
		sectionToSave.setPeriod(period);
		sectionToSave.setInstructor(getInstructors(cmSection));
		sectionToSave.setCatalogDescription(catalogDescription);			
	    }
	   
	    // save or update
	    archiveDao.saveArchiveCourseSection(sectionToSave);
	    
	    log.error("saved metadata to archive for course " + serializedCO.getCoId());
	} catch (Exception e) {
	    log.error("saveCourseMetadataToArchive(): " + e);
	    e.printStackTrace();
	}
    }

    /**
     * Gets the name of the session
     * 
     * @param session The session
     * @return The session name
     */
    private String getSession(AcademicSession session) {
	String sessionName = null;
	String sessionId = session.getEid();
	Date startDate = session.getStartDate();
	String year = startDate.toString().substring(0, 4);

	if ((sessionId.charAt(3)) == '1')
	    sessionName = WINTER + year;
	if ((sessionId.charAt(3)) == '2')
	    sessionName = SUMMER + year;
	if ((sessionId.charAt(3)) == '3')
	    sessionName = FALL + year;

	return sessionName;
    }

    /**
     * Gets the period associated to the course
     * 
     * @param session The session
     * @return The period
     */
    private String getPeriod(AcademicSession session) {
	String sessionId = session.getEid();
	String period = sessionId.substring(4, sessionId.length());
	return period;
    }

    /**
     * Gets the section associated to the course
     * 
     * @param section The section
     * @return The section Id (ex, A01)
     */
    private String getSection(Section section) {
	String sectionEid = section.getEid();
	String sectionId = sectionEid.substring(sectionEid.length()-3);
	return sectionId;
    }

    /**
     * format the instructors as a string for the HEC_COURSE_ARCHIVE table
     * 
     * @param courseSection The section
     * @return A string of instructors, formatted.
     */
    private String getInstructors(Section courseSection) {
	String instructors = "";
	EnrollmentSet enrollmentSet = cmService.getEnrollmentSet(courseSection.getEid());
	    
	for (User instructor : userDirectoryService.getUsersByEids(enrollmentSet.getOfficialInstructors())) {
	    if (instructors.length() > 0)
		instructors.concat(" & ");
	    
	    instructors += instructor.getLastName() + ", " + instructor.getFirstName();
	}

	//capitalize first letter of each name, and after '-'
	return WordUtils.capitalizeFully(instructors, new char[] {' ', '-'});
    }
}
