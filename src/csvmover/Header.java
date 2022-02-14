package csvmover;

/**
 * Agregates column header data
 */
public class Header
{
	/**
	 * Creates new header
	 * 
	 * @param name Header name
	 */
	public Header(String name)
	{
		this.name = name;
	}
	
	/**
	 * Column name
	 */
	public String name;
	
	/**
	 * Column source index
	 */
	public Integer from = null;
}
