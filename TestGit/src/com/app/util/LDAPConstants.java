/**
 * 
 */
package com.app.util;

/**
 * @author RajeshR
 *
 */
public final class LDAPConstants {
	public static final String SEARCH_USERS_BY_USER_ID="uid"; 
	public static final String SEARCH_USERS_BY_USER_Name="givenName";
	public static final String SEARCH_ALL_USERS="CN";
	public static final String SEARCH_USERS_BY_EMAIL_ID="email"; 
	
	public static final String SEARCH_GROUPS_BY_USER_ID="uid"; 
	public static final String SEARCH_GROUPS_BY_USER_NAME="cn"; 
	public static final String SEARCH_GROUPS_BY_GROUP_NAME="cn"; 
	public static final String SEARCH_ALL_GROUPS="cn";
	
	// Object types
	public static final String OBJECT_PERSON="PERSON";
	public static final String OBJECT_GROUP="GROUP";
	public static final String OBJECT_GROUP_MEMBERS="MEMBERS";
	public static final String OBJECT_DEPT="DEPT";
	public static final String OBJECT_APP_CONFIG="CONFIG";
}
