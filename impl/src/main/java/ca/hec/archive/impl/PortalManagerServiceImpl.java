package ca.hec.archive.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import ca.hec.archive.api.PortalManagerService;
import ca.hec.archive.model.Item;
import ca.hec.archive.model.ItemFactory;

public class PortalManagerServiceImpl implements PortalManagerService {

	private ResourceBundle msgs = null;
	List<Item> bundleSubjects = null;
	List<Item> bundleCareers = null;
	private ResourceBundle listSubjectsToDisplay = null;
	private ResourceBundle listCareersToDisplay = null;
	private Map<String, String> careerGroups = null;
	private Map<String, String> subjectGroups = null;

	public void init() {

	}


	/**
	 * Return the subjects/careers that need to be displayed in HEC public portal
	 * Each subject/career have a description and is associated to a group (can include several careers/subjects)
	 * @param  itemsType: subject/career
	 */
	public List<Item> getItems(String itemsType, String locale) {
		ItemFactory listDpt = new ItemFactory();
		Item dpTemp = null;
		ResourceBundle listItemsToDisplay = null;
		ResourceBundle orderBundle = null;
		Map<String, String> itemGroups;

		if ("career".equals(itemsType)){	    
			listCareersToDisplay = ResourceBundle.getBundle("careers",new Locale(locale));
			initGroup("career");
			listItemsToDisplay = listCareersToDisplay;
			itemGroups =  careerGroups;
			orderBundle = ResourceBundle.getBundle("order_careers");
		}
		else{
			listSubjectsToDisplay = ResourceBundle.getBundle("subjects",new Locale(locale));
			initGroup("subject");
			listItemsToDisplay = listSubjectsToDisplay;
			itemGroups =  subjectGroups;
			orderBundle = ResourceBundle.getBundle("order_subjects");
		}

		for (String itemKey : (Set<String>) listItemsToDisplay
				.keySet()) {
			String description = listItemsToDisplay.getString(itemKey);
			dpTemp = listDpt.getItemByDescription(description);

			//if dp != null it means that we already have a subject associated with this description, so we will update this subject instead of creating a new one
			if (dpTemp == null){
				Item dp = new Item();
				dp.setDescription(listItemsToDisplay.getString(itemKey));
				dp.setItemGroup(itemGroups.get(itemKey));
				dp.setOrder(Integer.parseInt(orderBundle.getString(itemKey)));
				listDpt.add(dp);
			}
		}

		List<Item> returnItemList = listDpt.getListItem();
		Collections.sort(returnItemList);
		return returnItemList;
	}


	/**
	 *  Init  the list of career or subject groups specified in the properties files
	 * 	A career/subject group can include several careers/subjects
	 *  @param  itemsType: subject/career
	 */
	public void initGroup(String itemsType) {
		ResourceBundle listItemsToDisplay = null;
		Map<String, String> listitemGroups = null;
		Map<String, String> listItemDescriptions  = new HashMap<String, String>();

		if ("career".equals(itemsType)){
			listItemsToDisplay = listCareersToDisplay;
			careerGroups = new HashMap<String, String>();
			listitemGroups = careerGroups;
		}
		else{
			listItemsToDisplay = listSubjectsToDisplay;
			subjectGroups = new HashMap<String, String>();
			listitemGroups = subjectGroups;
		}

		for (String key : listItemsToDisplay.keySet()) {
			String itemDescription = listItemsToDisplay.getString(key);
			String itemGroup =   listItemDescriptions.get(itemDescription);
			if (itemGroup == null){
				listItemDescriptions.put(itemDescription, key);
			}
			else{
				listItemDescriptions.put(itemDescription, itemGroup + "+" + key);
			}

		}

		for (String key : listItemsToDisplay.keySet()) {
			String itemDescription = listItemsToDisplay.getString(key);
			listitemGroups.put(key, listItemDescriptions.get(itemDescription));
		}

	}

	public Map<String, String> getBundle(String locale) {
		Map<String, String> msgsBundle = new HashMap<String, String>();
		msgs = ResourceBundle.getBundle("portal", new Locale(locale));	
		bundleSubjects = getItems("subject",locale);
		bundleCareers = getItems("career",locale);

		for (String key : msgs.keySet()) {
			msgsBundle.put((String) key, (String) msgs.getString(key));
		}

		for (Item item : bundleSubjects) {
			String key = "subject_" + item.getItemGroup();
			String description = item.getDescription();
			msgsBundle.put(key, description);
		}

		for (Item item : bundleCareers) {
			String key = "career_" + item.getItemGroup();
			String description = item.getDescription();
			msgsBundle.put(key, description);
		}


		return msgsBundle;
	}


	public String getSubjectGroup(String subject) {
		return subjectGroups.get(subject);
	}


	public String getCareerGroup(String career) {
		return careerGroups.get(career);
	}    


	public List<Item> getSubjects(String locale) {
		return getItems("subject",locale);
	}

	public List<Item> getCareers(String locale) {
		return getItems("career",locale);
	}
}
