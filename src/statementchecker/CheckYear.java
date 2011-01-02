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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
		if (args.length!=2) {
			System.err.println("Usage: <statement.csv> <cheques.csv>");
			System.err.println("Cheques: cheque,date,amount,description,classification");
			System.exit(-1);
		}
		try {
			Map<String,Cheque> cheques = Cheque.readFile(new File(args[1]));
			System.out.println("Read "+cheques.size()+" cheques");
			Statement s = Statement.readStatement(new File(args[0]), cheques);
			System.out.println("Read "+s.getTransactions().size()+" transactions:");
			//for (Transaction t: s.getTransactions()) 
			//	System.out.println(t.toString());
			{
				System.out.println("Output by type to bytype.csv");
				PrintWriter bytype = new PrintWriter(new FileWriter("bytype.csv"));
				bytype.print("month,year");
				TreeSet<String> types = s.getTypes();
				for (String type : types)
					bytype.print(","+type+"in,"+type+"out");
				bytype.println();
				Set<Integer> years = s.getYears();
				int grandtotals[] = new int[types.size()*2];
				for (int year : years) {
					for (int month=1; month<=12; month++) {
						int totals[] = new int[types.size()*2];
						for (Transaction t: s.getTransactions())
							if (t.year==year && t.month==month) {
								int ix = 0;
								for (Iterator<String> ti = types.iterator(); ti.hasNext(); ix++)
									if (ti.next().equals(t.type))
										break;
								if (t.inPence!=null)
									totals[ix*2] += t.inPence;
								if (t.outPence!=null)
									totals[ix*2+1] += t.outPence;							
							}
						bytype.print(month+","+year);
						for (int i=0; i<totals.length; i++) {
							bytype.print(","+totals[i]);
							grandtotals[i] += totals[i];
						}
						
						bytype.println();
					}
				}
				bytype.print("total,");
				for (int i=0; i<grandtotals.length; i++) {
					bytype.print(","+grandtotals[i]);
				}
				bytype.println();
				bytype.close();
			}
			{
				System.out.println("Output by classification to byclass.csv");
				PrintWriter byclass = new PrintWriter(new FileWriter("byclass.csv"));
				byclass.print("class");
				Set<Integer> years = s.getYears();
				for (int year : years) {
					for (int month=1; month<=12; month++) {
						byclass.print(","+month+"/"+year);
					}
				}
				byclass.println(",total,average");
				TreeSet<String> classes = s.getClassifications();
				int ix = 0;
				for (Iterator<String> ti = classes.iterator(); ti.hasNext(); ix++) {
					String clazz = ti.next();
					byclass.print(clazz);
					int grandtotal = 0;
					for (int year : years) {
						for (int month=1; month<=12; month++) {
							int total = 0;
							for (Transaction t: s.getTransactions())
								if (t.year==year && t.month==month && clazz.equals(t.classify())) {
									if (t.inPence!=null)
										total += t.inPence;
									if (t.outPence!=null)
										total -= t.outPence;							
							}
							byclass.print(","+(total*0.01));
							grandtotal += total;
						}
					}
					byclass.println(","+(grandtotal*0.01)+","+((grandtotal/(12*years.size()))*0.01));
				}
				byclass.close();
			}
			{
				System.out.println("Output with classification to withclass.csv");
				PrintWriter byclass = new PrintWriter(new FileWriter("withclass.csv"));
				byclass.println("month,year,day,class,type,description,in,out");
				for (Transaction t: s.getTransactions()) {
					byclass.println(t.month+","+t.year+","+t.day+","+t.classify()+","+t.type+","+t.description+","+(t.inPence!=null ? t.inPence*0.01 : "")+","+(t.outPence!=null ? t.outPence*0.01 : ""));
				}
				byclass.close();
			}
		}
		catch (Exception e) {
			System.err.println("Error: "+e);
			e.printStackTrace(System.err);
		}
	}

}
