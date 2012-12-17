package ca.hec.archive.logic;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ca.hec.archive.model.ArchiveCourseSection;

/**
 * Implementation of {@link ProjectLogic}
 * 
 * @author Mike Jennings (mike_jennings@unc.edu)
 *
 */
public class ProjectLogicImpl implements ProjectLogic {

	private static final Logger log = Logger.getLogger(ProjectLogicImpl.class);

	
	/**
	 * {@inheritDoc}
	 */
	public List<ArchiveCourseSection> getItems() {
		
		List<ArchiveCourseSection> items = new ArrayList<ArchiveCourseSection>();
		
		items.add(new ArchiveCourseSection());
		items.add(new ArchiveCourseSection());
		
		return items;
		
	}
	
	/**
	 * init - perform any actions required here for when this bean starts up
	 */
	public void init() {
		log.info("init");
	}

}
