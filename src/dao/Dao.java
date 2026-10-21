package dao;

import java.util.ArrayList;

import model.Employee;
import model.Product;

public interface Dao {
	
	public void connect();

	public void disconnect();

	public Employee getEmployee(int employeeId, String password);

	/**
	 * get the inventory stored in the data source
	 * 
	 * @return list of products
	 */
	public ArrayList<Product> getInventory();

	/**
	 * write the inventory in the data source
	 * 
	 * @param
	 * @return
	 */
	public boolean writeInventory(ArrayList<Product> inventory);
	
}
