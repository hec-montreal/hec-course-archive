package ca.hec.archive.api;

import java.util.List;
import java.util.Map;

import ca.hec.archive.model.Item;

public interface PortalManagerService
{
    public void init();

    public List<Item> getSubjects(String locale);

    public List<Item> getCareers(String locale);
    
    public Map<String, String> getBundle(String locale);

    public String getSubjectGroup(String subject);

    public String getCareerGroup(String career);
}
