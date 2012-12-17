package ca.hec.archive.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveCourseSection {

	private long id;
	private String session;
	private String section;
	private String period;
	private String instructor;
}
