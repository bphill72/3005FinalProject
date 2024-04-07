import javax.xml.transform.Result;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

//Main Class
public class Main
{
    //Global variables used to access db
    static final String url = "jdbc:postgresql://localhost:5432/project_db";
    static final String user = "postgres";
    static final String password = "admin";

    //Main function to call methods
    public static void main(String[] args)
    {
        try
        {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url,user,password);

            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM members");
            ResultSet resultSet = statement.getResultSet();

            while(resultSet.next())
            {
                System.out.println(resultSet.getString("first_name"));
            }
        }

        catch(Exception e)
        {
            System.out.println(e);
        }

    }

}
