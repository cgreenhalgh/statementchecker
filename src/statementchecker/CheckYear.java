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
import java.io.FileWriter;
import java.io.PrintWriter;

/** Run over a year's transactions dumped (from bank account) and 
 * build up a month-by-month breakdown/profile.
 * 
 * @author cmg
 *
 */
public class CheckYear {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length!=1) {
			System.err.println("Usage: <statement.csv>");
			System.exit(-1);
		}
		try {
			Statement s = Statement.readStatement(new File(args[0]));
			System.out.println("Read "+s.getTransactions().size()+" transactions:");
			//for (Transaction t: s.getTransactions()) 
			//	System.out.println(t.toString());
			System.out.println("Output by type to bytype.csv");
			PrintWriter bytype = new PrintWriter(new FileWriter("bytype.csv"));
			bytype.println("month,year,");
			bytype.close();
		}
		catch (Exception e) {
			System.err.println("Error: "+e);
			e.printStackTrace(System.err);
		}
	}

}
