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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** Run over a year's transactions dumped (from bank account) and 
 * build up a month-by-month breakdown/profile.
 * 
 * @author cmg
 *
 */
public class CheckBudget {
	static final int WEEKS = 53;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length!=4) {
			System.err.println("Usage: <budget.csv> <statement.csv> <cheques.csv> <allocations.csv>");
			System.err.println("Budget: class,amount");
			System.err.println("Cheques: cheque,date,amount,description,classification");
			System.err.println("Allocations: date,autoclass,autoamount,amount,description,class");
			System.exit(-1);
		}
		try {
			Map<String,Budget> budgets = Budget.readFile(new File(args[0]));
			System.out.println("Read "+budgets.size()+" budget headings");
			Map<String,Cheque> cheques = Cheque.readFile(new File(args[2]));
			System.out.println("Read "+cheques.size()+" cheques");
			Statement s = Statement.readStatement(new File(args[1]), cheques);
			System.out.println("Read "+s.getTransactions().size()+" transactions:");
			List<Allocation> allocations = Allocation.readFile(new File(args[3]));
			System.out.println("Read "+allocations.size()+" allocations");

			Collections.sort(s.getTransactions(), new Transaction.DateComparator());
			Collections.sort(allocations, new Allocation.DateComparator());
			
			//for (Transaction t: s.getTransactions()) 
			//	System.out.println(t.toString());
			{
				System.out.println("Output transactions to check_transactions.csv");
				PrintWriter ctpw = new PrintWriter(new FileWriter("check_transactions.csv"));
				ctpw.println("date,amount,class,description,type");
				Date d = new Date();
				String date = Statement.dateFormat.format(d).replace("/", "_");
				File fout = new File("check_"+date+".csv");
				
				Map<String,Check> checks = new HashMap<String,Check>();
				for (String bn : budgets.keySet()) {
					Check check = new Check();
					check.budgetname = bn;
					checks.put(bn, check);
				}

				int ti = 0;
				int ai = 0;
				if (s.getTransactions().size()==0) {
					System.out.println("No transactions");
					System.exit(-1);
				}
				int year = s.getTransactions().get(0).year;
				System.out.println("Year: "+year);
				List<Transaction> transactions = s.getTransactions();
				for (int wi=0; wi<WEEKS; wi++) {
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.YEAR,year);
					cal.set(Calendar.DAY_OF_YEAR, (wi+1)*7);
					cal.set(Calendar.HOUR_OF_DAY, 23);
					cal.set(Calendar.MINUTE, 59);
					Date endOfWeek = cal.getTime();
					for (; ti<transactions.size(); ti++) {
						Transaction t = transactions.get(ti);
						if (t.date.compareTo(endOfWeek)>0)
							break;
						String clazz = t.classify();
						int ix = clazz.indexOf("/");
						String bn = ix<0 ? clazz : clazz.substring(0,ix);
						Check check = checks.get(bn);
						if (check==null) {
							System.out.println("Creating new check for "+bn);
							check = new Check();
							check.budgetname = bn;
							checks.put(clazz, check);
						}
						int amountPence = 0;
						if (t.inPence!=null)
							amountPence = -t.inPence;
						if (t.outPence!=null)
							amountPence = t.outPence;
						check.add(clazz, wi, amountPence);
						
						//ctpw.println("date,amount,class,description,type");
						ctpw.println(t.day+"/"+t.month+"/"+t.year+","+amountPence*0.01+","+clazz+","+t.description+","+t.type);
					}
					for (; ai<allocations.size(); ai++) {
						Allocation a = allocations.get(ai);
						if (a.date.compareTo(endOfWeek)>0)
							break;
						
						//TODO
					}
				}

				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
				if (cal.get(Calendar.YEAR) > year)
					dayOfYear = 365;
					
				System.out.println("Output budget check to "+fout);
				PrintWriter bytype = new PrintWriter(new FileWriter(fout));
				bytype.print("class,subclass,totaltodate,budget,balance,budgetprorata,varianceprorata,budgetpw");
				for (int wi=0; wi<WEEKS; wi++)
					bytype.print(",week"+wi);
				bytype.println();

				TreeSet<String> checknames = new TreeSet<String>();
				for (String bn : checks.keySet()) {
					checknames.add(bn);
				}
					
				for (String cn : checknames) {
					Check check = checks.get(cn);
					Budget budget = budgets.get(cn);
					int budgetamount = budget!=null ? budget.amountPence : 0;
					int total = check.getTotal();
					bytype.print(cn+",,"+total*0.01+","+(budgetamount*0.01)+","+(budgetamount-total)*0.01/*...*/);
					int budgetprorata = budgetamount*dayOfYear/365;
					bytype.print(","+budgetprorata*0.01+","+(budgetprorata-total)*0.01+","+(budgetamount*7/365)*0.01);
					for (int wi=0; wi<WEEKS; wi++) {
						bytype.print(","+check.totals[wi]*0.01);
					}
					bytype.println();
					Set<String> subclasses = check.getSubclasses();
					if (subclasses.size()>1) {
							for (String sc : subclasses) {
							int subtotal = check.getTotal(sc);
							int sts[] = check.subtotals.get(sc);
							bytype.print(","+sc+","+subtotal*0.01+",,,,,");
							for (int wi=0; wi<WEEKS; wi++) {
								bytype.print(","+sts[wi]*0.01);
							}
							bytype.println();
						}
					}
				}
				
				bytype.close();
				ctpw.close();
			}
		}
		catch (Exception e) {
			System.err.println("Error: "+e);
			e.printStackTrace(System.err);
		}
	}
	static class Check {
		String budgetname;
		int totals[] = new int[WEEKS];
		Map<String,int[]> subtotals = new HashMap<String,int[]>();
		Check() {
			subtotals.put("", new int[WEEKS]);
		}
		TreeSet<String> getSubclasses() {
			TreeSet<String> subclasses = new TreeSet<String>();
			subclasses.addAll(subtotals.keySet());
			return subclasses;
		}
		/**
		 * @param wi
		 * @param amountPence
		 */
		public void add(String clazz, int wi, int amountPence) {
			int ix = clazz.indexOf("/");
			String subclass = ix<0 ? "" : clazz.substring(ix+1);
			int sts [] = subtotals.get(subclass);
			if (sts==null) {
				sts = new int[WEEKS];
				subtotals.put(subclass, sts);
				System.out.println("Adding subclass "+budgetname+" / "+subclass);
			}
			sts[wi] += amountPence;				
			totals[wi] += amountPence;
		}
		int getTotal() {
			int total = 0;
			for (int i=0; i<totals.length; i++)
				total += totals[i];
			return total;
		}
		int getTotal(String subclass) {
			int sts [] = subtotals.get(subclass);
			if (sts==null)
				return 0;
			int total = 0;
			for (int i=0; i<sts.length; i++)
				total += sts[i];
			return total;
		}
	}
}
