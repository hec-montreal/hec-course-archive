package ca.hec.archive.dao;

import java.util.List;

import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.cdm.model.CatalogDescription;

/**
 * This is the interface for the Dao for our Archive tool, it
 * handles the database access functionality of the tool
 * 
 * @author <a href="mailto:curtis.van-osch@hec.ca">Curtis van Osch</a>
 * @version $Id: $
 */
public interface ArchiveDao {

    public void init();

    /**
     * save the course section to the database
     * 
     * @param acs - the course section to save
     */
    public void saveArchiveCourseSection(ArchiveCourseSection acs);
    
    /** Return the list of instructors **/
    public List<String> getListIstructors();

    /**
     * Get a list of ArchiveCourseSection for the given course id
     * 
     * @param course_id - the id of the course whose sections should be returned
     * @return a list of ArchiveCourseSection
     */
    public List<ArchiveCourseSection> getSectionsByCourseId(String course_id);

    /**
     * Get a list of CatalogDescription for the given course id, part of course title
     * or instructor.  Used for the archive search
     * 
     * @param course_id - part or all of the id of the courses to be returned
     * @param title - part or all of the title of the courses to be returned
     * @param instructor - the name of an instructor who taught at least one 
     * 			   section of the courses to be returned
     * 
     * @return a list of ArchiveCourseSection
     */
    public List<CatalogDescription> getListCatalogDescription(String course_id,
	    String title, String instructor, String courseCareerGroup, String courseLanguage);
}
