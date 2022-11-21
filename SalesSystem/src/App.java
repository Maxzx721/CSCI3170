import java.sql.*;
import java.util.Scanner;

class App {
    public static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db19?autoReconnect=true&useSSL=false";
    public static String dbUsername = "Group19";
    public static String dbPassword = "CSCI3170";

    public static void main(String[] args) throws Exception {
        try { 
            Class.forName("com.mysql.jdbc.Driver"); 
        } catch(Exception x) { 
            System.err.println("Unable to load the driver class!"); 
        }
        Connection conn = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
        Statement stmt = conn.createStatement();

        menu();
    }

    public static void menu() {

        Scanner in = new Scanner(System.in);
        
        System.out.println("Welcome to sales system!\n");
        System.out.println("-----Main menue-----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Operations for administrator");
        System.out.println("2. Operations for salesperson");
        System.out.println("3. Operations for manager");
        System.out.println("4. Exit this program");
        System.out.print("Enter Your Choice: ");
        int choice = in.nextInt();
        in.close();

        switch (choice) {
            case 1:
                administrator();

            case 2:
                salesPerson();

            case 3:
                manager();
        
            default:
                return;
        }
        
    }

    public static void administrator() {
        
        Scanner in = new Scanner(System.in);
        
        System.out.println("-----Operations for administrator menu-----");
        System.out.println("What kinds of operation would you like to perorm?");
        System.out.println("1. Create all tables");
        System.out.println("2. Delate all tables");
        System.out.println("3. Load from datafile");
        System.out.println("4. Show content of a table");
        System.out.println("5. Return to the main menu");
        System.out.print("Enter Your Choice: ");

        int choice = in.nextInt();
        in.close();

        switch (choice) {
            case 1:

                System.out.println("Processing...Done! Database is initialized!");
                break;

            case 2:

                System.out.println("Processing...Done! Database is removed!");
                break;

            case 3:
                Scanner in = new Scanner(System.in);
                System.out.print("\nType in the Source Data Folder Path: ");
                String path = in.nextLine();

                in.close();
                System.out.println("Processing...Done! Data is inputted to the database!");
                break;

            case 4:
                Scanner in = new Scanner(System.in);
                System.out.print("Whih table would you like to show: ");
                String table = in.nextLine();

                in.close();
                break;
        
            default:
                menu();
        }

    }

    public static void salesPerson() {

        Scanner in = new Scanner(System.in);
        
        System.out.println("-----Operations for salesperson menu-----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Search for parts");
        System.out.println("2. Sell a part");
        System.out.println("3. Return to the main menu");
        System.out.print("Enter Your Choice: ");

        int choice = in.nextInt();
        in.close();

        switch (choice) {
            case 1:
                Scanner in = new Scanner(Systme.in);

                System.out.println("Choose the Search criterion:\n1. Part Name\n2. Manufacturer Name");
                System.out.print("Choose the search criterion: ");
                choice = in.nextInt();

                System.out.print("Choose the search criterion: ");
                String name = in.nextLine();
                break;
            
            case 2:
                System.out.print("Enter The Part ID: ");
                choice = in.nextInt();
                System.out.print("Enter The Salesperon ID: ");
                int choice1 = in.nextInt();

                Systme.out.println("");
                break;
        
            default:
                menu();
        }

    }
    
    public static void manager() {

        Scanner in = new Scanner(System.in);
        
        System.out.println("-----Operations for manager menu-----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. List all salespersons");
        System.out.println("2. Count the no. of sales record of each salesperson under a specific range on years of experience");
        System.out.println("3. Show the total sales value of each manufacturer");
        System.out.println("4. Show the N most popular part");
        System.out.println("5. Return to the main menu");
        System.out.print("Enter Your Choice: ");

        int choice = in.nextInt();
        in.close();

        switch (choice) {
            case 1:
                System.out.println("Choose ordering:\n1. By ascending order\n2. By descending order");
                System.out.print("Choose the list ordering: ");
                choice = in.nextInt();

                break;
            
            case 2:
                System.out.print("Type in the lower bound for years of experience: ");
                choice = in.nextInt();
                System.out.print("Type in the upper bound for years of experience: ");
                int choice1 = in.nextInt();

                System.out.println("End of Query");
                break;
        
            case 3:

                System.out.println("End of Query");
                break;
        
            case 4:
                System.out.print("Type in the number of parts: ");
                choice = in.nextInt();
                
                System.out.println("End of Query");
                break;
        
            default:
                menu();
        }
    }

}
