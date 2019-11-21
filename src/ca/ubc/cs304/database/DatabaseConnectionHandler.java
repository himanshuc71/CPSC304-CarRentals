package ca.ubc.cs304.database;

import java.sql.*;
import java.util.ArrayList;

import ca.ubc.cs304.model.BranchModel;
import ca.ubc.cs304.model.CustomerModel;
import ca.ubc.cs304.model.RentalModel;
import ca.ubc.cs304.model.ReservationModel;

/**
 * This class handles all database related transactions
 */
public class DatabaseConnectionHandler {
	private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1522:stu";
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";
	
	private Connection connection = null;
	
	public DatabaseConnectionHandler() {
		try {
			// Load the Oracle JDBC driver
			// Note that the path could change for new drivers
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}
	
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}

//	public void deleteBranch(int branchId) {
//		try {
//			PreparedStatement ps = connection.prepareStatement("DELETE FROM branch WHERE branch_id = ?");
//			ps.setInt(1, branchId);
//
//			int rowCount = ps.executeUpdate();
//			if (rowCount == 0) {
//				System.out.println(WARNING_TAG + " Branch " + branchId + " does not exist!");
//			}
//
//			connection.commit();
//
//			ps.close();
//		} catch (SQLException e) {
//			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
//			rollbackConnection();
//		}
//	}
	
//	public void insertBranch(BranchModel model) {
//		try {
//			PreparedStatement ps = connection.prepareStatement("INSERT INTO branch VALUES (?,?,?,?,?)");
//			ps.setInt(1, model.getId());
//			ps.setString(2, model.getName());
//			ps.setString(3, model.getAddress());
//			ps.setString(4, model.getCity());
//			if (model.getPhoneNumber() == 0) {
//				ps.setNull(5, java.sql.Types.INTEGER);
//			} else {
//				ps.setInt(5, model.getPhoneNumber());
//			}
//
//			ps.executeUpdate();
//			connection.commit();
//
//			ps.close();
//		} catch (SQLException e) {
//			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
//			rollbackConnection();
//		}
//	}
	
//	public BranchModel[] getBranchInfo() {
//		ArrayList<BranchModel> result = new ArrayList<BranchModel>();
//
//		try {
//			Statement stmt = connection.createStatement();
//			ResultSet rs = stmt.executeQuery("SELECT * FROM branch");
//
////    		// get info on ResultSet
////    		ResultSetMetaData rsmd = rs.getMetaData();
////
////    		System.out.println(" ");
////
////    		// display column names;
////    		for (int i = 0; i < rsmd.getColumnCount(); i++) {
////    			// get column name and print it
////    			System.out.printf("%-15s", rsmd.getColumnName(i + 1));
////    		}
//
//			while(rs.next()) {
//				BranchModel model = new BranchModel(rs.getString("branch_addr"),
//													rs.getString("branch_city"),
//													rs.getInt("branch_id"),
//													rs.getString("branch_name"),
//													rs.getInt("branch_phone"));
//				result.add(model);
//			}
//
//			rs.close();
//			stmt.close();
//		} catch (SQLException e) {
//			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
//		}
//
//		return result.toArray(new BranchModel[result.size()]);
//	}

	public void insertCustomer (CustomerModel customer) {
	    try {
	        PreparedStatement ps = connection.prepareStatement("INSERT INTO Customer VALUES (?,?,?,?)");
	        ps.setInt(1, customer.getCellphone());
            ps.setString(2, customer.getCname());
            ps.setString(3, customer.getAddress());
            ps.setInt(4, customer.getdLicense());

            ps.executeUpdate();
            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public void insertRental (int confNo, String cardName, int cardNo, String expDate) {
	    try {
	        Statement s = connection.createStatement();
	        ResultSet rs = s.executeQuery("SELECT * FROM RESERVATION WHERE CONFNO = " + confNo);
			ReservationModel reservation;
			if (rs.next()) {
				// got reservation
				reservation = new ReservationModel(rs.getInt("confNo"), rs.getString("vtname"),
						rs.getInt("dLicense"), rs.getTimestamp("fromDateTime"), rs.getTimestamp("toDateTime"));
				rs.close();
				// get customer who made the reservation
				rs = s.executeQuery("SELECT * FROM CUSTOMER WHERE DLICENSE = " + reservation.getdLicense());
				rs.next();
				CustomerModel customer = new CustomerModel(rs.getInt("cellphone"), rs.getString("cName"),
						rs.getString("address"), rs.getInt("dlicense"));
				rs.close();
				// rs is the resultSet of all available vehicles
				rs = s.executeQuery("SELECT * FROM VEHICLE WHERE STATUS = 'available' AND VTNAME = " + reservation.getVtname());
				rs.next();
				// rs1 is the max rental id for new rentalid creation
				ResultSet rs1 = s.executeQuery("SELECT MAX(RENTAL.RID) FROM RENTAL");
				int rentalId;
				if (rs1.next()) {
					rentalId = rs1.getInt("rid") + 1;
				} else {
					rentalId = 101;
				}
				// make a rental model and then insert into the database
				RentalModel rental = new RentalModel(rentalId, rs.getString("vlicense"),
						reservation.getdLicense(), rs.getInt("odometer"),
						cardName, cardNo, expDate, reservation.getConfNo(),
						reservation.getFromDateTime(), reservation.getToDateTime());
				rs.close();
				rs1.close();
				s.close();

				// inserting into db
				PreparedStatement ps = connection.prepareStatement("INSERT INTO RENTAL VALUES (?,?,?,?,?," +
						"?,?,?,?,?)");
				ps.setInt(1, rental.getRid());
				ps.setString(2, rental.getvLicense());
				ps.setInt(3, rental.getdLicense());
				ps.setInt(4, rental.getOdometer());
				ps.setString(5, rental.getCardName());
				ps.setInt(6,rental.getCardNo());
				ps.setString(7, rental.getExpDate());
				ps.setInt(8, rental.getConfNo());
				ps.setTimestamp(9, rental.getFromDateTime());
				ps.setTimestamp(10, rental.getToDateTime());

				ps.executeUpdate();
				connection.commit();

				ps.close();
				System.out.println("Hi, " + customer.getCname() + " your rented car " + reservation.getVtname()
						+ " of license number : " + rental.getvLicense() + " from " +
						rental.getFromDateTime() + " to " + rental.getToDateTime() + " confirmed.");
			} else {
				System.out.println("No reservation was found for the confirmation number: "
						+ confNo + "please try again.");
			}
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }
	
//	public void updateBranch(int id, String name) {
//		try {
//		  PreparedStatement ps = connection.prepareStatement("UPDATE branch SET branch_name = ? WHERE branch_id = ?");
//		  ps.setString(1, name);
//		  ps.setInt(2, id);
//
//		  int rowCount = ps.executeUpdate();
//		  if (rowCount == 0) {
//		      System.out.println(WARNING_TAG + " Branch " + id + " does not exist!");
//		  }
//
//		  connection.commit();
//
//		  ps.close();
//		} catch (SQLException e) {
//			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
//			rollbackConnection();
//		}
//	}
	
	public boolean login(String username, String password) {
		try {
			if (connection != null) {
				connection.close();
			}
	
			connection = DriverManager.getConnection(ORACLE_URL, "ora_chotwani", "a39315163");
			connection.setAutoCommit(false);
	
			System.out.println("\nConnected to Oracle!");
			return true;
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			return false;
		}
	}

	private void rollbackConnection() {
		try  {
			connection.rollback();	
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}
}
