package pl.jklimek.hex2decBackend.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JDBC model
 * It is meant to be used as part of MVC scheme converter.
 * @author Jakub Klimek
 * @version 8
 */

@MVCImplementation(MVCImplementation.Component.MODEL)
public class JdbcJavaConverter {
    /**
     * Constructs converter with provided parameters.
     * @param maxQueueSize maximum number of conversion pairs in queue
     * @param url database url
     * @param user database user
     * @param password database password
     * @throws OutOfBoundsException if selected size and maximum size are wrong
     * @throws SQLException when sql error occurs
     */
    @SuppressWarnings("Convert2Diamond")
    public JdbcJavaConverter(int maxQueueSize, String url, String user, String password) throws OutOfBoundsException, SQLException{
        this.maxQueueSize = maxQueueSize;            
           dbConnection = DriverManager.getConnection(url, user, password); 
        //check if table exists
        try{
            Statement st = dbConnection.createStatement();
            st.executeQuery("SELECT * FROM DATA");
            
        } catch (SQLException ex) {
            //if table doesn't exist, create it
            if(ex.getSQLState().equals("42X05")){
                try {
                    Statement st = dbConnection.createStatement();
                    st.executeUpdate("CREATE TABLE data (id int NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), deci int, hex varchar(15))");
                    dbConnection.commit();
                    this.queueSize = 0;
                    this.setQueueSize(1);
                } catch (SQLException ex1) {
                    //won't happen
                }
            }
        }
        
        
        this.getRowCount();
    }

    /**
     * Implemented interface method to return max queue size.
     * @return max queue size
     */
    public int getMaxQueueSize() {
        return maxQueueSize;
    }
    /**
     * Implemented interface's Dec to Hex cnversion method.
     * 
     */
    public void convertToHex() {
        try {
            Statement st = dbConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = st.executeQuery("SELECT * FROM DATA");
            while(rs.next()){
                rs.updateString("hex", Long.toHexString(rs.getInt("deci")));
                rs.updateRow();
            }
            rs.close();
            } catch (SQLException ex1) {
                //just not doing conversion
            }
    }
    /**
     * Implemented interface's Hex to Dec cnversion method.
     */
    public void convertToDec() {
        try {
            Statement st = dbConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = st.executeQuery("SELECT * FROM DATA");
            while(rs.next()){
                rs.updateLong("deci",Long.parseLong(rs.getString("hex"),16));
                rs.updateRow();
            }
            rs.close();
            } catch (SQLException ex1) {
            }
    }
    /**
     * Implemented interface's method to return numbers of conversion pairs in queue.
     * @return number of conversion pairs in queue
     */
    public int getQueueSize() {
        return queueSize; 
    }
    /**
     * Implemented interface's method to change queue size.
     * @param size new queue size
     * @throws OutOfBoundsException if desired size is lower or higher than allowed
     * @throws SQLException when sql error occurs
     * 
     */
    final public void setQueueSize(int size) throws OutOfBoundsException, SQLException {
        if(size == queueSize && size <= maxQueueSize){
            return;
        } else if(size > maxQueueSize || size <= 0) {
            throw new OutOfBoundsException();
        } 
        resizeLists(size);       
    }
    /**
     * Implemented interface's method to change queue size.
     * @param size new queue size
     * @throws OutOfBoundsException if desired size is lower or higher than allowed
     * @throws NumberFormatException if provided number value is not valid number
     * @throws SQLException when sql error occur
     */
    final public void setQueueSize(String size) throws OutOfBoundsException, NumberFormatException, SQLException {
        int sizeN = Integer.parseInt(size);
        setQueueSize(sizeN);       
    }
    /**
     * Implemented interface's method to set hex value of desired conversion pair.
     * @param index index of conversion pair
     * @param value new hex value
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws BadValueException if provided value couldn't be set
     * @throws SQLException when sql error occur
     */
    public void setHexValue(int index, String value) throws OutOfBoundsException, BadValueException, SQLException{
        if(isHex(value)){
            Statement st = dbConnection.createStatement();
            ResultSet rs = st.executeQuery("SELECT deci, hex FROM DATA where id=" + index);
            if(!rs.next()){
                 throw new OutOfBoundsException();
             }
            else{
                st.executeUpdate("update data set hex='" + value + "' where id=" + index);
            }
            
        }
        else{
            throw new BadValueException();
        }   
    }
    /**
     * Implemented interface's method to set hex value of desired conversion pair.
     * @param index index of conversion pair
     * @param value new hex value
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws NumberFormatException if provided number value is not valid number
     * @throws BadValueException if provided value couldn't be set
     * @throws SQLException when sql error occur
     */
    public void setHexValue(String index, String value) throws OutOfBoundsException, BadValueException, NumberFormatException, SQLException{
        int indexN = Integer.parseInt(index);
        setHexValue(indexN, value);
    }
    /**
     * Implemented interface's method to set decimal value of desired conversion pair.
     * @param index index of conversion pair
     * @param value new decimal value
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws BadValueException if provided value couldn't be set
     * @throws SQLException when sql error occur
     */
    public void setDecValue(int index, long value) throws OutOfBoundsException, BadValueException, SQLException{
        if(value < 0){ //this model allows conversion on numbers >= 0
            throw new BadValueException();
        }

            Statement st = dbConnection.createStatement();
            ResultSet rs = st.executeQuery("SELECT deci, hex FROM DATA where id=" + index);
            if(!rs.next()){
                 throw new OutOfBoundsException();
             }
            else{
                st.executeUpdate("update data set deci=" + value + " where id=" + index);
            }

    }
    /**
     * Implemented interface's method to set decimal value of desired conversion pair.
     * @param index index of conversion pair
     * @param value new decimal value
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws BadValueException if provided value couldn't be set
     * @throws SQLException when sql error occur
     */
    public void setDecValue(String index, String value) throws OutOfBoundsException, BadValueException, NumberFormatException, SQLException {
          int indexN = Integer.parseInt(index); 
          long valueL = Long.parseLong(value);
          setDecValue(indexN, valueL);
    }
    /**
     * Implemented interface's method that returns desired conversion pair.
     * @param index index of desired conversion pair
     * @return conversion pair as ConvertedPair object
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws SQLException when sql error occur
     */
    public ConvertedPair getPair(int index) throws OutOfBoundsException, SQLException{
            Statement st = dbConnection.createStatement();
            ResultSet rs = st.executeQuery("SELECT deci, hex FROM DATA where id==" + index);
            if(!rs.next()){
                 throw new OutOfBoundsException();
             }
            return new ConvertedPair(rs.getString("hex"), rs.getInt("deci"));
    }
    /**
     * Implemented interface's method that returns desired conversion pair.
     * @param index index of desired conversion pair
     * @return conversion pair as ConvertedPair object
     * @throws OutOfBoundsException if desired pair doesn't exist
     * @throws SQLException when sql error occur
     */
    public ConvertedPair getPair(String index) throws OutOfBoundsException,NumberFormatException, SQLException{
        int indexN = Integer.parseInt(index);
        return getPair(indexN);
    }
    /**
     * Implemented interface's method that returns all conversion pairs in queue.
     * @return Array of ConvertedPair objects representing queue elements
     * @throws SQLException when sql error occur
     */
    public ConvertedPair[] getAllPairs() throws SQLException {
        int size = 0;
            Statement st = dbConnection.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(id) as ile FROM DATA");
            if(!rs.next()){
                //failsafe display
                ConvertedPair[] pairs = new ConvertedPair[1];
                pairs[0] = new ConvertedPair("0", 0);
                 return pairs;
             }
            size = rs.getInt("ile");
            ConvertedPair[] pairs = new ConvertedPair[size];
            rs = st.executeQuery("SELECT deci, hex FROM DATA");
            int index = 0;
            while(rs.next()){
                pairs[index++] = new ConvertedPair(rs.getString("hex"), rs.getInt("deci"));
            }
            return pairs;

    }
    /**
     * Internal method responsible for resizing internal queue structures.
     * @param size new queue size
     * @throws SQLException when sql error occurs
     */
    private void resizeLists(int size)throws SQLException{

            if(queueSize > size){
                Statement st = dbConnection.createStatement();
                st.executeUpdate("DELETE FROM data WHERE id >=" + size);
                st.executeUpdate("ALTER TABLE data ALTER COLUMN id RESTART WITH " + (size));
            }
            
        if(size > queueSize){
            for(int index = queueSize; index < size; ++index){
                    Statement st = dbConnection.createStatement();
                    st.executeUpdate("INSERT INTO data(deci, hex) VALUES (0,'0')");
                    dbConnection.commit();
            }
        }
        queueSize = size;
    }
    /**
     * Internal method to check if provided String is hex number.
     * It also checks if provided hex number fits in Long variable.
     * @param hex hex humber to check
     * @return true if provided number is acceptable, false otherwise
     */
    private boolean isHex(String hex){
        if(hex == null)
            return false;
        Pattern hexPattern = Pattern.compile("[a-fA-F0-9]{1,16}");
        Matcher hexMatcher = hexPattern.matcher(hex);
        if(hexMatcher.matches()){
            if(hex.length() == 16){
                //ensure that hex number isn't bigger than long max value
                if(!(hex.charAt(0) < '8'))
                    return false;
            }
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * Private method to synchronize with database
     * @throws SQLException when sql error occurs
     */
    private void getRowCount() throws SQLException{

            Statement st = dbConnection.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(id) as ile FROM DATA");
            if(!rs.next()){
                queueSize = 0;
                return;
             }
            queueSize = rs.getInt("ile");
    }
    /**
     * Field contains information how many conversion pairs are currently in queue.
     */
    private int queueSize;
    /**
     * Max allowed queue size in this particular implementation's object.
     */
    private int maxQueueSize;
    /**
     * Internal field representing connection to database
     */
    private Connection dbConnection;

    
}
