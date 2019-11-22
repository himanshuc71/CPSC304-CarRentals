package ca.ubc.cs304.database;

import java.sql.*;
import java.util.ArrayList;

import ca.ubc.cs304.model.*;

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
	        ps.setLong(1, customer.getCellphone());
            ps.setString(2, customer.getCname());
            ps.setString(3, customer.getAddress());
            ps.setLong(4, customer.getdLicense());

            ps.executeUpdate();
            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    // insert rental with reservation
    public void insertRental (int confNo, String cardName, int cardNo, String expDate) {
	    try {
	        Statement s = connection.createStatement();
            Statement s1 = connection.createStatement();
	        ResultSet rs = s.executeQuery("SELECT * FROM RESERVATION WHERE CONFNO = " + confNo);
			ReservationModel reservation;
			if (rs.next()) {
				// got reservation
				reservation = new ReservationModel(rs.getInt("confNo"), rs.getString("vtname"),
						rs.getLong("dLicense"), rs.getTimestamp("fromDateTime"), rs.getTimestamp("toDateTime"));
				rs.close();
				// get customer who made the reservation
				rs = s.executeQuery("SELECT * FROM CUSTOMER WHERE DLICENSE = " + reservation.getdLicense());
				rs.next();
				CustomerModel customer = new CustomerModel(rs.getLong("cellphone"), rs.getString("cName"),
						rs.getString("address"), rs.getLong("dlicense"));
				rs.close();
				// rs is the resultSet of all available vehicles
				s.executeQuery("SELECT * FROM VEHICLE WHERE STATUS = 'available' AND VTNAME = " + "'" + reservation.getVtname()+ "'");
                rs = s.getResultSet();
                rs.next();
                Vehicle vehicle = new Vehicle(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getInt(6), rs.getString(7), rs.getString(8),
                        rs.getString(9), rs.getString(10));
				// rs1 is the max rental id for new rentalid creation
				ResultSet rs1 = s1.executeQuery("SELECT MAX(RENTAL.RID) as maxid FROM RENTAL");
				int rentalId;
				if (rs1.next()) {
					rentalId = rs1.getInt("maxid") + 1;
				} else {
					rentalId = 101;
				}
				// make a rental model and then insert into the database
				RentalModel rental = new RentalModel(rentalId, rs.getString("vlicense"),
						reservation.getdLicense(), rs.getInt("odometer"),
						cardName, cardNo, expDate, reservation.getConfNo(),
						reservation.getFromDateTime(), reservation.getToDateTime());

				// inserting into db
				PreparedStatement ps = connection.prepareStatement("INSERT INTO RENTAL VALUES (?,?,?,?,?," +
						"?,?,?,?,?)");
				ps.setInt(1, rental.getRid());
				ps.setString(2, rental.getvLicense());
				ps.setLong(3, rental.getdLicense());
                ps.setTimestamp(4, rental.getFromDateTime());
                ps.setTimestamp(5, rental.getToDateTime());
				ps.setInt(6, rental.getOdometer());
				ps.setString(7, rental.getCardName());
				ps.setLong(8,rental.getCardNo());
				ps.setString(9, rental.getExpDate());
				ps.setInt(10, rental.getConfNo());


				ps.executeUpdate();
                connection.commit();

				ps.close();
				// update that vehicle status to be rented
				s.executeUpdate("UPDATE VEHICLE SET STATUS = 'rented' where VLICENSE = " + "'" + vehicle.getvLicence() + "'");
                connection.commit();
                rs.close();
                rs1.close();
                s.close();
                s1.close();

				System.out.println("Hi, " + customer.getCname() + " your rented car " + reservation.getVtname()
						+ " of license number : " + rental.getvLicense() + " rental ID: " + rentalId + " from " +
						rental.getFromDateTime() + " to " + rental.getToDateTime() + " confirmed.");
			} else {
				System.out.println(WARNING_TAG + "No reservation was found for the confirmation number: "
						+ confNo + " please try again.");
			}
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

	// insert rental W/O reservation
	public void insertRental (String cardName, int cardNo, String expDate) {
		// make a reservation

		// call insert rental with confNo
	}

	public void insertReturn(ReturnModel returnModel){
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO RETURN VALUES (?,?,?,?,?)");
            ps.setInt(1, returnModel.getRid());
            ps.setTimestamp(2, returnModel.getRtnDateTime());
            ps.setInt(3, returnModel.getOdometer());
            ps.setInt(4, returnModel.getFullTank());
            ps.setFloat(5, returnModel.getValue());
            ps.executeUpdate();
            connection.commit();

            ps.close();

            // make that vehicle available
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT RENTAL.VLICENSE FROM RENTAL WHERE RID = "
                    + returnModel.getRid());
            rs.next();
            String vlicense = rs.getString(1);
            rs.close();
            statement.executeQuery("UPDATE VEHICLE SET STATUS = 'available' where VLICENSE = " + "'" + vlicense + "'");
            connection.commit();
            statement.close();
            System.out.println("Vehicle returned with rental id: " + returnModel.getRid() +
                    " license number: " + vlicense);
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public float calcValue (int rid, Timestamp rtnDateTime, int current_odometer) {
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT RENTAL.VLICENSE, RENTAL.FROMDATETIME FROM RENTAL " +
					"WHERE rid = " + rid);
			rs.next();
			String vlicense = rs.getString(1);
			Timestamp fromDateTime = rs.getTimestamp(2);
			rs.close();
			rs = statement.executeQuery("SELECT ODOMETER, VTNAME FROM VEHICLE WHERE " +
					"VLICENSE = " + "'" + vlicense + "'");
			rs.next();
			int kmsCovered = current_odometer - rs.getInt(1);   // reading at return - reading at pick up
			String vtname = rs.getString(2);
			rs.close();
			rs = statement.executeQuery("SELECT * FROM VEHICLETYPE WHERE VTNAME = " + "'" + vtname + "'");
			rs.next();
			VehicleType vehicleType = new VehicleType(vtname, rs.getString(2), rs.getInt(3), rs.getInt(4),
					rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8), rs.getInt(9));

			float numDays = (rtnDateTime.getTime() - fromDateTime.getTime())/(1000*60*60*24);
			int numWeeks = (int) numDays/7;
			float numHours = (rtnDateTime.getTime() - fromDateTime.getTime())/(1000*60*60);

			return numWeeks * vehicleType.getwRate() + numDays * vehicleType.getdRate() + numHours * vehicleType.gethRate() +
					numWeeks * vehicleType.getWiRate() + numDays * vehicleType.getDiRate()
					+ numHours * vehicleType.getHiRate() + kmsCovered * vehicleType.getkRate();

		} catch (SQLException e){
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return 0;
    }

	public boolean checkRentalExists(int rid) {
	    try {
            PreparedStatement ps = connection.prepareStatement("SELECT VLICENSE FROM RENTAL WHERE RID = ?");
            ps.setInt(1, rid);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return false;
            }
            String vlicense = rs.getString(1);
            rs.close();
            Statement statement = connection.createStatement();
            rs = statement.executeQuery("SELECT * FROM VEHICLE WHERE VLICENSE = "
                    + "'" + vlicense + "'" + " AND STATUS = 'rented'");
            if (rs.next()) {
                connection.commit();
                ps.close();
                return true;
            } else {
                connection.commit();
                ps.close();
                return false;
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
	    return false;
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
