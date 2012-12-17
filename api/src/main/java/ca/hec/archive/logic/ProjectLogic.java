package ca.hec.archive.logic;

import java.util.List;

import ca.hec.archive.model.ArchiveCourseSection;

/**
 * An example logic interface
 * 
 * @author Mike Jennings (mike_jennings@unc.edu)
 *
 */
public interface ProjectLogic {

	/**
	 * Get a list of Items
	 * @return
	 */
	public List<ArchiveCourseSection> getItems();
}
