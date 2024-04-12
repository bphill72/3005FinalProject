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
            
            //newMem();
            dashboard(1);
            

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

    /*
    //function to update profile weight and height, based on profile id provided
    public static void updateProfile(Integer profile_id, Integer mem_weight, Integer mem_height, Integer goal_id, Integer goal_weight, Integer goal_reps, Integer goal_sets)
    {

        //try and catch for any errors
        try
        {
            Class.forName("org.postgresql.Driver");

            //connecting to db
            Connection connection = DriverManager.getConnection(url, user, password);

            //query for updating student email based on id
            String updateQuery = "UPDATE profiles SET mem_weight = ?, mem_height = ? WHERE profile_id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setInt(1, mem_weight);
            preparedStatement.setInt(2, mem_height);
            preparedStatement.setInt(3, profile_id);
            int updatedRows = preparedStatement.executeUpdate();

            String updateGoalQuery = "UPDATE goals SET goal_weight = ?, goal_reps = ?, goal_sets = ? WHERE profile_id = ? AND goal_id = ?";
            PreparedStatement goalStatement = connection.prepareStatement(updateGoalQuery);
            goalStatement.setInt(1, goal_weight);
            goalStatement.setInt(2, goal_reps);
            goalStatement.setInt(3, goal_sets);
            goalStatement.setInt(4, profile_id);
            goalStatement.setInt(5, goal_id);
            int updatedGoalRows = goalStatement.executeUpdate();

            //checking to see if profile and goal were updated
            if (updatedRows > 0 && updatedGoalRows > 0)
            {
                System.out.println("Profile and Goal updated successfully.");
            }

            else
            {
                System.out.println("Profile with ID " + profile_id + " or Goal with ID " + goal_id + " not found.");
            }
        }

        //catch any exception errors
        catch (SQLException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    */


    public static void dashboard(int memberId) {
        try {
            //get goals from the goals table using the profile id of the member id
            PreparedStatement goalStatement = connection.prepareStatement("SELECT * FROM goals WHERE profile_id IN (SELECT profile_id FROM profiles WHERE member_id = ?)");
            goalStatement.setInt(1, memberId);
            ResultSet goalResultSet = goalStatement.executeQuery();
            
            //display exercise goals
            System.out.println("Exercise, Goals, and Current Achieved:");
            while (goalResultSet.next()) {
                String exercise = goalResultSet.getString("exercise");
                int goalWeight = goalResultSet.getInt("goal_weight");
                int goalReps = goalResultSet.getInt("goal_reps");
                int goalSets = goalResultSet.getInt("goal_sets");
                int currentWeight = goalResultSet.getInt("current_weight");
                int currentReps = goalResultSet.getInt("current_reps");
                int currentSets = goalResultSet.getInt("current_sets");
                
                System.out.println("Exercise: " + exercise);
                System.out.println("Goal Weight: " + goalWeight + " lbs");
                System.out.println("Goal Reps: " + goalReps);
                System.out.println("Goal Sets: " + goalSets);
                System.out.println("Current Weight: " + currentWeight + " lbs");
                System.out.println("Current Reps: " + currentReps);
                System.out.println("Current Sets: " + currentSets);
                System.out.println();
            }
            
            //health statistics
            PreparedStatement statisticsStatement = connection.prepareStatement("SELECT * FROM profiles WHERE member_id = ?");
            statisticsStatement.setInt(1, memberId);
            ResultSet statisticsResultSet = statisticsStatement.executeQuery();
            
            //display 
            System.out.println("Health Statistics:");
            while (statisticsResultSet.next()) {
                int weight = statisticsResultSet.getInt("mem_weight");
                int height = statisticsResultSet.getInt("mem_height");
                System.out.println("Weight: " + weight + " lbs");
                System.out.println("Height: " + height + " cm");
                System.out.println();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
