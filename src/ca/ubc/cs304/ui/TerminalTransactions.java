package ca.ubc.cs304.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
import ca.ubc.cs304.model.BranchModel;

/**
 * The class is only responsible for handling terminal text inputs. 
 */
public class TerminalTransactions {
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";
	private static final int INVALID_INPUT = Integer.MIN_VALUE;
	private static final int EMPTY_INPUT = 0;
	
	private BufferedReader bufferedReader = null;
	private TerminalTransactionsDelegate delegate = null;

	public TerminalTransactions() {
	}

	/**
	 * Displays simple text interface
	 */ 
	public void showMainMenu(TerminalTransactionsDelegate delegate) {
		this.delegate = delegate;
		
	    bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		int choice = INVALID_INPUT;
		
		while (choice != 3) {
			System.out.println();
//			System.out.println("1. Insert branch");
//			System.out.println("2. Delete branch");
//			System.out.println("3. Update branch name");
//			System.out.println("4. Show branch");
//			System.out.println("5. Quit");
			System.out.println("1. If you are a Customer");
			System.out.println("2. If you are a Clerk");
			System.out.println("3. Quit");
			System.out.print("Please choose one of the above 3 options: ");

			choice = readInteger(false);

			System.out.println(" ");

			if (choice != INVALID_INPUT) {
				switch (choice) {
				case 1:  
					//handleInsertOption();
					handleCustomer();
					break;
				case 2:  
					//handleDeleteOption();
					handleClerk();
					break;
				case 3:
					//handleUpdateOption();
					handleQuitOption();
					break;
//				case 4:
//					delegate.showBranch();
//					break;
//				case 5:
//					handleQuitOption();
//					break;
				default:
					System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
					break;
				}
			}
		}		
	}

	private void handleCustomer(){
	    int dLicense = INVALID_INPUT;
	    while (dLicense == INVALID_INPUT) {
            System.out.print("Please enter your Driver's License number: ");
            dLicense = readInteger(false);
            // check if customer exists in database if not make a new customer
			// Customer can view all vehicles, or a subset based on {car type, location, time interval}
			// when making a reservation see if the customer exists in the database if not make a new customer
			// by asking info and display the reservation conf no and stuff and PUT the new tuples in the
			// database
        }

	}

	private void handleClerk() {
		// functionalities for Clerk
        int choice = INVALID_INPUT;
        while (choice != 7) {
            System.out.println();
            System.out.print("1. Rent a vehicle for a Customer ");
            System.out.print("2. Return a vehicle for a Customer ");
            System.out.print("3. Generate daily rentals ");
            System.out.print("4. Generate daily rentals by branch ");
            System.out.print("5. Generate daily returns ");
            System.out.print("6. Generate daily returns by branch ");

            choice = readInteger(false);

            System.out.println(" ");

            if (choice != INVALID_INPUT) {
                switch (choice) {
                    case 1:
                        handleRental();
                        break;
                    case 2:
                        handleReturn();
                        break;
                    case 3:
                        handleDailyRentals();
                        break;
				    case 4:
                        handleDailyRentalsByBranch();
					    break;
				    case 5:
                        handleDailyRenturns();
					    break;
                    case 6:
                        handleDailyRenturnsByBranch();
                        break;
                    case 7:
                        goBackToMainMenu();
                        break;
                    default:
                        System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
                        break;
                }
            }
        }
	}

    private void handleRental() {
	    String reserve = null;
	    while (reserve == null || reserve.length() <= 0 || !reserve.toLowerCase().equals("y")
                || !reserve.toLowerCase().equals("n")) {
            System.out.println("Is there a reservation? Enter y/n: ");
            reserve = readLine().trim();
            if (reserve.toLowerCase().equals("y")) {
                handleRentalWReservation();
            } else if (reserve.toLowerCase().equals("n")) {
                handleRentalWOReservation();
            } else {
                System.out.println("Invalid input try again");
            }
        }
	}

	private void handleRentalWReservation(){
	    int confNo = INVALID_INPUT;
	    while (confNo == INVALID_INPUT) {
            System.out.println();
            System.out.println("Enter the confirmation number for the Reservation: ");
            confNo = readInteger(false);
            if (confNo != INVALID_INPUT) {
                delegate.insertRental(confNo);
            }
        }
    }

    private void handleRentalWOReservation(){
        //TODO
    }

    private void handleReturn(){
        //TODO
    }

    private void handleDailyRentals() {
        //TODO
    }

    private void handleDailyRentalsByBranch() {
        //TODO
    }

    private void handleDailyRenturns() {
        //TODO
    }

    private void handleDailyRenturnsByBranch() {
        //TODO
    }

    private void goBackToMainMenu() {
        //TODO
    }
	
//	private void handleDeleteOption() {
//		int branchId = INVALID_INPUT;
//		while (branchId == INVALID_INPUT) {
//			System.out.print("Please enter the branch ID you wish to delete: ");
//			branchId = readInteger(false);
//			if (branchId != INVALID_INPUT) {
//				delegate.deleteBranch(branchId);
//			}
//		}
//	}
	
//	private void handleInsertOption() {
//		int id = INVALID_INPUT;
//		while (id == INVALID_INPUT) {
//			System.out.print("Please enter the branch ID you wish to insert: ");
//			id = readInteger(false);
//		}
//
//		String name = null;
//		while (name == null || name.length() <= 0) {
//			System.out.print("Please enter the branch name you wish to insert: ");
//			name = readLine().trim();
//		}
//
//		// branch address is allowed to be null so we don't need to repeatedly ask for the address
//		System.out.print("Please enter the branch address you wish to insert: ");
//		String address = readLine().trim();
//		if (address.length() == 0) {
//			address = null;
//		}
//
//		String city = null;
//		while (city == null || city.length() <= 0) {
//			System.out.print("Please enter the branch city you wish to insert: ");
//			city = readLine().trim();
//		}
//
//		int phoneNumber = INVALID_INPUT;
//		while (phoneNumber == INVALID_INPUT) {
//			System.out.print("Please enter the branch phone number you wish to insert: ");
//			phoneNumber = readInteger(true);
//		}
//
//		BranchModel model = new BranchModel(address,
//											city,
//											id,
//											name,
//											phoneNumber);
//		delegate.insertBranch(model);
//	}
	
	private void handleQuitOption() {
		System.out.println("Good Bye!");
		
		if (bufferedReader != null) {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				System.out.println("IOException!");
			}
		}
		
		delegate.terminalTransactionsFinished();
	}
	
//	private void handleUpdateOption() {
//		int id = INVALID_INPUT;
//		while (id == INVALID_INPUT) {
//			System.out.print("Please enter the branch ID you wish to update: ");
//			id = readInteger(false);
//		}
//
//		String name = null;
//		while (name == null || name.length() <= 0) {
//			System.out.print("Please enter the branch name you wish to update: ");
//			name = readLine().trim();
//		}
//
//		delegate.updateBranch(id, name);
//	}
	
	private int readInteger(boolean allowEmpty) {
		String line = null;
		int input = INVALID_INPUT;
		try {
			line = bufferedReader.readLine();
			input = Integer.parseInt(line);
		} catch (IOException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		} catch (NumberFormatException e) {
			if (allowEmpty && line.length() == 0) {
				input = EMPTY_INPUT;
			} else {
				System.out.println(WARNING_TAG + " Your input was not an integer");
			}
		}
		return input;
	}
	
	private String readLine() {
		String result = null;
		try {
			result = bufferedReader.readLine();
		} catch (IOException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
		return result;
	}
}
