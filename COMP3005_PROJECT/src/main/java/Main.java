import javax.xml.transform.Result;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

//Main Class
public class Main
{
    //Global variables used to access db
    static final String url = "jdbc:postgresql://localhost:5432/project_db";
    static final String user = "postgres";
    static final String password = "admin";

    private static Connection connection;

    //Main function to call methods
    public static void main(String[] args)
    {
        try
        {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url,user,password);
            
            newMem();
            

        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }

    //function to get user input for new member
    public static void newMem(){

        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter first name:");
        String firstName = scanner.nextLine();
        
        System.out.println("Enter last name:");
        String lastName = scanner.nextLine();
        
        //set fee for new members
        int fee = 30;
        
        System.out.println("Enter registration date (yyyy-mm-dd):");
        String registrationDateStr = scanner.next();
        Date registrationDate = Date.valueOf(registrationDateStr);
        
        System.out.println("Enter weight:");
        int weight = scanner.nextInt();
        
        System.out.println("Enter height:");
        int height = scanner.nextInt();
        
        System.out.println("Enter username:");
        String username = scanner.next();
        
        System.out.println("Enter password:");
        String password = scanner.next();
        
        // Call the function with data
        memberRegistration(firstName, lastName, fee, registrationDate, weight, height, username, password);

    }

    //function for adding student to table
    public static void memberRegistration(String first_name, String last_name, int fee, Date registration_date, int weight, int height, String user, String pass) {
        try {
            // statement sent to db to create new member
            PreparedStatement statement = connection.prepareStatement("INSERT INTO members (first_name, last_name, fee, registration_date) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            
            // setting all the values
            statement.setString(1, first_name);
            statement.setString(2, last_name);
            statement.setInt(3, fee);
            statement.setDate(4, registration_date);
            
            // execute the statement
            int affectedRows = statement.executeUpdate();

            //error checking
            if (affectedRows == 0) {
                throw new SQLException("Creating member failed, no rows affected.");
            }
            
            //retrieve the generated member ID
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int memberId = generatedKeys.getInt(1);
                
                // create profile for member
                PreparedStatement profileStatement = connection.prepareStatement("INSERT INTO profiles (member_id, mem_weight, mem_height, user_name, user_pass) VALUES (?, ?, ?, ?, ?)");
                
                profileStatement.setInt(1, memberId);
                profileStatement.setInt(2, weight); 
                profileStatement.setInt(3, height); 
                profileStatement.setString(4, user);
                profileStatement.setString(5, pass); 
                profileStatement.executeUpdate();
                
                System.out.println("Member registered successfully with member ID: " + memberId);
            } else {
                throw new SQLException("Creating member failed, no ID obtained.");
            }
        } catch (SQLException e) {
            e.printStackTrace();   
        }
    }

}
