package ca.ubc.cs304.controller;

import ca.ubc.cs304.database.DatabaseConnectionHandler;
import ca.ubc.cs304.delegates.LoginWindowDelegate;
import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
import ca.ubc.cs304.model.BranchModel;
import ca.ubc.cs304.model.CustomerModel;
import ca.ubc.cs304.ui.LoginWindow;
import ca.ubc.cs304.ui.TerminalTransactions;

import java.sql.Timestamp;

/**
 * This is the main controller class that will orchestrate everything.
 */
public class Bank implements LoginWindowDelegate, TerminalTransactionsDelegate {
	private DatabaseConnectionHandler dbHandler = null;
	private LoginWindow loginWindow = null;

	public Bank() {
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

	public void insertRental (int confNo, String cardName, int cardNo, String expDate) {
	    dbHandler.insertRental(confNo, cardName, cardNo, expDate);
    }

	public void insertRental (String cardName, int cardNo, String expDate) {
		dbHandler.insertRental(cardName, cardNo, expDate);
	}
	public boolean customerExists(int dLicence) { return dbHandler.customerExists(dLicence); }
	public boolean branchExists(String location, String city) {return dbHandler.branchExists(location, city);}
	public boolean vehicleTypeExists(String vtname){return dbHandler.vehicleTypeExists(vtname);}

	public String getNameFromLicence(int dLicence) { return dbHandler.getNameFromLicence(dLicence); }

	public int numberVehiclesAvailable(String location, String vtname, Timestamp fromDate, Timestamp toDate) {
		return dbHandler.numberVehiclesAvailable(location, vtname, fromDate, toDate);
	}
	
//	/**
//	 * TermainalTransactionsDelegate Implementation
//	 *
//	 * Insert a branch with the given info
//	 */
//    public void insertBranch(BranchModel model) {
//    	dbHandler.insertBranch(model);
//    }
//
//    /**
//	 * TermainalTransactionsDelegate Implementation
//	 *
//	 * Delete branch with given branch ID.
//	 */
//    public void deleteBranch(int branchId) {
//    	dbHandler.deleteBranch(branchId);
//    }
//
//    /**
//	 * TermainalTransactionsDelegate Implementation
//	 *
//	 * Update the branch name for a specific ID
//	 */
//
//    public void updateBranch(int branchId, String name) {
//    	dbHandler.updateBranch(branchId, name);
//    }
//
//    /**
//	 * TermainalTransactionsDelegate Implementation
//	 *
//	 * Displays information about varies bank branches.
//	 */
//    public void showBranch() {
//    	BranchModel[] models = dbHandler.getBranchInfo();
//
//    	for (int i = 0; i < models.length; i++) {
//    		BranchModel model = models[i];
//
//    		// simplified output formatting; truncation may occur
//    		System.out.printf("%-10.10s", model.getId());
//    		System.out.printf("%-20.20s", model.getName());
//    		if (model.getAddress() == null) {
//    			System.out.printf("%-20.20s", " ");
//    		} else {
//    			System.out.printf("%-20.20s", model.getAddress());
//    		}
//    		System.out.printf("%-15.15s", model.getCity());
//    		if (model.getPhoneNumber() == 0) {
//    			System.out.printf("%-15.15s", " ");
//    		} else {
//    			System.out.printf("%-15.15s", model.getPhoneNumber());
//    		}
//
//    		System.out.println();
//    	}
//    }
	
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
		Bank bank = new Bank();
		bank.start();
	}
}
