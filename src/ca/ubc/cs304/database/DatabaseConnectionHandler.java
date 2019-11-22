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

	public void insertCustomer (CustomerModel customer) {
	    try {
	    	Statement stmt = connection.createStatement();
	    	String sql = String.format("INSERT INTO Customer VALUES (%d,%d,\'%s\',\'s\')", customer.getdLicense(), customer.getCellphone(), customer.getCname(), customer.getAddress());
	       stmt.executeUpdate(sql);

	       connection.commit();

        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

	public boolean customerExists(long licence) {
		boolean exists = false;
		int count = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM Customer WHERE dLicense = ?");
			ps.setLong(1, licence);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
			}
			connection.commit();
			ps.close();
			if (count == 1) {
				exists = true;
			} else if (count == 0) {
				exists = false;
			}
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			exists = false;
		}
		return exists;
	}

	public CustomerModel getCustomer(long licence) {
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM Customer WHERE dLicense = ?");
			ps.setLong(1, licence);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return new CustomerModel(rs.getLong(2), rs.getString(3), rs.getString(4), licence);
			}
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			return null;
		}
		return null;
	}

	public boolean branchExists(String location, String city) {
		boolean exists = false;
		int count = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM Branch WHERE location = ? " +
					"AND city = ?");
			ps.setString(1, location);
			ps.setString(2, city);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
			}
			connection.commit();
			ps.close();
			if (count == 1) {
				exists = true;
			} else if (count == 0) {
				exists = false;
			}
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			exists = false;
		}
		return exists;
	}

	public boolean vehicleTypeExists(String vtname) {
		boolean exists = false;
		int count = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM VehicleType WHERE " +
					"vtname = ? ");
			ps.setString(1, vtname);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
			}
			connection.commit();
			ps.close();
			if (count >= 1) {
				exists = true;
			} else if (count == 0) {
				exists = false;
			}
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			exists = false;
		}
		return exists;
	}

	public String getNameFromLicence(long dLicence) {
		String name = null;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT cname FROM Customer WHERE dLicense = ?");
			ps.setLong(1, dLicence);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				name = rs.getString(1);
			}
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return name;
	}

	public int numberVehiclesAvailable(String location, String vtname, Timestamp fromDate, Timestamp toDate) {
		if (location == null && vtname == null && (fromDate != null && toDate != null)) {
			return numberVehiclesAvailableDates(fromDate, toDate);
		} else if (location != null && vtname == null && (fromDate == null || toDate == null)) {
			return numberVehiclesAvailableLocation(location);
		} else if (location == null && vtname != null && (fromDate == null || toDate == null)) {
			return numberVehiclesAvailableVTname(vtname);
		} else if (location != null && vtname != null && (fromDate == null || toDate == null)) {
			return numberVehiclesAvailableLocationVTname(location, vtname);
		} else if (location != null && vtname == null) {
			return numberVehiclesAvailableLocationDates(location, fromDate, toDate);
		} else if (location == null && vtname != null) {
			return numberVehiclesAvailableVTnameDates(vtname, fromDate, toDate);
		} else {
			int numberOfRows = 0;
			try {
				// need to have different cases if any of the inputs are blank
				// using available status
				// check the rental table, reservation, vehicle and vehicle type
				PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM (SELECT v.vlicense " +
						"FROM Vehicle v WHERE v.status = 'available' AND v.location = ? AND v.vtname = ? " +
						"MINUS " +
						"SELECT r.vlicense " +
						"FROM Rental r " +
						"WHERE r.fromDateTime BETWEEN ? and ? " +
						"OR r.toDateTime BETWEEN ? and ?)");
				ps.setString(1, location);
				ps.setString(2, vtname);
				ps.setTimestamp(3, fromDate);
				ps.setTimestamp(4, toDate);
				ps.setTimestamp(5, fromDate);
				ps.setTimestamp(6, toDate);

				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					numberOfRows = rs.getInt(1);
				}
				connection.commit();
				ps.close();
			} catch (SQLException e) {
				System.out.println(EXCEPTION_TAG + " " + e.getMessage());
				rollbackConnection();
			}
			return numberOfRows;
		}
	}

	private int numberVehiclesAvailableLocation(String location) {
		int numberOfRows = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM Vehicle v WHERE location = ? AND status = 'available'");
			ps.setString(1, location);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				numberOfRows = rs.getInt(1);
			}
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return numberOfRows;
	}

	private int numberVehiclesAvailableVTname(String vtname) {
		int numberOfRows = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM Vehicle v WHERE vtname = ? AND status = 'available'");
			ps.setString(1, vtname);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				numberOfRows = rs.getInt(1);
			}
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return numberOfRows;
	}

	private int numberVehiclesAvailableLocationVTname(String location, String vtname) {
		int numberOfRows = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM Vehicle v WHERE location = ? AND vtname = ? AND status = 'available'");
			ps.setString(1, location);
			ps.setString(2, vtname);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				numberOfRows = rs.getInt(1);
			}
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return numberOfRows;
	}

	private int numberVehiclesAvailableDates(Timestamp fromDate, Timestamp toDate) {
		int numberOfRows = 0;
		try {
			String getLicenceRental = "SELECT COUNT(*) FROM (SELECT v.vlicense FROM Vehicle v WHERE v.status = 'available' " +
					"MINUS SELECT r.vlicense FROM Rental r WHERE r.fromDateTime " +
					"BETWEEN ? and ? OR r.toDateTime " +
					"BETWEEN ? and ?)";
			PreparedStatement ps = connection.prepareStatement(getLicenceRental);
			ps.setTimestamp(1, fromDate);
			ps.setTimestamp(2, toDate);
			ps.setTimestamp(3, fromDate);
			ps.setTimestamp(4, toDate);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				numberOfRows = rs.getInt(1);
			}
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return numberOfRows;
	}

	private int numberVehiclesAvailableLocationDates(String location, Timestamp fromDate, Timestamp toDate) {
		int numberOfRows = 0;
		try {
			String getLicenceRental = "SELECT COUNT(*) FROM (SELECT v.vlicense FROM Vehicle v WHERE v.status = 'available' AND v.location = ? " +
					"MINUS SELECT r.vlicense FROM Rental r WHERE r.fromDateTime " +
					"BETWEEN ? and ? OR r.toDateTime " +
					"BETWEEN ? and ?)";
			PreparedStatement ps = connection.prepareStatement(getLicenceRental);
			ps.setString(1, location);
			ps.setTimestamp(2, fromDate);
			ps.setTimestamp(3, toDate);
			ps.setTimestamp(4, fromDate);
			ps.setTimestamp(5, toDate);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				numberOfRows = rs.getInt(1);
			}
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return numberOfRows;
	}

	private int numberVehiclesAvailableVTnameDates(String vtname, Timestamp fromDate, Timestamp toDate) {
		int numberOfRows = 0;
		try {
			String getLicenceRental = "SELECT COUNT(*) FROM (SELECT v.vlicense FROM Vehicle v WHERE v.status = 'available' AND v.vtname = ? " +
					"MINUS SELECT r.vlicense FROM Rental r WHERE r.fromDateTime " +
					"BETWEEN ? and ? OR r.toDateTime " +
					"BETWEEN ? and ?)";
			PreparedStatement ps = connection.prepareStatement(getLicenceRental);
			ps.setString(1, vtname);
			ps.setTimestamp(2, fromDate);
			ps.setTimestamp(3, toDate);
			ps.setTimestamp(4, fromDate);
			ps.setTimestamp(5, toDate);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				numberOfRows = rs.getInt(1);
			}
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return numberOfRows;
	}

	public void makeReservation(long dLicence, String vtname, Timestamp fromDate, Timestamp toDate) {
		try {
			Statement s = connection.createStatement();
			Statement s1 = connection.createStatement();
			ResultSet rs = s.executeQuery("SELECT max(confNo) as maxConfNo FROM Reservation");
			if (rs.next()) {
				int confno = rs.getInt("maxConfNo") + 1;

				PreparedStatement ps = connection.prepareStatement("INSERT INTO Reservation VALUES (?,?,?,?,?)");
				ps.setInt(1, confno);
				ps.setString(2, vtname);
				ps.setLong(3, dLicence);
				ps.setTimestamp(4, fromDate);
				ps.setTimestamp(5, toDate);

				ps.executeUpdate();
				connection.commit();

				ps.close();
				System.out.println("You've made a reservation for " + fromDate + " to "
						+ toDate);
				if (vtname != null) {
					System.out.println("Vehicle Type: " + vtname);
				}
				System.out.println("Your confirmation number is " + confno);
			} else {
				System.out.println("Unable to make reservation. Please try again.");
			}
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}

	public boolean isValidReservation(String location, String vtnme, Timestamp startDate, Timestamp endDate) {
		return numberVehiclesAvailable(location, vtnme, startDate, endDate) > 0;
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
				rs = s.executeQuery("SELECT * FROM VEHICLE WHERE STATUS = 'available' AND VTNAME = " + "'" + reservation.getVtname()+ "'");
				rs.next();
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
				rs.close();
				rs1.close();
				s.close();
				s1.close();

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

	// insert rental W/O reservation
	public void insertRental (String cardName, int cardNo, String expDate) {
		// make a reservation

		// call insert rental with confNo
	}
	
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
