package edu.ncsu.csc.itrust.bean;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import edu.ncsu.csc.itrust.DBBuilder;
import edu.ncsu.csc.itrust.action.ReviewsAction;
import edu.ncsu.csc.itrust.action.ZipCodeAction;
import edu.ncsu.csc.itrust.beans.DistanceComparator;
import edu.ncsu.csc.itrust.beans.ObstetricsRecordBean;
import edu.ncsu.csc.itrust.beans.PersonnelBean;
import edu.ncsu.csc.itrust.beans.RatingComparator;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO;
import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.testutils.EvilDAOFactory;
import edu.ncsu.csc.itrust.testutils.TestDAOFactory;

public class DistanceComparatorTest extends TestCase{

	private DAOFactory factory = TestDAOFactory.getTestInstance();
	private TestDataGenerator gen;
	private ZipCodeAction action;
	private ZipCodeAction action2;
	
	@Override
	protected void setUp() throws FileNotFoundException, IOException, SQLException
	{
		gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.standardData();
		action = new ZipCodeAction(factory, 2L);
		action2 = new ZipCodeAction(factory, 3L);
	}
	
	protected void tearDown()
	{
		
	}
	
	public void testComparator()
	{
		List<PersonnelBean> beans = new ArrayList<PersonnelBean>();
		PersonnelDAO dao = new PersonnelDAO(factory);
		List<PersonnelBean> list = new ArrayList<PersonnelBean>();
		try {
			list = dao.getAllPersonnel();
		} catch (DBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try
		{
			if(list.size() != 0)
				Collections.sort(list, new DistanceComparator(action, "27587"));
			
		}
		catch(Exception e)
		{
			fail();
		}
		
	}
	
	public void testComparatorEvil()
	{
		EvilDAOFactory evil = new EvilDAOFactory();
		List<PersonnelBean> beans = new ArrayList<PersonnelBean>();
		PersonnelDAO dao = new PersonnelDAO(factory);
		List<PersonnelBean> list = new ArrayList<PersonnelBean>();
		try {
			list = dao.getAllPersonnel();
		} catch (DBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ZipCodeAction evilAction = new ZipCodeAction(evil, 2);
		try
		{
			DistanceComparator evilCompare = new DistanceComparator(evilAction, "27587");
			assertEquals(-1, evilCompare.compare(list.get(0), list.get(1)));
		}
		catch(Exception e)
		{
			fail();
		}
		
	}
	
	public void testError() {
		DBBuilder builder = new DBBuilder();
		DistanceComparator dist = new DistanceComparator(action2, "27587");
		PersonnelBean bean1 = new PersonnelBean();
		PersonnelBean bean2 = new PersonnelBean();
		try {
			builder.dropTables(); //drop all tables in the DB
			dist.compare(bean1, bean2);
		} catch(Exception e) {
			assertNull(bean1);
		}
		try {
			builder.createTables(); //now put them back so future tests aren't broken
		} catch(Exception e) {
			fail();
		}
	}
}
