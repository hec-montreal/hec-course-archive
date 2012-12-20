package ca.hec.archive.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.stereotype.Controller;

import ca.hec.cdm.model.CatalogDescription;

import ca.hec.archive.logic.SakaiProxy;
import ca.hec.archive.model.ArchiveCourseSection;

@Controller
public class HecCourseArchiveController {

	/**
	 * Course Archive Controller
	 * 
	 * @author Curtis van Osch (curtis.van-osch@hec.ca)
	 * 
	 */
	@Setter
	@Getter
	private SakaiProxy sakaiProxy = null;
	
	public ModelAndView handleRequest(HttpServletRequest arg0,
			HttpServletResponse arg1) throws Exception {
		
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("currentSiteId", sakaiProxy.getCurrentSiteId());
		map.put("userDisplayName", sakaiProxy.getCurrentUserDisplayName());
		
		return new ModelAndView("index", map);
	}

	
	@RequestMapping(value = "/course.json")
	public ModelAndView handleCourseRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	    
	    List<ArchiveCourseSection> sections = null;
	    
	    //pretend to load the sections  ***************************************
	    sections = new ArrayList<ArchiveCourseSection>();
	    
	    CatalogDescription cd = new CatalogDescription();
	    cd.setTitle("Le titre du Cours");
	    cd.setCourseId("1-234-99");
	    cd.setDescription("Voici une belle description annuaire pour un beau cours");
	    cd.setDepartment("Finance");
	    cd.setCareer("Bachelor");
	    cd.setCredits((float) 3);
	    cd.setRequirements("These are the requirements");
	    
	    for (int i=0; i<7; i++) {
		ArchiveCourseSection acs = new ArchiveCourseSection("E201"+i, "B0"+i, cd);
		sections.add(acs);
	    }
	    // ********************************************************************
	    
	    Map<String, Object> map = new HashMap<String,Object>();
	    map.put("courseId", cd.getCourseId());
	    map.put("title", cd.getTitle());
	    map.put("description", cd.getDescription());
	    map.put("department", cd.getDepartment());
	    map.put("career", cd.getCareer());
	    map.put("credits", cd.getCredits());
	    map.put("requirements", cd.getRequirements());
	    
	    List<Map<String, Object>> section_details = new ArrayList<Map<String, Object>>();
	    for (ArchiveCourseSection acs : sections) {
		Map<String, Object> section = new HashMap<String, Object>();
		section.put("session", acs.getSession());
		section.put("section", acs.getSection());
		section.put("instructor", acs.getInstructor());
		section_details.add(section);
	    }
	    map.put("sections", section_details);
	    
	    return new ModelAndView("jsonView", map);
	}
	
	@RequestMapping(value = "/search.json")
	public ModelAndView handleSearch(HttpServletRequest request,
		HttpServletResponse response) throws Exception {
		    
	    //search parameters
	    String course_id = request.getParameter("courseId");
	    String title = request.getParameter("courseTitle");
	    String teacher = request.getParameter("courseTeacher");
	    
	    List<ArchiveCourseSection> sections = new ArrayList<ArchiveCourseSection>();

	    //pretend search ************************************
	    for (int i=0; i<5; i++) {
		CatalogDescription cd = new CatalogDescription();
		cd.setCourseId(course_id + "-" + i);
		cd.setTitle(title + " #"+i);
		sections.add(new ArchiveCourseSection("E201"+i, "A0"+i, cd));
	    }
	    // end pretend search **********************
	    
	    Map<String, Object> map = new HashMap<String,Object>();
	    
	    List<Object> data = new ArrayList<Object>();
	    for (ArchiveCourseSection acs : sections) {
		List<String> array = new ArrayList<String>();
		array.add(acs.getCatalogDescription().getCourseId());
		array.add(acs.getCatalogDescription().getTitle());
		data.add(array);
	    }
	    
	    map.put("aaData", data);
		
	    return new ModelAndView("jsonView", map);
	}
}
