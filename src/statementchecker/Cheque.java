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

/** Extra info about a cheque
 * 
 * @author cmg
 *
 */
public class Cheque {
	private static final int COUNT = 5;
	private static final int CHEQUE = 0;
	private static final int DATE = 1;
	private static final int AMOUNT = 2;
	private static final int DESCRIPTION = 3;
	private static final int CLASSIFICATION = 4;

	public String cheque;
	public String date;
	public Integer amountPence;
	public String description;
	public String classification;
	/** cns */
	public Cheque() {		
	}
	public static Map<String,Cheque> readFile(File f) throws IOException {
		Map<String,Cheque> cs = new HashMap<String,Cheque>();
		BufferedReader br = new BufferedReader(new FileReader(f));
		int count = 0;
		while (true) {
			String line = br.readLine();
			count++;
			if (line==null)
				break;
			String toks[] = line.split(",");
			//Date	Type	Sort Code	Account Number	Description	In	Out	Balance
			if (toks.length!=COUNT) {
				System.err.println("Ignore "+f+":"+count+": "+line);
				continue;
			}
			if (toks[CHEQUE].equals("cheque"))
				continue;
			Cheque c = new Cheque();
			//01/02/2010	FEE	309932	308854	PLATINUM ACCOUNT		12	4180.03
			c.cheque = toks[CHEQUE];
			c.description =toks[DESCRIPTION];
			c.classification= toks[CLASSIFICATION];
			c.date= toks[DATE];
			try {
				c.amountPence = Statement.toPence(toks[AMOUNT]);
			} catch (NumberFormatException nfe) {
				System.err.println("Error parsing pence in "+f+":"+count+": "+line+" ("+nfe+")");
				continue;
			}
			cs.put(c.cheque, c);
		}
		br.close();
		return cs;
	}
}
