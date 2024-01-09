
/* This file is copyright (c) 2008-2013 Philippe Fournier-Viger
* 
* This file is part of the SPMF DATA MINING SOFTWARE
* (http://www.philippe-fournier-viger.com/spmf).
* 
* SPMF is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* 
* SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* SPMF. If not, see <http://www.gnu.org/licenses/>.
*/

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an itemset (a set of items) with utility information found
 * by the TWO-PHASE algorithm.
 *
 * @author Philippe Fournier-Viger
 */
public class Itemset {

	private List<Integer> items = new ArrayList<>();

	private int utility =0;


	public Itemset(List<Integer> itemset, int utility){
		this.items=itemset;
		this.utility=utility;
	}

	public List<Integer> getItems() {
		return items;
	}

	public void setItems(List<Integer> items) {
		this.items = items;
	}

	public int getUtility() {
		return utility;
	}

	public void setUtility(int utility) {
		this.utility = utility;
	}
}
