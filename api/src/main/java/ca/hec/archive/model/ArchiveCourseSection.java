package ca.hec.archive.model;

import ca.hec.cdm.model.CatalogDescription;
import lombok.Data;

@Data
public class ArchiveCourseSection {

	private long id;
	private String session;
	private String section;
	private String period;
	private String instructor;
	private CatalogDescription catalogDescription;
}
