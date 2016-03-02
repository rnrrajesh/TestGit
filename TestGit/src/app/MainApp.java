package app;



import com.app.util.LDAPMgr;

public class MainApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LDAPMgr ldapMgr = new LDAPMgr();

	
		try {

			//dirCntxt = ldapMgr.getLdapDirContext();
			//ldapMgr.getAllUsers();
			//ldapMgr.getUserDetailsByUserID("kmahesh");
			ldapMgr.getUserDetailsByUserName("R");
			//ldapMgr.getPersonsByDept(dirCntxt, "sales");
			
			//ldapMgr.authenticateUser("rrajesh", "raj1234");
			
			//ldapMgr.getGroupDetailsByGroupName("s");
			//ldapMgr.getAllGroups();
			//ldapMgr.getMembersByGroupName("sales");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
