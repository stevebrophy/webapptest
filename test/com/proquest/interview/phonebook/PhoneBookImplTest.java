package com.proquest.interview.phonebook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;

import com.proquest.interview.util.DatabaseUtil;

public class PhoneBookImplTest {
	
	/** Test adding new entries to the phone book, and retrieving existing entries. */
	@Test
	public void testAddFindPerson() throws Exception {
		PhoneBook book = new PhoneBookImpl();
		
		Person p1notfound = book.findPerson("John", "Smith");
		assertNull(p1notfound);
		
		Person person1 = new Person("John Smith", "(248) 123-4567", "1234 Sand Hill Dr, Royal Oak, MI");
		book.addPerson(person1);
		
		Person p1lookup = book.findPerson("John", "Smith");
		assertNotNull(p1lookup);
		assertEquals(person1.getName(), p1lookup.getName());
		assertEquals(person1.getPhoneNumber(), p1lookup.getPhoneNumber());
		assertEquals(person1.getAddress(), p1lookup.getAddress());
	}
	
	/** Test that the database has been modified after adding new phonebook entries */
	@Test
	public void testCheckDatabaseIsChanged() throws Exception {
		DatabaseUtil.initDB();
		Connection conn = DatabaseUtil.getConnection();
		try {
			int initialCount = getCountOfPhoneBookRecords(conn);
			PhoneBook book = new PhoneBookImpl();
			book.addPerson(new Person("John Smith"));
			book.addPerson(new Person("Cynthia Smith"));
			book.addEntriesToDatabase(conn);
			int updatedCount = getCountOfPhoneBookRecords(conn);
			assertEquals(initialCount + 2, updatedCount);
		} catch (Exception ex) {
			System.err.println("Got error in testCheckDatabaseIsChanged: " + ex.getMessage());
			throw ex;
		} finally {
			conn.close();
		}
	}
	
	/** Get count of the records in the PHONEBOOK table. */
	private int getCountOfPhoneBookRecords(Connection conn) throws Exception {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM PHONEBOOK");
		int count = 0;
		if (rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		stmt.close();
		return count;
	}
}
