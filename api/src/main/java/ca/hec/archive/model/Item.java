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

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

/**
 *
 * class that factorize properties/function shared by Career or Department
 */
@Data
public class Item implements Comparable<Item>{
    private String description;
    private String itemGroup;
    private Integer order;
    
    public Item(){
       }


    public int compareTo(Item it) {
	return order.compareTo(it.getOrder());
    }
    
}

