package pl.jklimek.hex2decBackend.model;

/**
 * Class representing one pair of converted numbers.
 * @author Jakub Klimek
 * @version 1
 */
public class ConvertedPair {
    /**
     * Construct generic conversion pair (0,0).
     */
    public ConvertedPair(){
        this("0",0);
    }
    /**
     * Construct conversion pair.
     * @param hex hex value
     * @param dec decimal value
     */
    public ConvertedPair(String hex, long dec){
        this.dec = dec;
        this.hex = hex;
    }
    /**
     * Method returns hex value contained inside conversion pair.
     * @return hex value
     */
    public String getHex() {
        return hex;
    }
    /**
     * Method sets hex value contained inside conversion pair.
     * @param hex new hex value
     */
    public void setHex(String hex) {
        this.hex = hex;
    }
    /**
     * Method returns decimal value contained inside conversion pair.
     * @return decimal value
     */
    public long getDec() {
        return dec;
    }
    /**
     * Method sets decimal value contained inside conversion pair.
     * @param dec new decimal value
     */
    public void setDec(long dec) {
        this.dec = dec;
    }
    /**
     * Field containing hex value of number pair.
     */
    private String hex;
    /**
     * Field containing decimal value of number pair.
     */
    private long dec;
}
