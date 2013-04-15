package com.proquest.interview.phonebook;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.proquest.interview.util.DatabaseUtil;

/**
 * An implemention of the Phone Book interface.
 * <p>
 * This implementation restricts the phone book entries to be Person objects with unique names.
 * The names must not change within a Person object.
 * @author BrophySM
 *
 */
public class PhoneBookImpl implements PhoneBook {
	//public List people;
	
	// Store phonebook entries in map with key = Person.name
	//   Names cannot be changed on an entry,
	//   the old entry must be removed and a new entry added.
	private final Map<String,Person> mPeople;
	
	
	/** Creates an empty phone book. */
	public PhoneBookImpl() {
		mPeople = new HashMap<String,Person>();
	}
	
	
	/**
	 * Adds an entry for a Person object into the phone book.
	 * <p>
	 * To be added, there must be no existing entry for the same person.
	 * If the new entry is a duplicate, no change is made to the phonebook.
	 * 
	 * @param newPerson is the person object to be added to the phone book.
	 */
	@Override
	public void addPerson(Person newPerson) {
		if (newPerson == null) {
			System.err.println("Cannot add a 'null' person to the phone book.");
			return;
		}
		if (mPeople.containsKey(newPerson.getName())) {
			System.err.println("Cannot add Person '" + newPerson.getName()
					+ " to the phone book, entry already exists.");
			return;
		}
		mPeople.put(newPerson.getName(), newPerson);
	}
	

	
	/**
	 * Find the specified name in the phone book and return that Person object.
	 * <p>
	 * Phone book entries are identified by full name,
	 * this method will combine the first name and the
	 * last name to produce the full name to be matched.
	 * 
	 * @param firstname is the first name of the person to find
	 * @param lastname is the last name of the person to find
	 * @return the matching Person record or a null if no match was found.
	 */
	@Override
	public Person findPerson(String firstname, String lastname) {
		// build name by combining components with spaces.
		StringBuilder name = new StringBuilder();
		if (firstname != null && (! firstname.isEmpty())) {
			name.append(firstname);
		}
		if (lastname != null && (! lastname.isEmpty())) {
			if (name.length() > 0) {
				name.append(" ");
			}
			name.append(lastname);
		}
		if (name.length() == 0) {
			return null;
		}
		return mPeople.get(name.toString());
	}
	
	
	
	/**
	 * Print out all entries in the phonebook to the specified output.
	 * <p>
	 * Entries will print in order sorted alphabetically by full name.
	 * 
	 * @param out is the PrintStream to print the entries to.
	 */
	public void printEntries(PrintStream out) {
		List<String> orderednames = new ArrayList<String>(mPeople.keySet());
		Collections.sort(orderednames);
		out.println("Phone Book Entries (" + orderednames.size() + "):");
		for (String name : orderednames) {
			Person p = mPeople.get(name);
			out.println("  " + p);
		}
	}
	
	
	/**
	 * Add all phone book entries into the specified database.
	 * <p>
	 * If the entry is already in the database, it will not be changed in the database.
	 * 
	 * @param conn is the connection to the database to add the entries into,
	 *      the database is expected to have a PHONEBOOK table with the correct schema.
	 */
	@Override
	public void addEntriesToDatabase(Connection conn) {
		// TODO: should process query parameters to escape or replace special chars like quotes
		// TODO: could check the connection AutoCommit status, put all these statements into a single transaction
		// TODO: could use prepared statements for the repeated 'select' and 'insert' queries.
		
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			for (Person p : mPeople.values()) {
				ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM PHONEBOOK WHERE NAME = '"
						+ p.getName() + "'");
				if (rs.next()) {
					int count = rs.getInt(1);
					if (count == 0) {
						// No existing entry yet for this person, add it.
						stmt.executeUpdate("INSERT INTO PHONEBOOK (NAME, PHONENUMBER, ADDRESS)"
								+ " VALUES('" + p.getName() + "','" + p.getPhoneNumber()
								+ "','" + p.getAddress() + "')");
					}
					else {
						System.out.println("Database already contains a phonebook entry for: " + p.getName());
					}
				}
				rs.close();
			}
		}
		catch (SQLException ex) {
			System.err.println("Got exception while adding phonebook entries to the database: " + ex.getMessage());
			ex.printStackTrace();
		}
		finally {
			if (stmt != null) {
				try {stmt.close();}
				catch (Exception ex) {System.err.println("exception on closing statement: " + ex.getMessage());}
			}
		}
		
	}
	
	
	public static void main(String[] args) throws Exception {
		DatabaseUtil.initDB();  //You should not remove this line, it creates the in-memory database

		/* TASK 1: create person objects and put them in the PhoneBook and database
		 * John Smith, (248) 123-4567, 1234 Sand Hill Dr, Royal Oak, MI
		 * Cynthia Smith, (824) 128-8758, 875 Main St, Ann Arbor, MI
		*/ 
		Person person1 = new Person("John Smith", "(248) 123-4567", "1234 Sand Hill Dr, Royal Oak, MI");
		Person person2 = new Person("Cynthia Smith", "(824) 128-8758", "875 Main St, Ann Arbor, MI");
		PhoneBook book = new PhoneBookImpl();
		book.addPerson(person1);
		book.addPerson(person2);
		// inserting persons into database delayed until task 4, we will add all entries in the book at once
		
		// TASK 2: print the phone book out to System.out
		book.printEntries(System.out);
		
		// TASK 3: find Cynthia Smith and print out just her entry
		Person found = book.findPerson("Cynthia", "Smith");
		System.out.println("Entry for Cynthia Smith:");
		System.out.println("  " + found);
		
		// TASK 4: insert the new person objects into the database
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection();
			book.addEntriesToDatabase(conn);
		} catch (Exception ex) {
			System.err.println("Could not add phonebook entries to the database, got exception: " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			if (conn != null) {
				try { 
					conn.close();
				} catch (Exception ex) {
					System.err.println("Got exception on closing the connection: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}
}
