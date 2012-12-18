package ca.hec.archive.dao;

import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.cdm.exception.DatabaseException;
import ca.hec.cdm.exception.StaleDataException;

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
     * save the catalog description to the database
     * 
     * @param cd - the catalog description to save
     * @throws StaleDataException
     * @throws DatabaseException
     */
    public void saveArchiveCourseSection(ArchiveCourseSection acs)
	    throws StaleDataException, DatabaseException;
}
