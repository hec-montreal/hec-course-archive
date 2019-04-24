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
	List<Item> bundleDepartments = null;
	List<Item> bundleCareers = null;
	private ResourceBundle listDepartmentsToDisplay = null;
	private ResourceBundle listCareersToDisplay = null;
	private Map<String, String> careerGroups = null;
	private Map<String, String> departmentGroups = null;

	public void init() {

	}


	/**
	 * Return the departments/careers that need to be displayed in HEC public portal
	 * Each department/career have a description and is associated to a group (can include several careers/departments)
	 * @param  itemsType: department/career
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
			listDepartmentsToDisplay = ResourceBundle.getBundle("departments",new Locale(locale));
			initGroup("department");
			listItemsToDisplay = listDepartmentsToDisplay;
			itemGroups =  departmentGroups;
			orderBundle = ResourceBundle.getBundle("order_departments");
		}

		for (String itemKey : (Set<String>) listItemsToDisplay
				.keySet()) {
			String description = listItemsToDisplay.getString(itemKey);
			dpTemp = listDpt.getItemByDescription(description);

			//if dp != null it means that we already have a department associated with this description, so we will update this department instead of creating a new one
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
	 *  Init  the list of career or department groups specified in the properties files
	 * 	A career/department group can include several careers/departments
	 *  @param  itemsType: department/career
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
			listItemsToDisplay = listDepartmentsToDisplay;
			departmentGroups = new HashMap<String, String>();
			listitemGroups = departmentGroups;
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


	/**
	 *  Return the group description associated with the department group
	 * 	A department group can include several departments
	 *  @param department - the group code whose description should be returned
	 *  @param locale - the language for the description
	 */
	public String getDepartmentDescription(String department, Locale locale) {
		ResourceBundle departmentDescriptions = ResourceBundle.getBundle("departments", locale);
		return departmentDescriptions.getString(department);
	}

	/**
	 *  Return the group description associated with the career group 
	 * 	A career group can include several careers
	 *  @param career - the group code whose description should be returned
	 *  @param locale - the language for the description
	 */
	public String getCareerDescription(String career, Locale locale) {
		ResourceBundle careerDescriptions = ResourceBundle.getBundle("careers", locale);

		if (careerDescriptions.containsKey(career)) {
			return careerDescriptions.getString(career);
		} 
		else {
			return ResourceBundle.getBundle("portal", locale).getString("label_unknown_category");
		}
	}



	public Map<String, String> getBundle(String locale) {
		Map<String, String> msgsBundle = new HashMap<String, String>();
		msgs = ResourceBundle.getBundle("portal", new Locale(locale));	
		bundleDepartments = getItems("department",locale);
		bundleCareers = getItems("career",locale);


		for (String key : msgs.keySet()) {
			msgsBundle.put((String) key, (String) msgs.getString(key));
		}

		for (Item item : bundleDepartments) {
			String key = "department_" + item.getItemGroup();
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


	public String getDepartmentGroup(String department) {
		return departmentGroups.get(department);
	}


	public String getCareerGroup(String career) {
		return careerGroups.get(career);
	}    


	public List<Item> getDepartments(String locale) {
		return getItems("department",locale);
	}

	public List<Item> getCareers(String locale) {
		return getItems("career",locale);
	}
}
