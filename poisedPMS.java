import java.sql.*;
import java.util.*;


/**
 * This is a main class of the program
 */
public class poisedPMS {


/**
 * This is main method
 * <br>
 * Main method of the program allows user to create, edit and finalize projects using databases
 * @param args displays project details on the console and editing options available to the user
 * @throws SQLException exception
 */
	public static void main(String[] args) throws SQLException {

		try {
			// Connecting to the database
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/poisepms",
					"otheruser", "swordfish");

			Statement statement = connection.createStatement();

			Scanner input = new Scanner(System.in);

			int userChoice;
			boolean exit = false;
			

			System.out.println("\nWelcome to project management system");

			while (exit == false) {
				

				// Printing out options available to user and constructing the body of the program based on the users' choice of action
				System.out.println("\nUser menu:" + "\n1. View all projects" + "\n2. Edit project" + "\n3. Add project"
						+ "\n4. View outstanding projects" + "\n5. View overdue projects" + "\n6. Exit the program");
				userChoice = input.nextInt();
				input.nextLine();

				// Printing all projects from the database
				if (userChoice == 1) {

					printAllFromTable(statement);

				}

				else if (userChoice == 2) {

					System.out.println("Enter the project number to edit: ");
					String prjEdit = input.nextLine();
					

					while (true) {

						// Displaying which elements of the project user can edit
						System.out.println("\nWhat would you like to edit?");
						System.out.println("1 - Deadline");
						System.out.println("2 - Paid To Date");
						System.out.println("3 - Finalized");
						System.out.println("4 - Architect");
						System.out.println("5 - Contractor");
						System.out.println("6 - Customer");
						System.out.println("7 - Back to User Menu");

						int menuOption = input.nextInt();
						input.nextLine();

						// Changing deadline of the project by interacting with the database
						if (menuOption == 1) {

							System.out.println("Please enter the new deadline (YYYYMMDD): ");
							String newDeadline = input.nextLine();

							// Sending update query to the database to perform the action
							statement.executeUpdate("UPDATE project SET deadline='" + newDeadline + "' WHERE prjNumber = '" + prjEdit + "'");
							
							System.out.println("\nDeadline of the project has been succesfully updated\n");
							

						}
						// Changing paid to date by interacting with the database
						else if (menuOption == 2) {
							System.out.print("Please enter new amount for Paid To Date: ");
							Float newPaidToDate = input.nextFloat();

							// Sending update query to the database to perform the action
							statement.executeUpdate("UPDATE project SET paidToDate='" + newPaidToDate + "' WHERE prjNumber = '" + prjEdit + "'");
							System.out.print("\nPaid to Date for the project has been succesfully updated\n");

						}

						// Finalizing the project by changing status
						else if (menuOption == 3) {

							// Sending update query to the database to perform the action
							statement.executeUpdate("UPDATE project SET prjFinalized='Yes' WHERE prjNumber = '" + prjEdit + "'");

							System.out.print("Please enter the date of finalization (YYYYMMDD): ");
							String newDateFinalized = input.nextLine();

							statement.executeUpdate("UPDATE project SET dateFinalized='" + newDateFinalized + "' WHERE prjNumber = '" + prjEdit + "'");

							System.out.print("\nProject has been succesfully finalized\n");
							

							// In order to generate invoice and calculate outstanding amount to be paid extracting total fee and paid to date
							// information from the database for a chosen project
							ResultSet calc = statement.executeQuery("SELECT * FROM project WHERE prjNumber = '" + prjEdit + "'");
							
							if (calc.next()) {
								
								// Defining variables for calculations
								float fee = calc.getFloat("totalFee");
								float paid = calc.getFloat("paidToDate");
								float Difference = fee - paid;
								
								
								if (Difference == 0) {

									System.out.println("Invoice will not be generated, customer paid full amount");
									
								} else {
									
								System.out.println("\n*****Invoice******");

								ResultSet result = statement.executeQuery("SELECT * FROM project WHERE prjNumber = '" + prjEdit + "'");
								
								if (result.next()) {

									// Getting information about the customer for selected project to generate invoice
									String customer = result.getString("customer");
	
									ResultSet result1 = statement.executeQuery("SELECT * FROM person WHERE fullName = '" + customer + "'");
									
									if (result1.next()) {
	
										System.out.println(result1.getString("fullname") + "\n" + result1.getString("personPhoneNumber") + "\n" + result1.getString("personEmail") + "\n" + result1.getString("personAddress"));
		
										System.out.println("Amount outstanding R" + Difference);
									}
								}
							}
						}
						}
						// Editing details of architect, contractor or customer by using editPerson method
						else if (menuOption == 4) {

							ResultSet result = statement.executeQuery("SELECT * FROM project WHERE prjNumber = '" + prjEdit + "'");
							
							if(result.next()) {

								String person = result.getString("architect");
	
								editPerson(statement, input, person);
							}

						} else if (menuOption == 5) {

							ResultSet result = statement.executeQuery("SELECT * FROM project WHERE prjNumber = '" + prjEdit + "'");
							
							if(result.next()) {

								String person = result.getString("contractor");
	
								editPerson(statement, input, person);
							}

						} else if (menuOption == 6) {

							ResultSet result = statement.executeQuery("SELECT * FROM project WHERE prjNumber = '" + prjEdit + "'");
							
							if(result.next()) {

								String person = result.getString("customer");
	
								editPerson(statement, input, person);
							}

						} else if (menuOption == 7) {
							break;
							

						} else {
							System.out.println("\nPlease enter correct menu option.");

						}
					}
				}

				// Adding new projects, asking user for input to populate database, information about project is written into project
				// table and information about people is written to person table
				else if (userChoice == 3) {

					System.out.println("Please enter the details of the new project.");
					System.out.println("Project Number: ");
					String prjNumber = input.nextLine();

					System.out.println("Project Name: ");
					String prjName = input.nextLine();

					System.out.println("Project Type: ");
					String prjType = input.nextLine();

					System.out.println("Project Address: ");
					String prjAddress = input.nextLine();

					System.out.println("Erf Number: ");
					String erfNumber = input.nextLine();

					// Make sure user input numbers by inserting <try/catch> method
					float totalFee = 0;
					while (true) {
						try {
							System.out.println("Total Fee: ");
							totalFee = input.nextFloat();
							input.nextLine();
							break;
						} catch (Exception e) {
							System.out.println("Please only enter numbers");
							input.next();

						}
					}
					// Make sure user input numbers by inserting <try/catch> method
					float paidToDate = 0;
					while (true) {
						try {
							System.out.println("Paid to Date: ");
							paidToDate = input.nextFloat();
							input.nextLine();
							break;
						} catch (Exception e) {
							System.out.println("Please only enter numbers");
							input.next();
						}
					}

					System.out.println("Deadline (YYYYMMDD): ");
					String deadline = input.nextLine();

					String prjFinalized = "No";
					String dateFinalized = "";

					System.out.println("PLease enter details of the architect: ");
					String architectRole = "architect";

					System.out.println("Name: ");
					String architectName = input.nextLine();

					System.out.println("Surname: ");
					String architectSurname = input.nextLine();

					System.out.println("Telephone: ");
					String architectPhoneNumber = input.nextLine();

					System.out.println("Email: ");
					String architectEmail = input.nextLine();

					System.out.println("Address: ");
					String architectAddress = input.nextLine();

					String architectFullName = architectName + " " + architectSurname;

					statement.executeUpdate("INSERT INTO person VALUES('" + architectFullName + "','" + architectRole + "','" + architectPhoneNumber + "','" + architectEmail + "','" + architectAddress + "')");

					System.out.println("PLease enter details of the contractor: ");
					String contractorRole = "contractor";

					System.out.println("Name: ");
					String contractorName = input.nextLine();

					System.out.println("Surname: ");
					String contractorSurname = input.nextLine();

					System.out.println("Telephone: ");
					String contractorPhoneNumber = input.nextLine();

					System.out.println("Email: ");
					String contractorEmail = input.nextLine();

					System.out.println("Address: ");
					String contractorAddress = input.nextLine();

					String contractorFullName = contractorName + " " + contractorSurname;

					statement.executeUpdate("INSERT INTO person VALUES('" + contractorFullName + "','" + contractorRole + "','" + contractorPhoneNumber + "','" + contractorEmail + "','" + contractorAddress + "')");

					System.out.println("PLease enter details of the customer: ");
					String customerRole = "customer";

					System.out.println("Name: ");
					String customerName = input.nextLine();

					System.out.println("Surname: ");
					String customerSurname = input.nextLine();

					System.out.println("Telephone: ");
					String customerPhoneNumber = input.nextLine();

					System.out.println("Email: ");
					String customerEmail = input.nextLine();

					System.out.println("Address: ");
					String customerAddress = input.nextLine();

					String customerFullName = customerName + " " + customerSurname;

					statement.executeUpdate("INSERT INTO person VALUES('" + customerFullName + "','" + customerRole + "','" + customerPhoneNumber + "','" + customerEmail + "','" + customerAddress + "')");

					// If user did not insert name of the project the project type and customers' surname is used to create project name
					if (prjName.equals("")) {
						prjName = prjType + " " + customerSurname;
					}

					statement.executeUpdate("INSERT INTO project VALUES('" + prjNumber + "','" + prjName + "','" + prjType + "','" + prjAddress + "','" + erfNumber + "'," + totalFee + "," + paidToDate + ",'" + deadline + "','" + prjFinalized + "','" + dateFinalized + "','" + architectFullName + "','" + contractorFullName + "','" + customerFullName + "')");

					System.out.println("\nNew project has been succesfully created.");
					
					
					// Extracting information from the database about projects that has not been finalized yet
				} else if (userChoice == 4) {

					System.out.println("\nProjects Not Yet Finalised: \n");

					ResultSet results = statement.executeQuery("SELECT * FROM project WHERE prjFinalized = 'No'");

					// Printing out projects' details
					while (results.next()) {
						System.out.println("\nProject Number: " + results.getString("prjNumber") + "\nProject Name: "
								+ results.getString("prjName") + "\nProject Type: " + results.getString("prjType")
								+ "\nProject Address: " + results.getString("prjAddress") + "\nerfNumber: "
								+ results.getString("erfNumber") + "\nTotal Fee: " + results.getFloat("totalFee")
								+ "\nPaid to Date: " + results.getFloat("paidToDate") + "\nDeadline: "
								+ results.getString("deadline") + "\nProject Finalized: "
								+ results.getString("prjFinalized") + "\nDate Finalized: "
								+ results.getString("dateFinalized") + "\nArchitect: " + results.getString("Architect")
								+ "\nContractor: " + results.getString("contractor") + "\nCustomer: "
								+ results.getString("customer"));

					}

					// Extracting information about projects that are not completed and are past due date
				} else if (userChoice == 5) {

					boolean noProjectsFound = true;
					
					System.out.println("\nOverdue Projects:");

					ResultSet checkDate = statement.executeQuery("SELECT * FROM project WHERE prjFinalized = 'No' and deadline < NOW()");

					if (checkDate.next()) {

						// Printing out information about projects that adhere to required criteria
						while (checkDate.next()) {
							System.out.println("\nProject Number: " + checkDate.getString("prjNumber")
									+ "\nProject Name: " + checkDate.getString("prjName") + "\nProject Type: "
									+ checkDate.getString("prjType") + "\nProject Address: "
									+ checkDate.getString("prjAddress") + "\nerfNumber: "
									+ checkDate.getString("erfNumber") + "\nTotal Fee: "
									+ checkDate.getFloat("totalFee") + "\nPaid to Date: "
									+ checkDate.getFloat("paidToDate") + "\nDeadline: "
									+ checkDate.getString("deadline") + "\nProject Finalized: "
									+ checkDate.getString("prjFinalized") + "\nDate Finalized: "
									+ checkDate.getString("dateFinalized") + "\nArchitect: "
									+ checkDate.getString("Architect") + "\nContractor: "
									+ checkDate.getString("contractor") + "\nCustomer: "
									+ checkDate.getString("customer"));
						}

						noProjectsFound = false;
					}

					else if (noProjectsFound) {
						System.out.println("No overdue projects has been found");

					}
				}

				else if (userChoice == 6) {
					System.out.println("\nThank you, Goodbye.");
					exit = true;

				} else {
					System.out.print("\nPlease enter valid menu option");
				}
			}
			// Closing database
			statement.close();
			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


/**
 * This method is used to print information about all projects in the project table from the database
 * @param statement database query
 * @throws SQLException exception
 */
	public static void printAllFromTable(Statement statement) throws SQLException {

		ResultSet results = statement.executeQuery("SELECT * FROM project");

		while (results.next()) {
			System.out.println("\n\nProject Number: " + results.getString("prjNumber") + "\nProject Name: "
					+ results.getString("prjName") + "\nProject Type: " + results.getString("prjType")
					+ "\nProject Address: " + results.getString("prjAddress") + "\nerfNumber: "
					+ results.getString("erfNumber") + "\nTotal Fee: " + results.getFloat("totalFee")
					+ "\nPaid to Date: " + results.getFloat("paidToDate") + "\nDeadline: "
					+ results.getString("deadline") + "\nProject Finalized: " + results.getString("prjFinalized")
					+ "\nDate Finalized: " + results.getString("dateFinalized") + "\nArchitect: "
					+ results.getString("Architect") + "\nContractor: " + results.getString("contractor")
					+ "\nCustomer: " + results.getString("customer"));

		}
	}


/**
 * This method is used to edit information about people that are involved in the particular project
 * @param statement database query
 * @param input users' input
 * @param person involved in the project
 * @throws SQLException exception
 */
	private static void editPerson(Statement statement, Scanner input, String person) throws SQLException {

		System.out.println("Please enter new phone number: ");
		String newPhoneNumber = input.nextLine();

		System.out.println("Please enter new email address: ");
		String newEmail = input.nextLine();

		System.out.println("Please enter new address: ");
		String newAddress = input.nextLine();

		statement.executeUpdate("UPDATE person SET personPhoneNumber='" + newPhoneNumber + "', personEmail='" + newEmail
				+ "', personAddress='" + newAddress + "' WHERE fullName = '" + person + "'");
		System.out.println("\nContact details has been succefully updated");
	}
}


// ***References***

// Jayaram, P. 31 October 2018. SQL date format Overview; DateDiff SQL function, DateAdd SQL function and more. Retrieved 15 March 2021, from https://www.sqlshack.com/sql-date-format-overview-datediff-sql-function-dateadd-sql-function-and-more/
// ResultSet exception - before start of result set. Retrieved 15 March 2021, from https://stackoverflow.com/questions/2120255/resultset-exception-before-start-of-result-set
// MySQL error: Unknown column in 'where clause'. Retrieved 15 March 2021, from https://stackoverflow.com/questions/10142583/mysql-error-unknown-column-in-where-clause/10142614