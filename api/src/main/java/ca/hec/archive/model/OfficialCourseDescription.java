/**
 * 
 */
package ca.hec.archive.model;

import lombok.Data;

/**
 * <p>
 * OfficialCourseDescription is the object for official course outline description
 * 
 * </p>
 */
@Data
public class OfficialCourseDescription {
	
	private String catalogNbr;
	private String courseId;
	private String subject;
	private String title;
	private String description;
	private String career;
	private String requirements;

	private String credits;
	private String language;
	private String shortDescription;
	private String themes;



}
