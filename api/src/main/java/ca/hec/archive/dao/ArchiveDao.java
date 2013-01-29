package ca.hec.archive.dao;

import java.util.List;

import ca.hec.archive.model.ArchiveCourseSection;
//import ca.hec.cdm.exception.DatabaseException;
//import ca.hec.cdm.exception.StaleDataException;
import ca.hec.cdm.model.CatalogDescription;

/**
 * This is the interface for the Dao for our Catalog Description tool, it
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

    public List<ArchiveCourseSection> getListCourseSection(String course_id);

    public List<CatalogDescription> getListCatalogDescription(String course_id,
	    String title, String instructor);
}
