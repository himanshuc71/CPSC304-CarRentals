package ca.ubc.cs304.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
import ca.ubc.cs304.model.BranchModel;
import ca.ubc.cs304.model.CustomerModel;

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

	public void showMainMenu(TerminalTransactionsDelegate delegate) {
		this.delegate = delegate;

		bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		int choice = INVALID_INPUT;

		while (choice != 5) {
			System.out.println();
			System.out.println("1. If you are a Customer");
			System.out.println("2. If you are a Clerk");
			System.out.println("3. Quit");
			System.out.print("Please choose one of the above 3 options: ");

			choice = readInteger(false);

			System.out.println(" ");

			if (choice != INVALID_INPUT) {
				switch (choice) {
					case 1:
						handleCustomer();
						break;
					case 2:
						handleClerk();
						break;
					case 3:
						handleQuitOption();
						break;
					default:
						System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
						break;
				}
			}
		}
	}


	private void handleCustomer() {
		int choice = INVALID_INPUT;
		while (choice != 5) {
			System.out.println();
			System.out.println("1. See cars available");
            System.out.println("2. Make a reservation");
            System.out.println("3. Create an account");
			System.out.println("4. Back");
			System.out.println("5. Quit");
			System.out.print("Please choose one of the above 4 options: ");
			choice = readInteger(false);
			System.out.println(" ");
			if (choice != INVALID_INPUT) {
				switch (choice) {
					case 1:
                        findNumVehicles();
						break;
					case 2:
						makeReservation();
						break;
                    case 3:
                        createAccount();
                        break;
					case 4:
						showMainMenu(delegate);
						break;
					case 5:
						handleQuitOption();
						break;
					default:
						System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
						break;
				}
			}
		}
	}

	private void createAccount() {
		int dLicense = INVALID_INPUT, cell = INVALID_INPUT;
		String name = null, address = null;
		while (dLicense == INVALID_INPUT) {
			System.out.print("Please enter your Driver's License number: ");
			dLicense = readInteger(false);
		}
		while (delegate.customerExists(dLicense)) {
			System.out.print("Licence number already exists. Please enter a different licence number: ");
			dLicense = readInteger(false);
		}
		System.out.print("Lets create your customer account. Please enter your full name: ");
		name = readLine().trim();
		while (cell == INVALID_INPUT) {
			System.out.print("Cellphone number: ");
			cell = readInteger(false);
		}
		System.out.print("Please enter your address: ");
		address = readLine().trim();
		CustomerModel customer = new CustomerModel(cell, name, address, dLicense);
		delegate.insertCustomer(customer);
		System.out.println();
		System.out.print("Customer account created");
		System.out.println();
		showMainMenu(delegate);
	}

	private void findNumVehicles() {
		Timestamp fromDate, toDate;
		System.out.print("Find vehicles available based on the following inputs:");
		String[] branch = getBranch();
		String vtname = getType();
		fromDate = getDate("From");
		toDate = getDate("To");

		int available = delegate.numberVehiclesAvailable(branch[0], vtname, fromDate, toDate);
		System.out.print("There are " + available + " cars available that fit your input.");

	}

	private void makeReservation() {
        Timestamp fromDate, toDate;
        int dLicense = INVALID_INPUT;
        while (dLicense == INVALID_INPUT) {
            System.out.print("Please enter your Driver's License number: ");
            dLicense = readInteger(false);
        }
        while (!delegate.customerExists(dLicense) && dLicense != 1) {
            System.out.print("Licence number does not exist. Please enter a licence number attached to an account," +
                    " or type 1 to create an account: ");
            dLicense = readInteger(false);
        }
        if (dLicense == 1) {
            createAccount();
        }
        String name = delegate.getNameFromLicence(dLicense);
        System.out.print("Hello " + name + ", make a reservation based on the following inputs");

        System.out.print("Start rental on date (YYYY-MM-DD): ");
        String startDate = readLine().trim();
        if (!validateDate(startDate)) {
            System.out.print("Invalid date format. Please re-enter rental start date (YYYY-MM-DD): ");
            startDate = readLine().trim();
        }
        System.out.print("End rental on date (YYYY-MM-DD): ");
        String endDate = readLine().trim();
        if (!validateDate(endDate)) {
            System.out.print("Invalid date format. Please re-enter rental start date (YYYY-MM-DD): ");
            endDate = readLine().trim();
        }
        Timestamp startDateTimestamp = getTimeStampAsString(startDate);
        Timestamp endDateTimestamp = getTimeStampAsString(endDate);

        System.out.print("Vehicle Type (Compact, Economy, Mid-size, Standard, Full-size, SUV, Truck): ");
        String vtname = readLine().trim();
        if (!(delegate.vehicleTypeExists(vtname))) {
            System.out.print("Invalid vehicle type. Press 1 to re-enter, or 2 to skip: ");
            int choice = readInteger(true);
            switch (choice) {
                case 1:
                    vtname = readLine().trim();
                    break;
                case 2:
                    vtname = null;
                    break;
                default:
                    System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
                    vtname = null;
                    break;
            }
        }


    }

	private String[] getBranch() {
		String location, city;
		System.out.println();
		System.out.print("Location: ");
		location = readLine().trim();
		System.out.print("City: ");
		city = readLine().trim();
		if (!delegate.branchExists(location, city)) {
			System.out.print("Branch does not exist. Press 1 to enter a different branch or 2 to skip: ");
			int choice = readInteger(true);
			switch (choice) {
				case 1:
					getBranch();
					break;
				case 2:
					location = null;
					city = null;
					break;
				default:
					System.out.println(WARNING_TAG + " The input that you entered was not a valid option. Please re-enter: ");
					location = null;
					city = null;
			}
		}
		return new String[] {location, city};
	}

	private String getType() {
		String vtname;
		System.out.println();
		System.out.print("Vehicle Type: ");
		vtname = readLine().trim();
		if (!delegate.vehicleTypeExists(vtname)) {
			System.out.print("That vehicle type does not exist. Press 1 to enter a different type or 2 to skip: ");
			int choice = readInteger(true);
			switch (choice) {
				case 1:
					getType();
					break;
				case 2:
					vtname = null;
					break;
				default:
					System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
					vtname = null;
					break;
			}
		}
		return vtname;
	}

	private Timestamp getDate(String dateType) {
		System.out.print(dateType + " date (YYYY-MM-DD): ");
		String date = readLine().trim();
		if (!validateDate(date)) {
			System.out.print("Invalid date format. Press 1 to enter a different date or 2 to skip: ");
			int choice = readInteger(true);
			switch (choice) {
				case 1:
					getDate(dateType);
					break;
				case 2:
					date = null;
					break;
				default:
					System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
					date = null;
					break;
			}
		}
		return getTimeStampAsString(date);
	}

	private boolean validateDate(String dateString) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
			Date date = format.parse(dateString + " 00:00:00");
			Timestamp ts = new Timestamp(date.getTime());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Timestamp getTimeStampAsString(String dateString) {
		try {
			String date = dateString + " 00:00:00";
			return Timestamp.valueOf(date);
		} catch (Exception e) {
			return null;
		}
	}

	private void handleClerk() {
		// functionalities for Clerk
        int choice = INVALID_INPUT;
        while (choice != 7) {
            System.out.println();
            System.out.println("1. Rent a vehicle for a Customer ");
            System.out.println("2. Return a vehicle for a Customer ");
            System.out.println("3. Generate daily rentals ");
            System.out.println("4. Generate daily rentals by branch ");
            System.out.println("5. Generate daily returns ");
            System.out.println("6. Generate daily returns by branch ");
            System.out.println("7. Go back to the main menu");

            System.out.print("Please choose one of the above 7 options: ");

            System.out.println();

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
	    while (reserve == null || reserve.length() <= 0) {
            System.out.println("Is there a reservation? Enter y/n: ");
            reserve = readLine().trim();
            if (reserve.toLowerCase().equals("y")) {
                handleRentalWReservation();
                break;
            } else if (reserve.toLowerCase().equals("n")) {
                handleRentalWOReservation();
                break;
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
            	String cardName = null;
            	while (cardName == null) {
					System.out.println("Enter the Credit Card Name: ");
					cardName = readLine();
					if (cardName != null) {
						int cardNo = INVALID_INPUT;
						while (cardNo == INVALID_INPUT) {
							System.out.println("Enter the Credit Card No: ");
							cardNo = readInteger(false);
							if (cardNo != INVALID_INPUT) {
								String expDate = null;
								while (expDate == null) {
									System.out.println("Enter the Credit Card Expiration Date in format mm/yy: ");
									expDate = readLine();
									if (expDate != null) {
										delegate.insertRental(confNo, cardName, cardNo, expDate);
									}
								}
							}
						}
					}
				}
            }
        }
    }

    private void handleRentalWOReservation() {
        //TODO
		//delegate.insertRental(cardName, cardNo, expDate);
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
        showMainMenu(delegate);
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
