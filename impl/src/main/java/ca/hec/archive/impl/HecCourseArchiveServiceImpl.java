/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2013 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package ca.hec.archive.impl;

import java.util.List;

import lombok.Setter;
import ca.hec.archive.api.HecCourseArchiveService;
import ca.hec.archive.dao.ArchiveDao;
import ca.hec.archive.model.ArchiveCourseSection;
import ca.hec.cdm.model.CatalogDescription;

public class HecCourseArchiveServiceImpl implements HecCourseArchiveService {

    @Setter
    private ArchiveDao archiveDao;

    public List<String> getListInstructors() {
	return archiveDao.getListIstructors();
    }

    public List<ArchiveCourseSection> getListCourseSection(String course_id) {
	return archiveDao.getListCourseSection(course_id);
    }

    public List<CatalogDescription> getListCatalogDescription(String course_id,
	    String title, String instructor) {
	return archiveDao.getListCatalogDescription(course_id, title, instructor);
    }

}
