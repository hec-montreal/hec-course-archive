package ca.hec.archive.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ca.hec.archive.api.HecCourseArchiveService;
import ca.hec.archive.logic.SakaiProxy;
import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.cdm.model.CatalogDescription;

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
	
	 @Setter
	 @Autowired
	 private HecCourseArchiveService hecCourseArchiveService;
	
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
	    cd.setTitle("Finance");
	    cd.setCourseId("2-200-96");
	    cd.setDescription("Le cours de finance vise essentiellement à initier l'étudiant à la finance corporative. L'accent est donc mis sur l'apprentissage des outils de base en finance ainsi que sur la compréhension des concepts les plus importants de ce domaine. Plus précisément, le cours a pour objectif de familiariser l'étudiant aux sujets suivants : les fondements de l'évaluation, la décision d'investissement, le coût du capital et du financement des entreprises ainsi que les aspects pratiques du financement des entreprises.");
	    cd.setDepartment("Finance");
	    cd.setCareer("Baccalauréat en administration des affaires (B.A.A.)");
	    cd.setCredits((float) 3);
	    cd.setRequirements("Préalable(s) : 1-612-96 / Pour version anglaise voir 2-200-97A");

		//create several section
		ArchiveCourseSection acs0 = new ArchiveCourseSection("Hiver 2013", "A01", cd);
		acs0.setInstructor("Tarte,Jean-Philippe");
		sections.add(acs0);
		ArchiveCourseSection acs1 = new ArchiveCourseSection("Hiver 2013", "C01", cd);
		acs1.setInstructor("Tremblay,Pierre-Marc");
		sections.add(acs1);
		ArchiveCourseSection acs2 = new ArchiveCourseSection("Hiver 2012", "A02", cd);
		acs2.setInstructor("Tremblay,Pierre-Marc");
		sections.add(acs2);
		ArchiveCourseSection acs3 = new ArchiveCourseSection("Hiver 2012", "B02", cd);
		acs3.setInstructor("Allard,Marie-Hélène & Bouchard,Philippe");
		sections.add(acs3);
		ArchiveCourseSection acs4 = new ArchiveCourseSection("Hiver 2012", "C01", cd);
		acs4.setInstructor("Tremblay,Pierre-Marc & Bouchard,Philippe");
		sections.add(acs4);
		ArchiveCourseSection acs5 = new ArchiveCourseSection("Hiver 2012", "D03", cd);
		acs5.setInstructor("Valéry,Pascale");
		sections.add(acs5);
		ArchiveCourseSection acs6 = new ArchiveCourseSection("Hiver 2011", "C02", cd);
		acs6.setInstructor("Amvella Motaze,Serge Patrick");
		sections.add(acs6);
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
	    String course_id = request.getParameter("courseId").trim();
	    String title = request.getParameter("courseTitle").trim();
	    String instructor = request.getParameter("courseInstructor");
	    
	    List<CatalogDescription> catalogDescriptions = hecCourseArchiveService.getListCatalogDescription(course_id, title, instructor);
	    
	    Map<String, Object> map = new HashMap<String,Object>();
	    
	    List<Object> data = new ArrayList<Object>();
	    for (CatalogDescription cd : catalogDescriptions) {
		List<String> array = new ArrayList<String>();
		array.add(cd.getCourseId());
		array.add(cd.getTitle());
		data.add(array);
	    }
	    
	    map.put("aaData", data);
		
	    return new ModelAndView("jsonView", map);
	}
	
	@RequestMapping(value = "/instructors.json")
	public ModelAndView handleListInstructors(HttpServletRequest request,
		HttpServletResponse response) throws Exception {
	    
	    List<String> instructors = hecCourseArchiveService.getListInstructors();
	    
	    Map<String, Object> map = new HashMap<String,Object>();
	    
	    map.put("data", instructors);
		
	    return new ModelAndView("jsonView", map);
	}
}
