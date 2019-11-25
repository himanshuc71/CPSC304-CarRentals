package ca.ubc.cs304.database;

import java.math.BigDecimal;
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

	public int numberVehiclesAvailable(String location, String vtname, Timestamp fromDate, Timestamp toDate){
		return getLicenses(location, vtname, fromDate, toDate).size();
	}

	public void printVehicles(ArrayList licences) {
		ArrayList vehicles = new java.util.ArrayList<Vehicle>();
		licences.forEach((licence) -> vehicles.add(getVehicleFromLicence((String) licence)));
		vehicles.forEach((vehicle) -> System.out.println("\n" + vehicle.toString()+ "\n"));
	}

	private Vehicle getVehicleFromLicence(String licence) {
		java.util.ArrayList alRowData = new java.util.ArrayList();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM Vehicle WHERE vlicense = ?");
			ps.setString(1, licence);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				java.sql.ResultSetMetaData rsmd = rs.getMetaData();
				int numberOfColumns = rsmd.getColumnCount();
				for(int columnIndex = 1; columnIndex <= numberOfColumns; columnIndex ++){
					alRowData.add(rs.getObject(columnIndex));
				}
			}
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return new Vehicle(alRowData.get(0).toString(), alRowData.get(1).toString(), alRowData.get(2).toString(),alRowData.get(3).toString(),
				alRowData.get(4).toString(), ((BigDecimal)alRowData.get(5)).intValue(),alRowData.get(6).toString(),alRowData.get(7).toString(),alRowData.get(8).toString(),
				alRowData.get(9).toString());
	}

	public ArrayList getLicenses(String location, String vtname, Timestamp fromDate, Timestamp toDate) {
		ArrayList vlicenses = new java.util.ArrayList();
		if (location == null && vtname == null && (fromDate != null && toDate != null)) {
			vlicenses = numberVehiclesAvailableDates(fromDate, toDate);
		} else if (location != null && vtname == null && (fromDate == null || toDate == null)) {
			vlicenses = numberVehiclesAvailableLocation(location);
		} else if (location == null && vtname != null && (fromDate == null || toDate == null)) {
			vlicenses = numberVehiclesAvailableVTname(vtname);
		} else if (location != null && vtname != null && (fromDate == null || toDate == null)) {
			vlicenses = numberVehiclesAvailableLocationVTname(location, vtname);
		} else if (location != null && vtname == null) {
			vlicenses = numberVehiclesAvailableLocationDates(location, fromDate, toDate);
		} else if (location == null && vtname != null) {
			vlicenses = numberVehiclesAvailableVTnameDates(vtname, fromDate, toDate);
		} else {
			int numberOfRows = 0;
			try {
				// need to have different cases if any of the inputs are blank
				// using available status
				// check the rental table, reservation, vehicle and vehicle type
				PreparedStatement ps = connection.prepareStatement("SELECT v.vlicense " +
						"FROM Vehicle v WHERE v.status = 'available' AND v.location = ? AND v.vtname = ? " +
						"MINUS " +
						"SELECT r.vlicense " +
						"FROM Rental r " +
						"WHERE r.fromDateTime BETWEEN ? and ? " +
						"OR r.toDateTime BETWEEN ? and ?");
				ps.setString(1, location);
				ps.setString(2, vtname);
				ps.setTimestamp(3, fromDate);
				ps.setTimestamp(4, toDate);
				ps.setTimestamp(5, fromDate);
				ps.setTimestamp(6, toDate);

				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					java.sql.ResultSetMetaData rsmd = rs.getMetaData();
					int numberOfColumns = rsmd.getColumnCount();
					for(int columnIndex = 1; columnIndex <= numberOfColumns; columnIndex ++){
						vlicenses.add(rs.getObject(columnIndex));
					}
				}
				connection.commit();
				ps.close();
			} catch (SQLException e) {
				System.out.println(EXCEPTION_TAG + " " + e.getMessage());
				rollbackConnection();
				vlicenses = null;
			}
		}
		return vlicenses;
	}

	private ArrayList numberVehiclesAvailableLocation(String location) {
		java.util.ArrayList vlicenses = new java.util.ArrayList();
		// int numberOfRows = 0;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT vlicense FROM Vehicle v WHERE location = ? AND status = 'available'");
			ps.setString(1, location);


			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				java.sql.ResultSetMetaData rsmd = rs.getMetaData();
				int numberOfColumns = rsmd.getColumnCount();
				for(int columnIndex = 1; columnIndex <= numberOfColumns; columnIndex ++){
					vlicenses.add(rs.getObject(columnIndex));
				}
			}
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			vlicenses = null;
		}
		return vlicenses;
	}

	private ArrayList numberVehiclesAvailableVTname(String vtname) {
		java.util.ArrayList vlicenses = new java.util.ArrayList();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT vlicense FROM Vehicle v WHERE vtname = ? AND status = 'available'");
			ps.setString(1, vtname);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				java.sql.ResultSetMetaData rsmd = rs.getMetaData();
				int numberOfColumns = rsmd.getColumnCount();
				for(int columnIndex = 1; columnIndex <= numberOfColumns; columnIndex ++){
					vlicenses.add(rs.getObject(columnIndex));
				}
			}
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			vlicenses = null;
		}
		return vlicenses;
	}

	private ArrayList numberVehiclesAvailableLocationVTname(String location, String vtname) {
		ArrayList vlicenses = new java.util.ArrayList();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT vlicense FROM Vehicle v WHERE location = ? AND vtname = ? AND status = 'available'");
			ps.setString(1, location);
			ps.setString(2, vtname);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				java.util.ArrayList alRowData = new java.util.ArrayList();
				java.sql.ResultSetMetaData rsmd = rs.getMetaData();
				int numberOfColumns = rsmd.getColumnCount();
				for(int columnIndex = 1; columnIndex <= numberOfColumns; columnIndex ++){
					alRowData.add(rs.getObject(columnIndex));
				}
				vlicenses = alRowData;
			}
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			vlicenses = null;
		}
		return vlicenses;
	}

	private ArrayList numberVehiclesAvailableDates(Timestamp fromDate, Timestamp toDate) {
		ArrayList vlicenses = new java.util.ArrayList();
		try {
			String getLicenceRental = "SELECT v.vlicense FROM Vehicle v WHERE v.status = 'available' " +
					"MINUS SELECT r.vlicense FROM Rental r WHERE r.fromDateTime " +
					"BETWEEN ? and ? OR r.toDateTime " +
					"BETWEEN ? and ?";
			PreparedStatement ps = connection.prepareStatement(getLicenceRental);
			ps.setTimestamp(1, fromDate);
			ps.setTimestamp(2, toDate);
			ps.setTimestamp(3, fromDate);
			ps.setTimestamp(4, toDate);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				java.sql.ResultSetMetaData rsmd = rs.getMetaData();
				int numberOfColumns = rsmd.getColumnCount();
				for(int columnIndex = 1; columnIndex <= numberOfColumns; columnIndex ++){
					vlicenses.add(rs.getObject(columnIndex));
				}
			}
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			vlicenses = null;
		}
		return vlicenses;
	}

	private ArrayList numberVehiclesAvailableLocationDates(String location, Timestamp fromDate, Timestamp toDate) {
		ArrayList vlicenses = new java.util.ArrayList();
		try {
			String getLicenceRental = "SELECT v.vlicense FROM Vehicle v WHERE v.status = 'available' AND v.location = ? " +
					"MINUS SELECT r.vlicense FROM Rental r WHERE r.fromDateTime " +
					"BETWEEN ? and ? OR r.toDateTime " +
					"BETWEEN ? and ?";
			PreparedStatement ps = connection.prepareStatement(getLicenceRental);
			ps.setString(1, location);
			ps.setTimestamp(2, fromDate);
			ps.setTimestamp(3, toDate);
			ps.setTimestamp(4, fromDate);
			ps.setTimestamp(5, toDate);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				java.sql.ResultSetMetaData rsmd = rs.getMetaData();
				int numberOfColumns = rsmd.getColumnCount();
				for(int columnIndex = 1; columnIndex <= numberOfColumns; columnIndex ++){
					vlicenses.add(rs.getObject(columnIndex));
				}
			}
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			vlicenses = null;
		}
		return vlicenses;
	}

	private ArrayList numberVehiclesAvailableVTnameDates(String vtname, Timestamp fromDate, Timestamp toDate) {
		ArrayList vlicenses = new java.util.ArrayList();
		try {
			String getLicenceRental = "SELECT v.vlicense FROM Vehicle v WHERE v.status = 'available' AND v.vtname = ? " +
					"MINUS SELECT r.vlicense FROM Rental r WHERE r.fromDateTime " +
					"BETWEEN ? and ? OR r.toDateTime " +
					"BETWEEN ? and ?";
			PreparedStatement ps = connection.prepareStatement(getLicenceRental);
			ps.setString(1, vtname);
			ps.setTimestamp(2, fromDate);
			ps.setTimestamp(3, toDate);
			ps.setTimestamp(4, fromDate);
			ps.setTimestamp(5, toDate);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				java.sql.ResultSetMetaData rsmd = rs.getMetaData();
				int numberOfColumns = rsmd.getColumnCount();
				for(int columnIndex = 1; columnIndex <= numberOfColumns; columnIndex ++){
					vlicenses.add(rs.getObject(columnIndex));
				}
			}
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			vlicenses = null;
		}
		return vlicenses;
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

				System.out.println("");
				System.out.println("Dear " + customer.getCname() + ",\n" +
						"Detail's about your rental:\n" +
						"License number: " + rental.getvLicense() + "\n" +
						"Rental ID: " + rentalId + "\n" +
						"Car: " + vehicle.getMake() + " " + vehicle.getModel() + "\n" +
						"Color: " + vehicle.getColor() + "\n" +
						"Dates: " + rental.getFromDateTime() + " to \n" +
						rental.getToDateTime() +
						"\n\nThank you!.");
			} else {
				System.out.println(WARNING_TAG + "No reservation was found for the confirmation number: "
						+ confNo + " please try again.");
			}
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

	public void insertReturn(ReturnModel returnModel, String total_string){
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
            ResultSet rs = statement.executeQuery("SELECT RENTAL.VLICENSE, RENTAL.DLICENSE, RENTAL.FROMDATETIME " +
					"FROM RENTAL WHERE RID = "
                    + returnModel.getRid());
            rs.next();
            String vlicense = rs.getString(1);
            Long dlicense = rs.getLong(2);
            String fromDate = rs.getString(3);
            rs.close();
            rs = statement.executeQuery("SELECT * FROM CUSTOMER WHERE DLICENSE = " + dlicense);
            rs.next();
            CustomerModel customer = new CustomerModel(rs.getLong(2),rs.getString(3), rs.getString(4), rs.getLong(1));
            rs.close();
            rs = statement.executeQuery("SELECT * FROM VEHICLE WHERE VLICENSE = " + "'" + vlicense + "'");
            rs.next();
            Vehicle vehicle = new Vehicle(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
					rs.getString(5), rs.getInt(6), rs.getString(7), rs.getString(8), rs.getString(9),
					rs.getString(10));
            rs.close();
            statement.executeQuery("UPDATE VEHICLE SET STATUS = 'available', ODOMETER = ODOMETER + " + returnModel.getOdometer() +
                    " where VLICENSE = " + "'" + vlicense + "'");
            connection.commit();
            statement.close();
			System.out.println(" ");
            System.out.println("Dear " + customer.getCname() + ",\n" +
					"Rental Receipt: \n" +
					"License Number: " + vehicle.getvLicence() +"\n" +
					"Rental ID: " + returnModel.getRid() + "\n" +
					"Car: " + vehicle.getMake() + " " + vehicle.getModel() + "\n" +
					"Color: " + vehicle.getColor() + "\n" +
					"Dates: " + fromDate + " to \n" +
					returnModel.getRtnDateTime() + "\n\n" +
					"TOTAL: " + total_string + " = $" + returnModel.getValue() + "\n\n" +
					"Thank you for renting with us.");
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public String[] calcValue (int rid, Timestamp rtnDateTime, int current_odometer) {
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
			float value = numWeeks * vehicleType.getwRate() + numDays * vehicleType.getdRate() + numHours * vehicleType.gethRate() +
					numWeeks * vehicleType.getWiRate() + numDays * vehicleType.getDiRate()
					+ numHours * vehicleType.getHiRate() + kmsCovered * vehicleType.getkRate();
			String total_value = "Number of weeks * weekly rate + number of days * daily rate + number of hours * hourly rate + \n" +
					"number of weeks * weekly insurance rate + number of days * daily insurance rate + \n" +
					"number of hours * hourly insurance rate + Kilometers covered * per km rate = \n" +
					numWeeks + " * " + vehicleType.getwRate() + " + " + numDays + " * " + vehicleType.getdRate() + " + " +
					numHours + " * " + vehicleType.gethRate() + " + " + numWeeks + " * " + vehicleType.getWiRate() + " + " +
					numDays + " * " + vehicleType.getDiRate() + " + " + numHours + " * " + vehicleType.getHiRate() + " + " +
					kmsCovered + " * " + vehicleType.getkRate();

			return new String[]{total_value, String.valueOf(value)};

		} catch (SQLException e){
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
		return new String[] {"", "0"};
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

    public void generateDailyRentals(String date){
	    try {
	        Statement statement = connection.createStatement();
	        String query = "select v.city as Branch_City," +
                    "v.LOCATION as Branch_Location," +
                    "count(vB.vlicense) as Total_Branch_Count," +
                    "v.VTNAME as Vehicle_Type," +
                    "count(r.rid) as Vehicle_Type_Count " +
                    "from Vehicle v " +
                    "inner join Rental r on r.vlicense = v.vlicense " +
                    "inner join (select v2.vlicense, " +
                    "v2.city, " +
                    "v2.location " +
                    "from Vehicle v2) vB " +
                    "on vB.vlicense = v.vlicense " +
                    "and vB.city = v.city " +
                    "and vB.location = v.location " +
                    "where to_date(to_char(r.fromDateTime, 'YYYY-MM-DD'), 'YYYY-MM-DD') = " +
                    "to_date('" + date + "', 'YYYY-MM-DD') " +
                    "group by v.city, v.location, v.vtname, v.vlicense";
	        ResultSet rs = statement.executeQuery(query);

            // get info on ResultSet
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            System.out.println(" ");

            // display column names;
    		for (int i = 0; i < columnsNumber; i++) {
    			// get column name and print it
                if (i < columnsNumber - 1)
                    System.out.print(rsmd.getColumnName(i + 1) + "  |  ");
                else
                    System.out.print(rsmd.getColumnName(i + 1) + "\n");
    		}

            while (rs.next()) {
                //Print one row
                for(int i = 1 ; i <= columnsNumber; i++){
                    if (i < columnsNumber)
                        System.out.print(rs.getString(i) + "\t\t\t\t"); //Print one element of a row
                    else
                        System.out.print(rs.getString(i) + " "); //Print one element of a row
                }
                System.out.println();//Move to the next line to print the next row.
            }
            System.out.println(" ");
            rs.close();
            query = "select count(r.rid) as Total_Count " +
                    "from Vehicle v " +
                    "inner join Rental r on r.vlicense = v.vlicense " +
                    "where to_date(to_char(r.fromDateTime, 'YYYY-MM-DD'), 'YYYY-MM-DD') = " +
                    "to_date('" + date + "', 'YYYY-MM-DD')";
            rs = statement.executeQuery(query);
            rs.next();

            System.out.println("TOTAL RENTALS BY THE COMPANY ON " + date + " : " + rs.getInt(1));

        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public void generateDailyRentalsByBranch(String date, String location, String city){
	    try{
	        Statement statement = connection.createStatement();
	        String query = "select v.city as Branch_City, " +
                    "v.location as Branch_Location, " +
                    "v.vtname as Vehicle_Type, " +
                    "count(r.rid) as Vehicle_Type_Count " +
                    "from Vehicle v " +
                    "inner join Rental r on r.vlicense = v.vlicense " +
                    "where v.city = '" + city + "' " +
                    "and v.location = '" + location + "' " +
                    "and to_date(to_char(r.fromDateTime, 'YYYY-MM-DD'), 'YYYY-MM-DD') = " +
                    "to_date('" + date + "', 'YYYY-MM-DD') " +
                    "group by v.city, v.location, v.vtname";
	        ResultSet rs = statement.executeQuery(query);

            // get info on ResultSet
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            System.out.println(" ");

            // display column names;
            for (int i = 0; i < columnsNumber; i++) {
                // get column name and print it
                if (i < columnsNumber - 1)
                    System.out.print(rsmd.getColumnName(i + 1) + "  |  ");
                else
                    System.out.print(rsmd.getColumnName(i + 1) + "\n");
            }

            while (rs.next()) {
                //Print one row
                for(int i = 1 ; i <= columnsNumber; i++){
                    if (i < columnsNumber)
                        System.out.print(rs.getString(i) + "\t\t\t\t"); //Print one element of a row
                    else
                        System.out.print(rs.getString(i) + " "); //Print one element of a row
                }
                System.out.println();//Move to the next line to print the next row.
            }
            System.out.println(" ");
            rs.close();

            query = "select v.city as Branch_City, " +
                    "v.location as Branch_Location, " +
                    "count(r.rid) as Branch_Total_Count " +
                    "from Vehicle v " +
                    "inner join Rental r on r.vlicense = v.vlicense " +
                    "where to_date(to_char(r.fromDateTime, 'YYYY-MM-DD'), 'YYYY-MM-DD') = " +
                    "to_date('2019-05-01', 'YYYY-MM-DD') " +
                    "and v.city = '" + city + "' " +
                    "and v.location = '" + location + "' " +
                    "group by v.city, v.location";

            rs = statement.executeQuery(query);
            rs.next();

            System.out.println("TOTAL RENTALS BY THE BRANCH ON " + date + " : " + rs.getInt(3));
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

	public void printDailyReturns(String date){
		try {
			PreparedStatement ps1;
			PreparedStatement ps = connection.prepareStatement("select v.city as Branch_City, " +
					"v.location as Branch_Location, " +
					"count(rtB.rid) as Branch_Count," +
					"sum(rtB.value) as Branch_Revenue, " +
					"v.vtname as Vehicle_Type, " +
					"count(rt.rid) as Return_Count, " +
					"sum(rt.value) as Return_Revenue " +
					"from Rental r " +
					"inner join Vehicle v on r.vlicense = v.vlicense " +
					"inner join Return rt on rt.rid = r.rid " +
					"inner join (select rt2.rid, " +
					"rt2.value, rt2.rtnDateTime from Return rt2) rtB on rtB.rid = r.rid " +
					"where to_date(to_char(rt.rtnDateTime, 'YYYY-MM-DD'), 'YYYY-MM-DD') = to_date(?, 'YYYY-MM-DD') " +
					"group by v.city, v.location, v.vtname, v.vlicense");
			ps.setString(1, date);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			// display column names;
			for (int i = 0; i < columnsNumber; i++) {
				// get column name and print it
				if (i < columnsNumber - 1)
					System.out.print(rsmd.getColumnName(i + 1) + "  |  ");
				else
					System.out.print(rsmd.getColumnName(i + 1) + "\n");
			}
			while (rs.next()) {
				//Print one row
				for (int i = 1; i <= columnsNumber; i++) {
					if (i < columnsNumber)
						System.out.print(rs.getString(i) + "\t\t\t\t"); //Print one element of a row
					else
						System.out.print(rs.getString(i) + " "); //Print one element of a row
				}
				System.out.println();//Move to the next line to print the next row.
			}
			System.out.println(" ");
			ps1 = connection.prepareStatement("select count(rt.rid) as Total_Count, " +
					"sum(rt.value) as Total_Revenue " +
					"from Rental r " +
					"inner join Vehicle v on v.vlicense = r.vlicense " +
					"inner join Return rt on rt.rid = r.rid " +
					"where to_date(to_char(rt.rtnDateTime, 'YYYY-MM-DD'), 'YYYY-MM-DD') = to_date(?, 'YYYY-MM-DD')");
			ps1.setString(1, date);
			ResultSet rs1 = ps1.executeQuery();
			ResultSetMetaData rsmd1 = rs1.getMetaData();
			int columnsNumber1 = rsmd1.getColumnCount();
			while (rs1.next()) {
				for (int i = 1; i <= columnsNumber1; i++) {
					if (i > 1) System.out.print(",  ");
					String columnValue = rs1.getString(i);
					System.out.print(rsmd1.getColumnName(i) + ": " + columnValue);
				}
				System.out.println("");
			}

			rs.close();
			rs1.close();
			ps.close();
			ps1.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}

	public void printDailyReturnsByBranch(String city, String location, String date){
		try {
			PreparedStatement ps1;
			PreparedStatement ps = connection.prepareStatement("select v.city as Branch_City, " +
					"v.location as Branch_Location, " +
					"v.vtname as Vehicle_Type, " +
					"count(rt.rid) as Return_Count, " +
					"sum(rt.value) as Revenue " +
					"from Rental r " +
					"inner join Vehicle v on v.vlicense = r.vlicense " +
					"inner join Return rt on rt.rid = r.rid " +
					"where v.city = ? " +
					"and v.location = ? " +
					"and to_date(to_char(rt.rtnDateTime, 'YYYY-MM-DD'), 'YYYY-MM-DD') = to_date(?, 'YYYY-MM-DD') " +
					"group by v.city, v.location, v.vtname");
			ps.setString(1, city);
			ps.setString(2, location);
			ps.setString(3, date);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();

			int columnsNumber = rsmd.getColumnCount();
			// display column names;
			for (int i = 0; i < columnsNumber; i++) {
				// get column name and print it
				if (i < columnsNumber - 1)
					System.out.print(rsmd.getColumnName(i + 1) + "  |  ");
				else
					System.out.print(rsmd.getColumnName(i + 1) + "\n");
			}
			while (rs.next()) {
				//Print one row
				for (int i = 1; i <= columnsNumber; i++) {
					if (i < columnsNumber)
						System.out.print(rs.getString(i) + "\t\t\t\t"); //Print one element of a row
					else
						System.out.print(rs.getString(i) + " "); //Print one element of a row
				}
				System.out.println();//Move to the next line to print the next row.
			}
			System.out.println(" ");

			ps1 = connection.prepareStatement("select v.city as Branch_City, " +
					"v.location as Branch_Location, " +
					"count(rt.rid) as Return_Count, " +
					"sum(rt.value) as Revenue " +
					"from Rental r " +
					"inner join Vehicle v on v.vlicense = r.vlicense " +
					"inner join Return rt on rt.rid = r.rid " +
					"where v.city = ? " +
					"and v.location = ? " +
					"and to_date(to_char(rt.rtnDateTime, 'YYYY-MM-DD'), 'YYYY-MM-DD') = to_date(?, 'YYYY-MM-DD') " +
					"group by v.city, v.location");
			ps1.setString(1, city);
			ps1.setString(2, location);
			ps1.setString(3, date);
			ResultSet rs1 = ps1.executeQuery();
			ResultSetMetaData rsmd1 = rs1.getMetaData();
			int columnsNumber1 = rsmd1.getColumnCount();
			while (rs1.next()) {
				for (int i = 1; i <= columnsNumber1; i++) {
					if (i > 1) System.out.print(",  ");
					String columnValue = rs1.getString(i);
					System.out.print(rsmd1.getColumnName(i) + ": " + columnValue);
				}
				System.out.println("");
			}

			rs.close();
			rs1.close();
			ps.close();
			ps1.close();

		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}
	
	public boolean login(String username, String password) {
		try {
			if (connection != null) {
				connection.close();
			}
	
			connection = DriverManager.getConnection(ORACLE_URL, username, password);
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
