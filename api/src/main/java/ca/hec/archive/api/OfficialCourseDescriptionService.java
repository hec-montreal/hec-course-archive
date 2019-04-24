package ca.hec.archive.api;

import ca.hec.archive.model.OfficialCourseDescription;

import java.util.List;
import java.util.Map;

/**
 * This is the interface of services used to access Catalog Descriptions
 *  
 * @author 11091096
 */
public interface OfficialCourseDescriptionService {
 
    public List<OfficialCourseDescription> getOfficialCourseDescriptions(Map<String, String> searchWords, Map<String, String> searchScope);

    public OfficialCourseDescription getOfficialCourseDescription(String courseId) ;
    

}

