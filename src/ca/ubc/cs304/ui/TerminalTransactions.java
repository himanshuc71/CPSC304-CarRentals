package ca.ubc.cs304.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
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

	/**
	 * Displays simple text interface
	 */
	public void showMainMenu(TerminalTransactionsDelegate delegate) {
		this.delegate = delegate;

		bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		int choice = INVALID_INPUT;

		System.out.println("\nMAIN MENU");
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
		System.out.println("CUSTOMER MENU");
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
						makeReservation("Customer");
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

	private CustomerModel createAccount() {
		long dLicense = INVALID_INPUT, cell = INVALID_INPUT;
		String name = null, address = null;
		while (dLicense == INVALID_INPUT) {
			System.out.print("Please enter your Driver's License number: ");
			dLicense = readLong(false);
		}
		while (delegate.customerExists(dLicense)) {
			System.out.print("Licence number already exists. Please enter a different licence number: ");
			dLicense = readLong(false);
		}
		System.out.print("Lets create your customer account. Please enter your full name: ");
		name = readLine().trim();
		while (cell == INVALID_INPUT) {
			System.out.print("Cellphone number: ");
			cell = readLong(false);
		}
		System.out.print("Please enter your address: ");
		address = readLine().trim();
		CustomerModel customer = new CustomerModel(cell, name, address, dLicense);
		delegate.insertCustomer(customer);
		System.out.println();
		System.out.print("Customer account created");
		System.out.println();
		return customer;
	}

	private void findNumVehicles() {
		String[] branch;
		String vtname;
		Timestamp fromDate, toDate;
		System.out.print("To filter by location, press 1 or press any key to skip: ");
		int choice = readInteger(true);
		switch (choice) {
			case 1:
				branch = getBranch();
				break;
			default:
				branch = new String[]{null, null};
				break;
		}
		System.out.print("To filter by Vehicle Type, press 1 or press any key to skip: ");
		choice = readInteger(true);
		switch (choice) {
			case 1:
				vtname = getType();
				break;
			default:
				vtname = null;
				break;
		}
		System.out.print("To filter by date, press 1 or press any key to skip: ");
		choice = readInteger(true);
		switch (choice) {
			case 1:
				fromDate = getDate("From");
				toDate = getDate("To");
				break;
			default:
				fromDate = null;
				toDate = null;
				break;
		}

		int available = delegate.numberVehiclesAvailable(branch[0], vtname, fromDate, toDate);
		System.out.println("\n There are " + available + " cars available that fit your input.\n");
		System.out.print("To see the details of the vehicles available press 1, or any key to skip: ");
		choice = readInteger(true);
		switch (choice) {
			case 1:
				printVehicles(branch[0], vtname, fromDate, toDate);
				break;
			default:
				showMainMenu(delegate);
				break;
		}
	}

	private void printVehicles(String location, String vtname, Timestamp fromDate, Timestamp toDate) {
		ArrayList licences = delegate.getLicenses(location, vtname, fromDate, toDate);
		delegate.printVehicles(licences);
		showMainMenu(delegate);
	}

	private void makeReservation(String whoCalled) {
		long dLicense = INVALID_INPUT;
		while (dLicense == INVALID_INPUT) {
			System.out.print("Please enter your Driver's License number: ");
			dLicense = readLong(false);
		}
		while (!delegate.customerExists(dLicense) && dLicense != 1) {
			System.out.print("Licence number does not exist. Please enter a licence number attached to an account," +
					" or type 1 to create an account: ");
			dLicense = readLong(false);
		}
		if (dLicense == 1) {
			CustomerModel customer = createAccount();
			dLicense = customer.getdLicense();
		}
		String name = delegate.getNameFromLicence(dLicense);
		System.out.println("Hello " + name + ", make a reservation based on the following inputs");

		System.out.print("Start rental on date (YYYY-MM-DD): ");
		String startDate = readLine().trim();
		if (!validateDate(startDate)) {
			System.out.print("Invalid date format. Please re-enter rental start date (YYYY-MM-DD): ");
			startDate = readLine().trim();
		}
		String startTime = getTime();

		System.out.print("End rental on date (YYYY-MM-DD): ");
		String endDate = readLine().trim();
		if (!validateDate(endDate)) {
			System.out.print("Invalid date format. Please re-enter rental start date (YYYY-MM-DD): ");
			endDate = readLine().trim();
		}
		String endTime = getTime();

		Timestamp startDateTimestamp = getTimeStampWithTime(startDate, startTime);
		Timestamp endDateTimestamp = getTimeStampWithTime(endDate, endTime);

		String vtname = getType();
		if (startDateTimestamp == null || endDateTimestamp == null) {
			System.out.println("Unable to make a reservation.");
			if (whoCalled.equals("Customer"))
				handleCustomer();
		} else {
			if (delegate.isValidReservation(null, vtname, startDateTimestamp, endDateTimestamp)) {
				delegate.makeReservation(dLicense, vtname, startDateTimestamp, endDateTimestamp);
				if (whoCalled.equals("Customer"))
					handleCustomer();
			} else {
				System.out.println("Unable to make a reservation with the dates and/or Vehicle Type entered.");
				if (whoCalled.equals("Customer"))
					handleCustomer();
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
		System.out.print("Vehicle Type (Compact, Economy, Mid-size, Standard, Full-size, SUV, Truck): ");
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
		System.out.print("At time (HH:MM): ");
		String time = getTime();
		return getTimeStampWithTime(date, time);
	}

	private String getTime() {
		System.out.print("At time (HH:mm): ");
		String time = readLine().trim();
		if (!validateTime(time)) {
			System.out.print("Invalid time format. Press 1 to enter a different date or 2 to skip: ");
			int choice = readInteger(true);
			switch (choice) {
				case 1:
					getTime();
					break;
				case 2:
					time = null;
					break;
				default:
					System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
					time = null;
					break;
			}
		}
		return time;
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

	private boolean validateTime(String timeString) {
		try {
			Pattern pattern;
			Matcher matcher;
			String TIME24HOURS_PATTERN =
					"([01]?[0-9]|2[0-3]):[0-5][0-9]";
			pattern = Pattern.compile(TIME24HOURS_PATTERN);
			matcher = pattern.matcher(timeString);
			return matcher.matches();
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

	private Timestamp getTimeStampWithTime(String dateString, String timeString) {
		try {
			if (timeString == null) {
				String date = dateString + " " + "00:00:00";
				return Timestamp.valueOf(date);
			} else {
				String date = dateString + " " + timeString + ":00";
				return Timestamp.valueOf(date);
			}
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
                        handleDailyReturns();
					    break;
                    case 6:
                        handleDailyReturnsByBranch();
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
					cardName = readLine().trim();
					if (cardName != null) {
						int cardNo = INVALID_INPUT;
						while (cardNo == INVALID_INPUT) {
							System.out.println("Enter the Credit Card No: ");
							cardNo = readInteger(false);
							if (cardNo != INVALID_INPUT) {
								String expDate = null;
								while (expDate == null) {
									System.out.println("Enter the Credit Card Expiration Date in format mm/yy: ");
									expDate = readLine().trim();
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
        int rid = INVALID_INPUT;
        while (rid == INVALID_INPUT) {
            System.out.println("Please enter the rental id: ");
            rid = readInteger(false);
            if (delegate.checkRentalExists(rid)) {
                int tankFull = INVALID_INPUT;
                while (true) {
                    System.out.println("Enter 1 if the tank was full, 0 otherwise: ");
                    tankFull = readInteger(false);
                    if (tankFull == 1 || tankFull == 0) {
                        break;
                    }
                    System.out.println(WARNING_TAG + "You are allowed to enter 1 or 0 only");
                }
                int odometer = INVALID_INPUT;
                while (odometer == INVALID_INPUT) {
                    System.out.println("Enter odometer value: ");
                    odometer = readInteger(false);
                }
                String rtnDateTime = null;
                while (rtnDateTime == null) {
                    System.out.println("Enter return date in format yyyy-mm-dd: ");
                    rtnDateTime = readLine().trim();
                }
                rtnDateTime = rtnDateTime + " " + "00:00:00";
                Timestamp rtnDateTime_timestamp = Timestamp.valueOf(rtnDateTime);

                delegate.insertReturn(rid, rtnDateTime_timestamp, odometer, tankFull);
            } else {
                System.out.println(WARNING_TAG + " No currently rented vehicle with rental id: " + rid + " found");
            }
        }


    }

    private void handleDailyRentals() {
		// TODO
    }

    private void handleDailyRentalsByBranch() {
        //TODO
    }

    private void handleDailyReturns() {
		System.out.print("Enter date for returns report (YYYY-MM-DD): ");
		String date = readLine().trim();
		while (!validateDate(date)) {
			System.out.print("Enter date for returns report (YYYY-MM-DD): ");
			date = readLine().trim();
		}
		delegate.printDailyReturns(date);
    }

    private void handleDailyReturnsByBranch() {
		System.out.print("Enter date for returns report (YYYY-MM-DD): ");
		String date = readLine().trim();
		while (!validateDate(date)) {
			System.out.print("Enter date for returns report (YYYY-MM-DD): ");
			date = readLine().trim();
		}
		String[] branch = getBranch();
		delegate.printDailyReturnsByBranch(branch[1], branch[0], date);
    }

    private void goBackToMainMenu() {
        showMainMenu(delegate);
    }
	
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

	private long readLong(boolean allowEmpty) {
		String line = null;
		long input = INVALID_INPUT;
		try {
			line = bufferedReader.readLine();
			input = Long.parseLong(line);
		} catch (IOException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		} catch (NumberFormatException e) {
			if (allowEmpty && line.length() == 0) {
				input = EMPTY_INPUT;
			} else {
				System.out.println(WARNING_TAG + " Your input was not an long");
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
