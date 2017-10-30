package ca.hec.archive.tool;

import ca.hec.archive.api.HecCourseArchiveService;
import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.archive.util.ArchiveUtils;
import ca.hec.commons.utils.FormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.net.URLDecoder;
import java.util.*;

@Controller
public class HecCourseArchiveController {

    /**
     * Course Archive Controller
     * 
     * @author Curtis van Osch (curtis.van-osch@hec.ca)
     */
     @Autowired
    private HecCourseArchiveService hecCourseArchiveService;

    private ResourceLoader msgs = null;

    private static Log log = LogFactory
	    .getLog(HecCourseArchiveController.class);
    private static String key_no_instructor = "no_instructor";

    @PostConstruct
    public void init() {
	msgs = new ResourceLoader("archives");
    }

    @RequestMapping(value = "/course_sections.json", method=RequestMethod.GET)
    public @ResponseBody Map<String, Object> handleCourseRequest(@RequestParam("courseId") String course_id) throws Exception {

	String pdf_url = null;
	//remove -
	if (course_id.contains("-"))
		course_id = course_id.replace("-","");

	List<ArchiveCourseSection> sections =
		hecCourseArchiveService.getSectionsByCourseId(course_id);

	Map<String, Object> map = new HashMap<String, Object>();

	List<Map<String, Object>> sections_details =
		new ArrayList<Map<String, Object>>();
	for (ArchiveCourseSection acs : sections) {
	    Map<String, Object> section = new HashMap<String, Object>();
	    section.put("session", acs.getSession());
	    section.put("section", acs.getSection());
	    if (acs.getInstructor() == null){
		section.put("instructor", msgs.get(key_no_instructor));
	    }else{
		section.put("instructor", acs.getInstructor());
	    }
	    
	    

	    // generate pdf url, maybe should be in utils?
	    String site_id =
				FormatUtils.formatCourseId(acs.getCourseId()) + "."
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

	return map;
    }

    @RequestMapping(value = "/search.json", method=RequestMethod.GET)
    public @ResponseBody Map<String, Object> handleSearch(@RequestParam("courseId") String course_id,
    		@RequestParam("courseTitle") String title, @RequestParam("courseInstructor") String instructor, 
    		@RequestParam("courseCareerGroup") String courseCareerGroup, @RequestParam("courseLanguage") String courseLanguage) throws Exception {

	
	// search parameters
	 course_id = course_id.trim();

	 //remove -
	 if (course_id.contains("-"))
	 	course_id = course_id.replace("-","");
	 title = URLDecoder.decode(title.trim(),"UTF-8");

    instructor = URLDecoder.decode(instructor, "UTF-8");
	courseLanguage = ArchiveUtils.getCorrespondenceLocaleLanguage(courseLanguage);

	List<ArchiveCourseSection> archiveCourseSections =
		hecCourseArchiveService.getListArchiveCourseSections(course_id,
			title, instructor, courseCareerGroup, courseLanguage);

	Map<String, Object> map = new HashMap<String, Object>();

	Set<Object> data = new HashSet<Object>();
	for (ArchiveCourseSection cd : archiveCourseSections) {
	    List<String> arrayList = new ArrayList<String>();
	    arrayList.add(FormatUtils.formatCourseId(cd.getCourseId()));
	    arrayList.add(cd.getTitle());
	    data.add(arrayList);
	}

	map.put("aaData", data);

	return  map;
    }

    @RequestMapping(value = "/instructors.json", method=RequestMethod.GET)
    public @ResponseBody Map<String, Object> handleListInstructors() throws Exception {

	List<String> instructors = hecCourseArchiveService.getListInstructors();
	java.util.Collections.sort(instructors);

	Map<String, Object> map = new HashMap<String, Object>();

	map.put("data", instructors);

	return  map;
    }

    /*
     * Called to get tool bundles (for use in javascript function)
     */
    @RequestMapping(value = "/bundle.json", method=RequestMethod.GET)
    public @ResponseBody Map<String, Object> getBundle() throws Exception {

	Map<String, String> msgsBundle = new HashMap<String, String>();
	for (Object key : msgs.keySet()) {
	    msgsBundle.put((String) key, (String) msgs.get(key));
	}

	Map<String, Object> model = new HashMap<String, Object>();

	model.put("data", msgsBundle);

	String locale = msgs.getLocale().toString();
	// because "en" is the default locale, sakai return "" when user
	// preferences are in English
	if ("".equals(locale)) {
	    locale = "en";
	}
	model.put("locale", locale);

	return model;
    }
}
