/**
 * 
 */
package com.app.util;

import java.util.HashMap;
import java.util.Vector;
import javax.naming.NamingException;


/**
 * @author RajeshR
 *
 */
public interface ILDAPMgr {
	public HashMap<String, Vector<String>> authenticateUser(String userName, String userPwd);
	public HashMap<String, Vector<String>> getAllUsers() throws NamingException;
	public HashMap<String, Vector<String>> getUserDetailsByUserID(String userID) throws NamingException;
	public HashMap<String, Vector<String>> getUserDetailsByEmailID(String userEmailID) throws NamingException;
	public HashMap<String, Vector<String>> getUserDetailsByUserName(String userName) throws NamingException;
	public HashMap<String, Vector<String>> getAllGroups() throws NamingException;
	public HashMap<String, Vector<String>> getGroupDetailsByGroupName(String groupName) throws NamingException;
	public HashMap<String, Vector<String>> getGroupsByUserID(String userID) throws NamingException;
	public HashMap<String, Vector<String>> getGroupsByUserName(String userName) throws NamingException;
	public HashMap<String, Vector<String>> getMembersByGroupName(String groupName) throws NamingException;
	public boolean isMemberofGroup(String userID, String groupName) throws NamingException;
}
