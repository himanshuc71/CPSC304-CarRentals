package ca.ubc.cs304.controller;

import ca.ubc.cs304.database.DatabaseConnectionHandler;
import ca.ubc.cs304.delegates.LoginWindowDelegate;
import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
import ca.ubc.cs304.model.CustomerModel;
import ca.ubc.cs304.model.ReturnModel;
import ca.ubc.cs304.ui.LoginWindow;
import ca.ubc.cs304.ui.TerminalTransactions;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * This is the main controller class that will orchestrate everything.
 */
public class CarRental implements LoginWindowDelegate, TerminalTransactionsDelegate {
	private DatabaseConnectionHandler dbHandler = null;
	private LoginWindow loginWindow = null;

	public CarRental() {
		dbHandler = new DatabaseConnectionHandler();
	}
	
	private void start() {
		loginWindow = new LoginWindow();
		loginWindow.showFrame(this);
	}
	
	/**
	 * LoginWindowDelegate Implementation
	 * 
     * connects to Oracle database with supplied username and password
     */ 
	public void login(String username, String password) {
		boolean didConnect = dbHandler.login(username, password);

		if (didConnect) {
			// Once connected, remove login window and start text transaction flow
			loginWindow.dispose();

			TerminalTransactions transaction = new TerminalTransactions();
			transaction.showMainMenu(this);
		} else {
			loginWindow.handleLoginFailed();

			if (loginWindow.hasReachedMaxLoginAttempts()) {
				loginWindow.dispose();
				System.out.println("You have exceeded your number of allowed attempts");
				System.exit(-1);
			}
		}
	}

	/**
	 * TermainalTransactionsDelegate Implementation
	 *
	 * Insert a customer with the given info
	 */
	public void insertCustomer(CustomerModel customer) {
		dbHandler.insertCustomer(customer);
	}
	public boolean customerExists(long dLicence) { return dbHandler.customerExists(dLicence); }
	public boolean branchExists(String location, String city) {return dbHandler.branchExists(location, city);}
	public boolean vehicleTypeExists(String vtname){return dbHandler.vehicleTypeExists(vtname);}

	public String getNameFromLicence(long dLicence) { return dbHandler.getNameFromLicence(dLicence); }

	public int numberVehiclesAvailable(String location, String vtname, Timestamp fromDate, Timestamp toDate) {
		return dbHandler.numberVehiclesAvailable(location, vtname, fromDate, toDate);
	}

	public CustomerModel getCustomer(long licence) { return dbHandler.getCustomer(licence); }

	public void makeReservation(long dLicence, String vtname, Timestamp fromDate, Timestamp toDate) {
		dbHandler.makeReservation(dLicence, vtname, fromDate, toDate);
	}

	public boolean isValidReservation(String location, String vtname, Timestamp startDateTimestamp, Timestamp endDate){
		return dbHandler.isValidReservation(location, vtname, startDateTimestamp, endDate);
	}

	public void insertRental (int confNo, String cardName, int cardNo, String expDate) {
	    dbHandler.insertRental(confNo, cardName, cardNo, expDate);
    }

	public boolean checkRentalExists (int rid) {
	    return dbHandler.checkRentalExists(rid);
    }

    public void insertReturn(int rid, Timestamp rtnDateTime, int odometer, int fullTank){
        ReturnModel returnModel = new ReturnModel(rid, rtnDateTime, odometer, fullTank, Float.parseFloat(calcValue(rid, rtnDateTime, odometer)[1]));
	    dbHandler.insertReturn(returnModel, calcValue(rid, rtnDateTime, odometer)[0]);
    }
    public String[] calcValue(int rid, Timestamp rtnDateTime, int current_odometer) {
	    return dbHandler.calcValue(rid, rtnDateTime, current_odometer);
    }

    public void generateDailyRentals(String date){
	    dbHandler.generateDailyRentals(date);
    }

    public void generateDailyRentalsByBranch(String date, String location, String city){
        dbHandler.generateDailyRentalsByBranch(date, location, city);
    }

	public void printDailyReturns(String date){dbHandler.printDailyReturns(date);}

	public void printDailyReturnsByBranch(String city, String location, String date){dbHandler.printDailyReturnsByBranch(city,location,date);}

	public void printVehicles(ArrayList licences){
		dbHandler.printVehicles(licences);
	}

	public ArrayList getLicenses(String location, String vtname, Timestamp fromDate, Timestamp toDate) {
		return dbHandler.getLicenses(location, vtname, fromDate, toDate);
	}

	/**
	 * TerminalTransactionsDelegate Implementation
	 * 
     * The TerminalTransaction instance tells us that it is done with what it's 
     * doing so we are cleaning up the connection since it's no longer needed.
     */ 
    public void terminalTransactionsFinished() {
    	dbHandler.close();
    	dbHandler = null;
    	
    	System.exit(0);
    }
    
	/**
	 * Main method called at launch time
	 */
	public static void main(String args[]) {
		CarRental carRental = new CarRental();
		carRental.start();
	}
}
