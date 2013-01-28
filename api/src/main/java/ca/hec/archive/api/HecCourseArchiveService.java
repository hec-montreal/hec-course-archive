
package ca.hec.archive.api;

import java.util.List;

import ca.hec.archive.model.ArchiveCourseSection;

public interface HecCourseArchiveService {
    
    public List<String> getListInstructors();

    public List<ArchiveCourseSection> getListCourseSection(String course_id,
	    String title, String instructor);

}

