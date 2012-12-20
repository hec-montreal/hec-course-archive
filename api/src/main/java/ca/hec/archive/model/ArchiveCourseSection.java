package ca.hec.archive.model;

import ca.hec.cdm.model.CatalogDescription;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ArchiveCourseSection {

	private long id;
	@NonNull private String session;
	@NonNull private String section;
	private String period;
	private String instructor;
	@NonNull private CatalogDescription catalogDescription;
}
