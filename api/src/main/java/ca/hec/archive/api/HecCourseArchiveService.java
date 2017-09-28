
package ca.hec.archive.api;

import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.cdm.model.CatalogDescription;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

import java.util.List;
import java.util.Set;

public interface HecCourseArchiveService {
    
    public List<String> getListInstructors();

    public List<ArchiveCourseSection> getSectionsByCourseId(String course_id);

    public List<CatalogDescription> getListCatalogDescription(String course_id,
	    String title, String instructor, String courseCareerGroup, String courseLanguage);
    
    public void saveCourseMetadataToArchive(COSerialized serializedCO);

    public void saveCourseMetadataToArchive(String  siteId, String syllabusId, Set<String> syllabusSections);

    public void deleteArchiveCourseSection(String site_id);
}

