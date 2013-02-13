
package ca.hec.archive.api;

import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.cdm.model.CatalogDescription;

public interface HecCourseArchiveService {
    
    public List<String> getListInstructors();

    public List<ArchiveCourseSection> getSectionsByCourseId(String course_id);

    public List<CatalogDescription> getListCatalogDescription(String course_id,
	    String title, String instructor, String courseCareerGroup, String courseLanguage);
    
    public void saveCourseMetadataToArchive(COSerialized serializedCO);

}

