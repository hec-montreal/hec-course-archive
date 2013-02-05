package ca.hec.archive.tool;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.util.ResourceLoader;
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
     */
    @Setter
    @Getter
    private SakaiProxy sakaiProxy = null;

    @Setter
    @Autowired
    private HecCourseArchiveService hecCourseArchiveService;

    private ResourceLoader msgs = null;

    private static Log log = LogFactory
	    .getLog(HecCourseArchiveController.class);

    @PostConstruct
    public void init() {
	msgs = new ResourceLoader("archives");
    }

    public ModelAndView handleRequest(HttpServletRequest arg0,
	    HttpServletResponse arg1) throws Exception {

	Map<String, Object> map = new HashMap<String, Object>();
	map.put("currentSiteId", sakaiProxy.getCurrentSiteId());
	map.put("userDisplayName", sakaiProxy.getCurrentUserDisplayName());

	return new ModelAndView("index", map);
    }

    @RequestMapping(value = "/course_sections.json")
    public ModelAndView handleCourseRequest(HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	String course_id = request.getParameter("courseId");
	String pdf_url = null;

	List<ArchiveCourseSection> sections =
		hecCourseArchiveService.getSectionsByCourseId(course_id);

	Map<String, Object> map = new HashMap<String, Object>();

	List<Map<String, Object>> sections_details =
		new ArrayList<Map<String, Object>>();
	for (ArchiveCourseSection acs : sections) {
	    Map<String, Object> section = new HashMap<String, Object>();
	    section.put("session", acs.getSession());
	    section.put("section", acs.getSection());
	    section.put("instructor", acs.getInstructor());

	    // generate pdf url, maybe should be in utils?
	    String site_id =
		    acs.getCatalogDescription().getCourseId() + "."
			    + acs.getSession();

	    if (acs.getPeriod() != null && acs.getPeriod() != ""
		    && !acs.getPeriod().equals("1")) {
		site_id += "." + acs.getPeriod();
	    }
	    site_id += "." + acs.getSection();
	    pdf_url =
		    "/sdata/c/attachment/" + site_id + "/OpenSyllabus/"
			    + site_id + "_public.pdf";
	    // end pdf url

	    section.put("pdf_url", pdf_url);
	    sections_details.add(section);
	}
	map.put("data", sections_details);

	return new ModelAndView("jsonView", map);
    }

    @RequestMapping(value = "/search.json")
    public ModelAndView handleSearch(HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	// search parameters
	String course_id = request.getParameter("courseId").trim();
	String title =
		URLDecoder.decode(request.getParameter("courseTitle").trim(),
			"UTF-8");
	String instructor =
		URLDecoder.decode(request.getParameter("courseInstructor"),
			"UTF-8");

	List<CatalogDescription> catalogDescriptions =
		hecCourseArchiveService.getListCatalogDescription(course_id,
			title, instructor);

	Map<String, Object> map = new HashMap<String, Object>();

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

	Map<String, Object> map = new HashMap<String, Object>();

	map.put("data", instructors);

	return new ModelAndView("jsonView", map);
    }

    /*
     * Called to get tool bundles (for use in javascript function)
     */
    @RequestMapping(value = "/bundle.json")
    public ModelAndView getBundle(HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	Map<String, String> msgsBundle = new HashMap<String, String>();
	for (Object key : msgs.keySet()) {
	    msgsBundle.put((String) key, (String) msgs.get(key));
	}

	Map<String, Object> model = new HashMap<String, Object>();

	model.put("data", msgsBundle);

	return new ModelAndView("jsonView", model);
    }
}
