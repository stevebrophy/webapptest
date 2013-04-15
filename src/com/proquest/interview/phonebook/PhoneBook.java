package com.proquest.interview.phonebook;

import java.io.PrintStream;
import java.sql.Connection;

public interface PhoneBook {
	public Person findPerson(String firstName, String lastName);
	public void addPerson(Person newPerson);
	public void printEntries(PrintStream out);
	public void addEntriesToDatabase(Connection conn);
}
