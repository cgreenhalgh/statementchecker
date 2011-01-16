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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** Extra info about allocating an outgoing to a different budget class than its default.
 * CSV file, headings: date,autoclass,autoamount,amount,description,class
 * 
 * @author cmg
 *
 */
public class Allocation {
	private static final int COUNT = 6;
	private static final int DATE = 0;
	private static final int AUTOCLASSIFICATION = 1;
	private static final int AUTOAMOUNT = 2;
	private static final int AMOUNT = 3;
	private static final int DESCRIPTION = 4;
	private static final int CLASSIFICATION = 5;

	public Date date;
	public String autoclassification;
	public Integer autoamountPence;
	public Integer amountPence;
	public String description;
	public String classification;
	/** cns */
	public Allocation() {		
	}
	public static List<Allocation> readFile(File f) throws IOException {
		List<Allocation> as = new LinkedList<Allocation>();
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
			if (toks[DATE].toLowerCase().equals("date"))
				continue;
			Allocation a = new Allocation();
			//a.date= toks[DATE];
			a.autoclassification = toks[AUTOCLASSIFICATION];
			a.description =toks[DESCRIPTION];
			a.classification= toks[CLASSIFICATION];
			try {
				a.amountPence = Statement.toPence(toks[AMOUNT]);
				a.autoamountPence = Statement.toPence(toks[AUTOAMOUNT]);
			} catch (NumberFormatException nfe) {
				System.err.println("Error parsing pence in "+f+":"+count+": "+line+" ("+nfe+")");
				continue;
			}
			try {
				a.date = Statement.dateFormat.parse(toks[DATE]);
			}
			catch (Exception e) {
				System.err.println("Error parsing date in "+f+":"+count+": "+line);
			}
			as.add(a);
		}
		br.close();
		return as;
	}
	public static class DateComparator implements Comparator<Allocation> {
		@Override
		public int compare(Allocation t0, Allocation t1) {
			return t0.date.compareTo(t1.date);
		}		
	}
}
