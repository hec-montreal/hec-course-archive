/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2012 The Sakai Foundation, The Sakai Quebec Team.
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
package ca.hec.archive.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 *
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @version $Id: $
 */
@Data
public class ItemFactory {
    
    private List<Item> listItem;
    
    public ItemFactory(){
	listItem = new ArrayList<Item>();
    }
    
    /**
     * Get the first Item that have the folowing description
     */
    public Item getItemByDescription(String description){
	for (Item it : listItem){
	    if (description.equals(it.getDescription())){
		return it;
	    }
	}
	return null;
    }

    public void add(Item it) {
	listItem.add(it);
    }

}

