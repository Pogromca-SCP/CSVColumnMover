package csvmover;

/**
 * Contains column header data
 */
public class Header
{
	/**
	 * Creates new header
	 * 
	 * @param n Header name
	 */
	public Header(String n)
	{
		name = n;
		from = null;
	}
	
	/**
	 * Column name
	 */
	public final String name;
	
	/**
	 * Column source index
	 */
	public Integer from;
}
