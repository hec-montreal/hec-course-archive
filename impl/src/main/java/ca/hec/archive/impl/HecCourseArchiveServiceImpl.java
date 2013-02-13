package ca.hec.archive.impl;

import java.io.IOException;
import java.util.List;

import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.hec.archive.api.HecCourseArchiveService;
import ca.hec.archive.dao.ArchiveDao;
import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.cdm.model.CatalogDescription;

public class HecCourseArchiveServiceImpl implements HecCourseArchiveService {

    @Setter
    private ArchiveDao archiveDao;
    
    private static Log log = LogFactory.getLog(HecCourseArchiveServiceImpl.class);

    public List<String> getListInstructors() {
	return archiveDao.getListIstructors();
    }

    public List<ArchiveCourseSection> getSectionsByCourseId(String course_id) {
	return archiveDao.getSectionsByCourseId(course_id);
    }
    
    public List<CatalogDescription> getListCatalogDescription(String course_id,
	    String title, String instructor, String courseCareerGroup, String courseLanguage) {
	return archiveDao.getListCatalogDescription(course_id, title, instructor, courseCareerGroup, courseLanguage);
    }
    
    public void saveCourseMetadataToArchive(String serializedCO) {
	log.error("serializedCO: " + serializedCO);
    }

}
