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
            int rows = statement.executeUpdate();

            //error checking
            if (rows == 0) {
                throw new SQLException("Creating member failed, no rows affected.");
            }
            
            //retrieve the generated member ID
            ResultSet pKeys = statement.getGeneratedKeys();
            if (pKeys.next()) {
                int memberId = pKeys.getInt(1);
                
                // create profile for member
                PreparedStatement pStatement = connection.prepareStatement("INSERT INTO profiles (member_id, mem_weight, mem_height, user_name, user_pass) VALUES (?, ?, ?, ?, ?)");
                
                pStatement.setInt(1, memberId);
                pStatement.setInt(2, weight); 
                pStatement.setInt(3, height); 
                pStatement.setString(4, user);
                pStatement.setString(5, pass); 
                pStatement.executeUpdate();
                
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

            //statement to get name of member
            PreparedStatement nameState = connection.prepareStatement("SELECT first_name, last_name FROM members WHERE member_id = ?");
            nameState.setInt(1, memberId);
            ResultSet nameRS = nameState.executeQuery();
            
            String fname = "";
            String lname = "";
            if (nameRS.next()) {
                fname = nameRS.getString("first_name");
                lname = nameRS.getString("last_name");
            }
            
            System.out.println("Dashboard for Member: " + fname + " " + lname);
            System.out.println();

            //get goals from the goals table using the profile id of the member id
            PreparedStatement goalState = connection.prepareStatement("SELECT * FROM goals WHERE profile_id IN (SELECT profile_id FROM profiles WHERE member_id = ?)");
            goalState.setInt(1, memberId);
            ResultSet goalRS = goalState.executeQuery();
            
            //display exercise goals
            System.out.println("Exercise, Goals, and Current Achieved:");
            while (goalRS.next()) {
                String exercise = goalRS.getString("exercise");
                int goalWeight = goalRS.getInt("goal_weight");
                int goalReps = goalRS.getInt("goal_reps");
                int goalSets = goalRS.getInt("goal_sets");
                int currentWeight = goalRS.getInt("current_weight");
                int currentReps = goalRS.getInt("current_reps");
                int currentSets = goalRS.getInt("current_sets");
                
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
            PreparedStatement statsState = connection.prepareStatement("SELECT * FROM profiles WHERE member_id = ?");
            statsState.setInt(1, memberId);
            ResultSet statsRS = statsState.executeQuery();
            
            //display 
            System.out.println("Health Statistics:");
            while (statsRS.next()) {
                int weight = statsRS.getInt("mem_weight");
                int height = statsRS.getInt("mem_height");
                System.out.println("Weight: " + weight + " lbs");
                System.out.println("Height: " + height + " cm");
                System.out.println();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /*
    public static void equipmentMaintenance(int equipment_id) {
        try{
            Statement statement = connection.createStatement();
            String insertSQL = "UPDATE equipment SET last_maintained_date=? WHERE equipment_id=?";
            // Creating a prepared statement for security
            try(PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setDate(1, new java.sql.Date(System.currentTimeMillis()));
                pstmt.setInt(2, equipment_id);
                pstmt.executeUpdate();
                System.out.println("Updated the equipment's last maintained by date");
                pstmt.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (SQLException e) {}
    }

    public static void trainerSchedule(int trainer_id) {
        try{
            Statement statement = connection.createStatement();
            String insertSQL = "SELECT * FROM trainers WHERE trainer_id=?";
            // Creating a prepared statement for security
            try(PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setInt(1, trainer_id);
                pstmt.executeQuery();
                System.out.println("Here are the trainer's availabilites: ");

                ResultSet results = pstmt.getResultSet();

                // Prints each attribute of a student seperated by a tab
                while (results.next()) {
                    System.out.print(results.getString("availability_id") + "\t");
                    System.out.print(results.getString("trainer_id") + "\t");
                    System.out.print(results.getString("start_time") + "\t");
                    System.out.print(results.getString("end_time") + "\t");
                    System.out.println(results.getString("week_day"));
                }
                results.close();

                pstmt.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        } catch (SQLException e) {}

        Scanner scanner = new Scanner(System.in);

        System.out.println("Select the date you wish to change your availability for:");
        System.out.println("[1] Sunday");
        System.out.println("[2] Monday");
        System.out.println("[3] Tuesday");
        System.out.println("[4] Wednesday");
        System.out.println("[5] Thursday");
        System.out.println("[6] Friday");
        System.out.println("[7] Saturday");
        int day = scanner.nextInt();

        String weekday = "";

        switch (day) {
            case 1:
                weekday = "Sunday";
                break;
            case 2:
                weekday = "Monday";
                break;
            case 3:
                weekday = "Tuesday";
                break;
            case 4:
                weekday = "Wednesday";
                break;
            case 5:
                weekday = "Thursday";
                break;
            case 6:
                weekday = "Friday";
                break;
            case 7:
                weekday = "Saturday";
                break;
        }
        updateTrainerSchedule(trainer_id, weekday);
    }

    public static void updateTrainerSchedule(int trainer_id, String weekday) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the new start time in the following format: (hh:mm)");
        String start = scanner.nextLine();

        System.out.println("Enter the new end time in the following format: (hh:mm)");
        String end = scanner.nextLine();

        try{
            Statement statement = connection.createStatement();
            String insertSQL = "UPDATE availability SET start_time=?, end_time=? WHERE trainer_id=? AND weekday=?";
            // Creating a prepared statement for security
            try(PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setTime(1, new java.sql.Time(start));
                pstmt.setTime(2, new java.sql.Time(end));
                pstmt.setInt(3, trainer_id);
                pstmt.setString(4, weekday);
                pstmt.executeUpdate();
                System.out.println("Updated the trainer's availability");
                pstmt.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (SQLException e) {}
    }
     */
}
