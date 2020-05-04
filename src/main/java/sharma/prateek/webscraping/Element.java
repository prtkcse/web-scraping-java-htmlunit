package sharma.prateek.webscraping;

public class Element {
	
	private String name;
	private String url;
	private String contact;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("********************Element****************\n");
		result.append("Name : "+getName()+"\n");
		result.append("Contact : "+getContact()+"\n");
		result.append("URL : "+getUrl()+"\n");
		result.append("*******************************************\n");
		result.append("\n");
		
		return result.toString();		
	}
	

}
