/**
 * Copyright 2010 The University of Nottingham
 * 
 * This file is part of statementchecker.
 *
 *  statementchecker is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  statementchecker is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with statementchecker.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package statementchecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** Extra info about a budget.
 * CSV file, headings: class,amount [annual total]
 * 
 * @author cmg
 *
 */
public class Budget {
	private static final int COUNT = 2;
	private static final int CLASSIFICATION = 0;
	private static final int AMOUNT = 1;

	public String classification;
	public Integer amountPence;
	/** cns */
	public Budget() {		
	}
	public static Map<String,Budget> readFile(File f) throws IOException {
		Map<String,Budget> bs = new HashMap<String,Budget>();
		BufferedReader br = new BufferedReader(new FileReader(f));
		int count = 0;
		while (true) {
			String line = br.readLine();
			count++;
			if (line==null)
				break;
			String toks[] = line.split(",");
			if (toks.length!=COUNT) {
				System.err.println("Ignore "+f+":"+count+": "+line);
				continue;
			}
			if (toks[CLASSIFICATION].toLowerCase().equals("class"))
				continue;
			Budget a = new Budget();
			a.classification= toks[CLASSIFICATION];
			try {
				a.amountPence = Statement.toPence(toks[AMOUNT]);
			} catch (NumberFormatException nfe) {
				System.err.println("Error parsing pence in "+f+":"+count+": "+line+" ("+nfe+")");
				continue;
			}
			bs.put(a.classification, a);
		}
		br.close();
		return bs;
	}
}
