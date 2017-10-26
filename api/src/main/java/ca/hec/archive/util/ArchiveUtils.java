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
package ca.hec.archive.util;
/**
 *
 * Class that gather utils functions shared among archive projects
 */
public class ArchiveUtils {
    
    //return correspondence between locale and language description storage in catalog description table
    public static String getCorrespondenceLocaleLanguage(String locale) {
	if ("en".equals(locale)){
	    return "ANGLAIS";
	}
	
	if ("es".equals(locale)){
	    return "ESPAGNOL";
	}
	
	if ("fr".equals(locale)){
	    return "FRANÇAIS";
	}
	
	return "";
    }
}

