package ca.hec.archive.entityprovider;

import ca.hec.commons.utils.FormatUtils;
import ca.hec.archive.api.OfficialCourseDescriptionService;
import ca.hec.archive.api.PortalManagerService;
import ca.hec.archive.model.OfficialCourseDescription;
import ca.hec.archive.model.SimpleOfficialCourseDescription;
import lombok.Setter;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybroker.entityprovider.capabilities.*;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class PortalManagerEntityProviderImpl extends AbstractEntityProvider
implements CoreEntityProvider, AutoRegisterEntityProvider, Resolvable, Sampleable, Outputable, ActionsExecutable {

	public final static String ENTITY_PREFIX = "portalManager";

	@Setter
	@Autowired
	private PortalManagerService portalManagerService;

	@Setter
	@Autowired
	private OfficialCourseDescriptionService officialCourseDescriptionService;

	public String[] getHandledOutputFormats() {
		return new String[] { Formats.JSON, Formats.HTML, Formats.XML };
	}

	@EntityCustomAction(action = "getOfficialCourseDescription", viewKey = EntityView.VIEW_LIST)
	public Object getOfficialCourseDescription(EntityView view, Map<String, Object> params) {
		String courseId = (String)params.get("courseId");
		//remove -
		if (courseId.contains("-"))
			courseId = courseId.replace("-","");

		OfficialCourseDescription ocd = officialCourseDescriptionService.getOfficialCourseDescription(courseId);

		if (ocd == null)
			return null;
		else
			return simplifyOfficialCourseDescription(ocd);
	}

	public SimpleOfficialCourseDescription simplifyOfficialCourseDescription(
			OfficialCourseDescription cd) {
		SimpleOfficialCourseDescription scd = new SimpleOfficialCourseDescription();

		scd.setTitle(cd.getTitle());
		if (cd.getDescription() != null)
			scd.setDescription(cd.getDescription().replace("\n", "</br>") );
		else
			scd.setDescription(cd.getDescription());
		scd.setDepartmentGroup(portalManagerService.getDepartmentGroup(cd.getDepartment()));
		scd.setCareerGroup(portalManagerService.getCareerGroup(cd.getCareer()));
		scd.setRequirements(cd.getRequirements());
		scd.setCourseId(cd.getCourseId());
		scd.setHyphenatedCourseId(FormatUtils.formatCourseId(cd.getCourseId()));
		scd.setCredits("" + cd.getCredits());
		if (cd.getShortDescription() != null)
			scd.setShortDescription(cd.getShortDescription().replace("\n", "</br>") );
		else
			scd.setShortDescription(cd.getShortDescription());
		if (cd.getThemes() != null)
			scd.setThemes(cd.getThemes().replace("\n", "</br>") );
		else
			scd.setThemes(cd.getThemes());
		String lang = cd.getLanguage();
		if (lang != null)
			scd.setLang(lang.substring(0, 2));

		return scd;
	}

	@EntityCustomAction(action = "getDepartments", viewKey = EntityView.VIEW_LIST)
	public List<?> getDepartments(EntityView view, Map<String, Object> params) {
		String locale = view.getPathSegment(2);
		return portalManagerService.getDepartments(locale);
	}

	@EntityCustomAction(action = "getCareers", viewKey = EntityView.VIEW_LIST)
	public List<?> getCareers(EntityView view, Map<String, Object> params) {
		String locale = view.getPathSegment(2);
		return portalManagerService.getCareers(locale);
	}

	public String getEntityPrefix() {
		return ENTITY_PREFIX;
	}

	public Object getSampleEntity() {
		return null;
	}

	public Object getEntity(EntityReference ref) {
		return null;
	}

	public boolean entityExists(String id) {
		return false;
	}

}
