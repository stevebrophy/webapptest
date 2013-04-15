package com.proquest.interview.phonebook;

/**
 * Holds contact information for a person.
 * <p>
 * To uniquely identify a Person object, we will use the 'name'.
 * <li> The 'name' field will be immutable
 * <li> The 'name' field is required in the constructor.
 * <li> The 'name' field must not be null or empty.
 * <p>
 * The other fields can be set in a constructor or with a 'setter' method,
 *    and may be null.
 *    
 * @author BrophySM
 *
 */
public class Person {
	private final String mName;
	private String mPhoneNumber;
	private String mAddress;
	
	/**
	 * Constructor that sets the name.
	 * 
	 * @param name is the person's full name, used as a unique identifer, immutable, and not null.
	 */
	public Person(String name) throws Exception {
		this(name, null, null);
	}
	
	/**
	 * Constructor that sets name and initial phone number and address.
	 * 
	 * @param name is the full name unique identifier for a person, required, not null.
	 * @param the phone number for the person, may be null.
	 * @param the address for the person, may be null.
	 */
	public Person(String name, String phoneNumber, String address) throws Exception {
		if (name == null || name.isEmpty()) {
			System.err.println("The 'name' is required for a Person object.");
			throw new Exception("Could not create Person object, the 'name' is required.");
		}
		this.mName = name;
		this.mPhoneNumber = phoneNumber;
		this.mAddress = address;
	}
	
	public String getName() {
		return mName;
	}
	
	public String getPhoneNumber() {
		return mPhoneNumber;
	}
	
	public String getAddress() {
		return mAddress;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		mPhoneNumber = phoneNumber;
	}
	
	public void setAddress(String address) {
		mAddress = address;
	}
	
	/** For default string representation, combine all components on one line. */
	@Override
	public String toString() {
		StringBuilder entry = new StringBuilder();
		// build name by combining components with spaces.
		entry.append(mName);
		entry.append(": phone = ");
		entry.append(mPhoneNumber);
		entry.append("; address = ");
		entry.append(mAddress);
		return entry.toString();
	}
}
