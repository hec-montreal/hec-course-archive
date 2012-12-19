package ca.hec.archive.dao;

import java.util.List;

import ca.hec.archive.model.ArchiveCourseSection;
//import ca.hec.cdm.exception.DatabaseException;
//import ca.hec.cdm.exception.StaleDataException;

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
     * @throws StaleDataException
     * @throws DatabaseException
     */
    public void saveArchiveCourseSection(ArchiveCourseSection acs);
//	    throws StaleDataException, DatabaseException;

    /**
     * save the catalog description to the database
     * 
     * @param cd - the catalog description to save
     * @throws StaleDataException
     * @throws DatabaseException
     */
    public List<ArchiveCourseSection> getArchiveCourseSections();
}
