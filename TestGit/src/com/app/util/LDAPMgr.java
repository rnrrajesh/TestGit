/**
 * 
 */
package com.app.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;


/**
 * @author RajeshR
 * 
 */ 
public class LDAPMgr implements ILDAPMgr {
	/**
	 * 
	 * @return
	 */
	private static String getLdapServerName() {
		String ldapServer = "ldap://localhost:10389/";
		return ldapServer;
	}
	
	/**
	 * 
	 * @return
	 */
	private static String getDirBaseforSearch() {
		String base = "dc=example,dc=com";
		return base;
	}
	
	/**
	 * 
	 * @param server
	 * @return
	 * @throws CommunicationException
	 */
	
	private DirContext getLdapDirContext(String userID, String userPwd) throws CommunicationException, AuthenticationException {
		DirContext dirCtxt = null;
		
		// Create a hash table and populate it with LDAP service/directory connection details
		Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, getLdapServerName());
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        
        if (userID != null && userPwd != null) {
        	env.put(Context.SECURITY_PRINCIPAL, userID);
        	env.put(Context.SECURITY_CREDENTIALS, userPwd);
        }
        // Get the directory context for a valid LDAP service by biding anonymously
        try {
			dirCtxt = new InitialDirContext(env);
        } catch (AuthenticationException authEx) {
        	authEx.printStackTrace();
        } catch (CommunicationException commEx) {
        	commEx.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return dirCtxt;
	}
	
	/**
	 * Method to authenticate any user present in the Directory server. 
	 * If successful, returns the user details
	 * @param dirCntxt
	 * @return
	 */
	public HashMap<String, Vector<String>> authenticateUser(String userName, String userPwd) {
		HashMap<String, Vector<String>> validUser = null;
		String userDN="";
		boolean isAuthenticated = false;
		DirContext dirCntxt = null;
		
		try {
	        // Bind with the admin credentials for validating the directory connection
	        dirCntxt = getLdapDirContext(null, null);
	        
			// Search the directory to get the folly qualified name of the user
            String base = getDirBaseforSearch();
            String filter = "(&(objectClass=inetOrgPerson)(uid={0}))";           
            SearchControls ctls = new SearchControls();
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            ctls.setReturningAttributes(new String[0]);
            ctls.setReturningObjFlag(true);
            NamingEnumeration<SearchResult> enm = dirCntxt.search(base, filter, new String[] { userName }, ctls);
            
            
			if (enm.hasMore()) {
                SearchResult result = (SearchResult) enm.next();
                userDN =  result.getNameInNamespace();
                System.out.println("attributes: " + result.getAttributes().toString());

                System.out.println("dn: "+ userDN);
                isAuthenticated = true;
            } else {
            	System.out.println("No items found");
            }

            if (userDN == null || enm.hasMore()) {
                // uid not found or not unique
            	isAuthenticated = false;
                throw new NamingException("Authentication failed");
            }
            enm.close();
		} catch (NamingException ldapNe) {
			ldapNe.printStackTrace();
		}  
         
		// Checking for authentication
		try {
		// Bind with found DN and given password
				if(isAuthenticated && userDN != null && userPwd != null) {
					dirCntxt = getLdapDirContext(userDN, userPwd);
					
					if (dirCntxt != null) {
			            // Perform a lookup in order to force a bind operation with JNDI
			            validUser = new HashMap<String, Vector<String>>();
			            Vector<String> userDetail = new Vector<String>(1);
			            userDetail.addElement(userDN);
			            validUser.put(userName, userDetail);
			            isAuthenticated = true;
					}
	            }
		} catch (AuthenticationException authEx) {
			isAuthenticated = false;
		} catch (NamingException ldapNe) {
			ldapNe.printStackTrace();
		}
        
		System.out.println("Authenticated? " + isAuthenticated);
		
		return validUser;
	}
	
	/**
	 * Method to return all users present in the Directory
	 * @return
	 * @throws NamingException
	 */
	public HashMap<String, Vector<String>> getAllUsers() throws NamingException {
		HashMap<String, Vector<String>> personMap = null;
		personMap = getDetails(LDAPConstants.OBJECT_PERSON, LDAPConstants.SEARCH_ALL_USERS, "*");
		return personMap;
	}
	
	/**
	 * Method to return the details of all users that match the specified criteria - user ID
	 * @param userID
	 * @return
	 * @throws NamingException
	 */
	public HashMap<String, Vector<String>> getUserDetailsByUserID(String userID) throws NamingException {
		HashMap<String, Vector<String>> personMap = null;
		personMap = getDetails(LDAPConstants.OBJECT_PERSON, LDAPConstants.SEARCH_GROUPS_BY_USER_ID, userID);
		return personMap;
	}
	
	/**
	 * Method to return the details of all users that match the specified criteria - user Name
	 * @param userName
	 * @return
	 * @throws NamingException
	 */
	public HashMap<String, Vector<String>> getUserDetailsByUserName(String userName) throws NamingException {
		HashMap<String, Vector<String>> personMap = null;
		personMap = getDetails(LDAPConstants.OBJECT_PERSON, LDAPConstants.SEARCH_GROUPS_BY_USER_NAME, "*" + userName + "*");
		return personMap;
	}
	
	/**
	 * Method that returns all groups and their respective attributes present in the Directory server
	 * @return
	 * @throws NamingException
	 */
	public HashMap<String, Vector<String>> getAllGroups() throws NamingException {
		HashMap<String, Vector<String>> personMap = null;
		personMap = getDetails(LDAPConstants.OBJECT_GROUP, LDAPConstants.SEARCH_ALL_GROUPS, "*");
		return personMap;
	}
	
	/**
	 * Method that returns all groups and their respective attributes for the matching criteria - groupName
	 * @param groupName
	 * @return
	 * @throws NamingException
	 */
	public HashMap<String, Vector<String>> getGroupDetailsByGroupName(String groupName) throws NamingException {
		HashMap<String, Vector<String>> personMap = null;
		personMap = getDetails(LDAPConstants.OBJECT_GROUP, LDAPConstants.SEARCH_GROUPS_BY_GROUP_NAME, "*" + groupName + "*");
		return personMap;
	}
	
	public HashMap<String, Vector<String>> getMembersByGroupName(String groupName) throws NamingException {
		HashMap<String, Vector<String>> personMap = null;
		personMap = getDetails(LDAPConstants.OBJECT_GROUP_MEMBERS, LDAPConstants.SEARCH_GROUPS_BY_GROUP_NAME, groupName);
		return personMap;
	}
	
	public boolean isMemberofGroup(String userID, String groupName) {
		boolean isMember = false;
		
		return isMember;
	}
	/**
	 * Generic method that returns various details from Directory Server based on specified criteria
	 * @param objectType
	 * @param ldapUserSearchTokenType
	 * @param searchToken
	 * @return
	 * @throws NamingException
	 */
	public HashMap<String, Vector<String>> getDetails(String objectType, String ldapUserSearchTokenType, String searchToken) throws NamingException {
		String dn="";
		HashMap<String, Vector<String>> personMap = new HashMap<String, Vector<String>>();
		Vector<String> personAttr = null;
		SearchResult result = null;
		String attr = "";
		NamingEnumeration<SearchResult> enm = null;
		NamingEnumeration<? extends Attribute> attrEnm = null;

		
		try {
			DirContext dirCntxt = getLdapDirContext(null, null);
			
			// Search the directory
            String base = getDirBaseforSearch();
            SearchControls ctls = new SearchControls();
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            ctls.setReturningAttributes(new String[0]);
            ctls.setReturningObjFlag(true);
            
            switch (objectType) {
	            case LDAPConstants.OBJECT_PERSON:
	            	enm = dirCntxt.search(base, "(&(objectClass=inetOrgPerson)(" + ldapUserSearchTokenType + "=" + searchToken + "))", ctls);
	            	break;
	            case LDAPConstants.OBJECT_GROUP:
	            	enm = dirCntxt.search(base, "(&(objectClass=groupOfNames)(" + ldapUserSearchTokenType + "=" + searchToken + "))", ctls);
	            	break;
	            case LDAPConstants.OBJECT_GROUP_MEMBERS:
	            	String[] returnAttr = { "member", "memberOf"};
	            	ctls.setReturningAttributes(returnAttr);
	            	enm = dirCntxt.search(base, "(&(objectClass=groupOfNames)(" + ldapUserSearchTokenType + "=" + searchToken + "))", ctls);
	            	personMap = getAttributeDetails(dirCntxt, enm);
	            	break;
	            default:
	            	enm = null;
            }

            // Loop through the search results (persons) that match the criteria to retrieve corresponding attributes
            if (enm != null) {
				while (enm.hasMore()) {
	            	result = (SearchResult) enm.next();
	                dn = result.getNameInNamespace();
	            	attrEnm = dirCntxt.getAttributes(dn).getAll();
	                if (attrEnm != null) {
	                	personAttr = new Vector<String>(1, 1);
	                	System.out.println("DN: " + dn);
	                	while (attrEnm.hasMore()) { 
	                		attr = attrEnm.next().toString();
			                personAttr.addElement(attr);
			                System.out.println("attribute: " + attr);
	                	}
	                	personMap.put(dn, personAttr);
	                	attrEnm.close();
	                }
	            }
	            //personMap = getAttributeDetails(dirCntxt, enm);
	            enm.close(); 
            }

		} catch (NamingException ldapNe) {
			ldapNe.printStackTrace();
		} 
		return personMap;
	
	}
	
	private HashMap<String, Vector<String>> getAttributeDetails(DirContext dirCntxt, NamingEnumeration<SearchResult> enm) {
		HashMap<String, Vector<String>> personMap = new HashMap<String, Vector<String>>();
		String dn = "";
		NamingEnumeration<? extends Attribute> attrEnm = null;
		SearchResult result = null;
		Vector<String> personAttr = null;
		Attribute attr = null;
		
		// Loop through the search results (persons) that match the criteria to retrieve corresponding attributes
		try {
			while (enm.hasMore()) {
	        	result = (SearchResult) enm.next();
	            dn = result.getNameInNamespace();
	        	attrEnm = dirCntxt.getAttributes(dn).getAll();
	            if (attrEnm != null) {
	            	personAttr = new Vector<String>(1, 1);
	            	System.out.println("DN: " + dn);
	            	while (attrEnm.hasMore()) { 
	            		attr = (Attribute)attrEnm.next();
	            		Enumeration<?> vals = attr.getAll();
		                personAttr.addElement(attr.getID());
		                
		                System.out.print(attr.getID() + ", ---");
		                while (vals.hasMoreElements()) {
		                	String username = (String)vals.nextElement();
		                	System.out.println("attribute: " + username);
		                }
		                
	            	}
	            	personMap.put(dn, personAttr);
	            	attrEnm.close();
	            }
	        }
	        enm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return personMap;
	}


	@Override
	public HashMap<String, Vector<String>> getUserDetailsByEmailID(
			String userEmailID) throws NamingException {
		
		return null;
	}

	@Override
	public HashMap<String, Vector<String>> getGroupsByUserID(String userID) {
		
		return null;
	}

	@Override
	public HashMap<String, Vector<String>> getGroupsByUserName(String userName) {
		
		return null;
	}
}
	

