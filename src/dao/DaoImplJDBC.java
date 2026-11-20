package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import model.Employee;
import model.Product;
import model.Amount;

public class DaoImplJDBC implements Dao {
	Connection connection;

	@Override
	public void connect() {
		// Define connection parameters
		String url = "jdbc:mysql://localhost:3306/shop";
		String user = "root";
		String pass = "";
		try {
			this.connection = DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		Employee employee = null;
		String query = "select * from employee where employeeId= ? and password = ? ";
		
		try (PreparedStatement ps = connection.prepareStatement(query)) { 
    		ps.setInt(1,employeeId);
    	  	ps.setString(2,password);
    	  	//System.out.println(ps.toString());
            try (ResultSet rs = ps.executeQuery()) {
            	if (rs.next()) {
            		employee = new Employee(rs.getInt(1), rs.getString(2), rs.getString(3));
            	}
            }
        } catch (SQLException e) {
			e.printStackTrace();
		}
    	return employee;
	}

	@Override
	public ArrayList<Product> getInventory() {
	    ArrayList<Product> inventory = new ArrayList<>();
	    String query = "SELECT id, name, wholesalerPrice, available, stock FROM inventory";
	    try (PreparedStatement ps = connection.prepareStatement(query);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	            Product p = new Product(rs.getString("name"),
	                    new Amount(rs.getDouble("wholesalerPrice")),
	                    rs.getBoolean("available"), rs.getInt("stock"));
	            p.setId(rs.getInt("id"));
	            inventory.add(p);
	        }
	    } catch (SQLException e) { e.printStackTrace(); }
	    return inventory;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {
	    String sql = "INSERT INTO historical_inventory (id_product, name, wholesalerPrice, available, stock, created_at) VALUES (?,?,?,?,?,?)";
	    Timestamp now = new Timestamp(System.currentTimeMillis());
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        for (Product p : inventory) {
	            if (p == null) continue;
	            ps.setInt(1, p.getId());
	            ps.setString(2, p.getName());
	            ps.setDouble(3, p.getWholesalerPrice().getValue());
	            ps.setBoolean(4, p.isAvailable());
	            ps.setInt(5, p.getStock());
	            ps.setTimestamp(6, now);
	            ps.addBatch();
	        }
	        ps.executeBatch();
	        return true;
	    } catch (SQLException e) { e.printStackTrace(); return false; }
	}

}
