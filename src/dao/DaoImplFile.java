package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;

import model.Amount;
import model.Employee;
import model.Product;

public class DaoImplFile implements Dao {

	@Override
	public void connect() {
	}

	@Override
	public void disconnect() {
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		return null;
	}

	/**
	 * read inventory from file files/inputInventory.txt
	 * 
	 * @return list of products read from the file
	 */
	@Override
	public ArrayList<Product> getInventory() {
		ArrayList<Product> inventory = new ArrayList<Product>();

		// locate file, path and name
		File f = new File(System.getProperty("user.dir") + File.separator + "files" + File.separator
				+ "inputInventory.txt");

		try (BufferedReader br = new BufferedReader(new FileReader(f))) {

			String line = br.readLine();

			while (line != null) {
				if (line.startsWith("Product:")) {
					String[] sections = line.split(";");

					String name = "";
					double wholesalerPrice = 0.0;
					int stock = 0;

					for (int i = 0; i < sections.length; i++) {
						String[] data = sections[i].split(":");

						switch (i) {
						case 0:
							name = data[1];
							break;

						case 1:
							wholesalerPrice = Double.parseDouble(data[1]);
							break;

						case 2:
							stock = Integer.parseInt(data[1]);
							break;

						default:
							break;
						}
					}
					inventory.add(new Product(name, new Amount(wholesalerPrice), true, stock));
				}

				line = br.readLine();
			}

		} catch (IOException | RuntimeException e) {
			e.printStackTrace();
		}
		return inventory;
	}

	/**
	 * write inventory in file files/inventory_yyyy-mm-dd.txt
	 * 
	 * @param inventory list of products
	 * @return true if processed correctly, false if there was any problem
	 */
	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {
		// define file name based on date
		String fileName = "inventory_" + LocalDate.now().toString() + ".txt";

		File folder = new File(System.getProperty("user.dir") + File.separator + "files");
		if (!folder.exists() && !folder.mkdirs()) {
			return false;
		}
		File f = new File(folder, fileName);

		try (PrintWriter pw = new PrintWriter(new FileWriter(f, false))) {
			int total = 0;
			for (Product product : inventory) {
				if (product != null) {
					total++;
					pw.println(total + ";Product:" + product.getName() + ";Stock:" + product.getStock() + ";");
				}
			}
			pw.println("Total number of products:" + total + ";");

			return !pw.checkError();

		} catch (IOException | RuntimeException e) {
			// if any exception is captured, return false
			e.printStackTrace();
			return false;
		}
	}

}
