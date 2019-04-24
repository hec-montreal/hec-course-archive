package ca.hec.archive.impl;

import ca.hec.archive.api.HecCourseArchiveService;
import ca.hec.archive.api.OfficialCourseDescriptionService;
import ca.hec.archive.dao.ArchiveDao;
import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.archive.model.OfficialCourseDescription;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.coursemanagement.api.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HecCourseArchiveServiceImpl implements HecCourseArchiveService {

    @Setter
    private ArchiveDao archiveDao;
    @Setter
    private AuthzGroupService authzGroupService;
    @Setter
    private CourseManagementService cmService;
    @Setter
    private OfficialCourseDescriptionService officialCourseDescriptionService;

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

    public List<ArchiveCourseSection> getListArchiveCourseSections(String course_id,
																   String title, String instructor, String courseCareerGroup, String courseLanguage) {
	return archiveDao.getListArchiveCourseSections(course_id, title, instructor, courseCareerGroup, courseLanguage);
    }

	@Override
	public void saveCourseMetadataToArchive(String siteId, String syllabusId, Set<String> syllabusSections) {
		ArchiveCourseSection sectionToSave;
		Set<String> sectionGroups;
		try {
			AuthzGroup realm =
					authzGroupService.getAuthzGroup(SITE_PREFIX
							+ siteId);
			String providers = realm.getProviderGroupId();

			for (String provider: providers.split("\\+")) {
				sectionGroups = authzGroupService.getAuthzGroupIds(provider);

				if (provider == null || !cmService.isSectionDefined(provider)) {
					log.info("The course outline " + siteId
							+ " is not associated to a section in the course management,"
							+ " it's details will not be saved to HEC_COURSE_ARCHIVE.");
					return;
				}
				if (provider.endsWith(SITE_SHAREABLE)) {
					log.info("The course outline " + siteId
							+ " will not be transferred to HEC_COURSE_ARCHIVE because it is a dummy section references the sharable site.");
					return;
				}
				if (!updateArchiveEntry(sectionGroups, syllabusSections)) {
					log.info("This syllabus " + syllabusId + " is not associated to the section " + provider);
					continue;
				}

				Section cmSection = cmService.getSection(provider);
				CourseOffering courseOffering = cmService.getCourseOffering(cmSection.getCourseOfferingEid());
				AcademicSession cmSession = courseOffering.getAcademicSession();

				String courseId =courseOffering.getCanonicalCourseEid();
				String section = getSection(cmSection);
				String session = getSession(cmSession);
				String period = getPeriod(cmSession);

				sectionToSave = archiveDao.getArchiveCourseSection(courseId, session, section, period);

				if (sectionToSave == null) {
					OfficialCourseDescription officialCourseDescription =
							officialCourseDescriptionService.getOfficialCourseDescription(courseId);

					// prepare the ArchiveCourseSection for writing
					sectionToSave = new ArchiveCourseSection();
					sectionToSave.setSection(section);
					sectionToSave.setSession(session);
					sectionToSave.setPeriod(period);
					sectionToSave.setCourseId(String.valueOf(courseId));
					sectionToSave.setTitle(officialCourseDescription.getTitle());
					sectionToSave.setCareer(officialCourseDescription.getCareer());
					sectionToSave.setDepartment(officialCourseDescription.getDepartment());
					sectionToSave.setLanguage(officialCourseDescription.getLanguage());
				}

				// always set the instructors string
				sectionToSave.setInstructor(
						archiveDao.getInstructors(getSectionInstructors(cmSection)));

				// save or update
				archiveDao.saveArchiveCourseSection(sectionToSave);

				log.debug("saved metadata to archive for course " + syllabusId);
			}
		} catch (Exception e) {
			log.error("saveCourseMetadataToArchive(): " + e);
			e.printStackTrace();
		}
	}

	private boolean updateArchiveEntry (Set<String> providerGroups, Set<String> syllabusSectionGroups){
    	for (String providerGroup: providerGroups){
    		for(String syllabusSectionGroup: syllabusSectionGroups){
    			if (providerGroup.contains(syllabusSectionGroup))
    				return true;
			}
		}
		return false;
	}
	private Set<String> getSectionInstructors (Section section) {
    	Set<String> instructors = new HashSet<String>();
		Set<Membership> memberships = cmService.getSectionMemberships(section.getEid());

		for (Membership member: memberships){
			if (member.getRole().equalsIgnoreCase("I") || member.getRole().equalsIgnoreCase("CI"))
				instructors.add(member.getUserId());
		}

    	return instructors;
	}

    public void deleteArchiveCourseSection(String site_id) {
	ArchiveCourseSection acs = null;
	String site_info[] = site_id.split("\\.");
	
	if (site_info.length == 3) {
	    acs = archiveDao.getArchiveCourseSection(site_info[0], site_info[1], site_info[2], "1");
	} else if (site_info.length == 4 && !site_info[3].equals(SITE_SHAREABLE)) {
	    acs = archiveDao.getArchiveCourseSection(site_info[0], site_info[1], site_info[2], site_info[3]);
	}
	
	if (acs != null) {
	    archiveDao.deleteArchiveCourseSection(acs);
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
	String courseOff = section.getCourseOfferingEid();
	
	String sectionId = sectionEid.substring(courseOff.length());
	return sectionId;
    }
}
