package ca.hec.archive.dao;


import ca.hec.archive.model.OfficialCourseDescription;

import java.util.List;
import java.util.Map;

/**
 * This is the interface for the Dao for our Catalog Description tool, it
 * handles the database access functionality of the tool
 * 
 * @author <a href="mailto:curtis.van-osch@hec.ca">Curtis van Osch</a>
 * @version $Id: $
 */
public interface OfficialCourseDescriptionDao {

    public void init();

    /**
     * Return the active catalog description based on the course id
     *
     * @param courseId - the course id of the catalog description
     * @return - the last version of the OfficialCourseDescription
     */
    public OfficialCourseDescription getOfficialCourseDescription(String courseId);

    /**
     * Return the last version of a catalog description based on the course id
     * 
     * @param courseId - the course id of the catalog description
     * @return - the last version of the OfficialCourseDescription (null if nothing is found)
     */
    public OfficialCourseDescription getLastVersionOfficialCourseDescription(String courseId);

    
    /**
     * Return catalog descriptions that correspond to the given department
     * (can be null for all)
     * 
     * @return - the list of OfficialCourseDescription
     */
    public List<OfficialCourseDescription> getOfficialCourseDescriptionsByDepartment(String department);
    

    /**
     * Return active catalog descriptions that correspond to the given academic career
     * (can be null for all)
     * 
     * @return - the list of OfficialCourseDescription
     */
    public List<OfficialCourseDescription> getOfficialCourseDescriptionsByCareer(String career);
    

    /**
     * Return all active catalog descriptions for certificates programs
     * (can be null for all)
     * 
     * @return - the list of OfficialCourseDescription
     */
    
    public List<OfficialCourseDescription> getAllOfficialCourseDescriptionsForCertificate();
    

    /**
     * Return catalog descriptions that correspond to the given criteria map
     * (can be null for all)
     * 
     * @return - the list of OfficialCourseDescription
     */
    List<OfficialCourseDescription> getOfficialCourseDescriptions(Map<String, String> searchWords, Map<String, String> searchScope);



    
    /** 
     * @return all catalog descriptions
     */
    public List<OfficialCourseDescription> getAllOfficialCourseDescriptions();
    
    

    /**
     * Get catalog description from department that have no description and which are not certificate courses
     * 
     */
    public List<OfficialCourseDescription> getOfficialCourseDescriptionsByDepartmentWithNoDescription(String department);
    
    
    /**
     * Get catalog description for certificates courses that have no description
     * 
     */
    public List<OfficialCourseDescription> getAllOfficialCourseDescriptionsForCertificatesWithNoDescription();
    
    /**
     * Get all departments that have at least one catalog description with an empty description
     * 
     */
    public List<String> getDepartmentNameWithAtLeastOneCaWithNoDescription();
    
 
    /**
     * return whether or not the specified Catalog Description exists.
     * 
     * @param course_id - the course_id of the catalog description
     * @return true if the catalog description exists, false otherwise
     */
    public boolean descriptionExists(String course_id);


}
