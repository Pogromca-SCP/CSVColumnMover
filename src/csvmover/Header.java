package csvmover;

/**
 * Contains column header data
 */
public class Header
{	
	/**
	 * Column name
	 */
	public final String name;
	
	/**
	 * Column source index
	 */
	public Integer from;
	
	/**
	 * Creates new header
	 * 
	 * @param n Header name
	 */
	public Header(final String n)
	{
		name = n;
		from = null;
	}
}
