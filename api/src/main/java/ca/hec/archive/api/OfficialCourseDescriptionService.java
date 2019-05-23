package ca.hec.archive.api;

import ca.hec.archive.model.OfficialCourseDescription;

/**
 * This is the interface of services used to access Catalog Descriptions
 *  
 * @author 11091096
 */
public interface OfficialCourseDescriptionService {
 
    public OfficialCourseDescription getOfficialCourseDescription(String courseId) ;

}

