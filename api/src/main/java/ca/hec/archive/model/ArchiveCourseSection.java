package ca.hec.archive.model;

import ca.hec.portal.model.OfficialCourseDescription;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ArchiveCourseSection {

	private long id;
	@NonNull private String session;
	@NonNull private String section;
	private String period;
	private String instructor;
	private OfficialCourseDescription officialCourseDescription;
	
	// these are defined ONLY for sorting the results in ArchiveDaoImpl
	private Character session_letter;
	private String year;
		
}
