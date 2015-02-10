package uncc.goldrush.bean;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;



public class Clinic {
	public Collection<Owner> findOwners(String str){
		return null;
	}
	public void storeOwner(Owner s){
		  PreparedStatement sql = null;
		  try {
			int i = sql.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
