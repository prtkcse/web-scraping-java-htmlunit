package sharma.prateek.webscraping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;



public class WebScraper {
	
	public static void main(String[] args){
		
		try {
			Properties properties = getProperties();
			String baseURL = properties.getProperty("link");
			Integer pages = Integer.parseInt(properties.getProperty("pages"));
			List<Element> elements = new ArrayList<>();
			
			for(int i=1;i<=pages;i++) {
				processPage(i,baseURL,elements);
			}
			
			System.out.println(elements.size());
			System.out.println();
			
			for(Element element : elements)
				System.out.println(element.toString());
			
			exportToExcel(elements);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	private static void exportToExcel(List<Element> elements) throws IOException {
		String newFileName = "Report_"+new Date()+".xls";
		File oldFile = new File("ReportTemplate.xls");
		File newFile = new File(newFileName);
		
		FileUtils.copyFile(oldFile, newFile);
		
		InputStream inputStream = new FileInputStream(newFileName);
		Workbook workbook = WorkbookFactory.create(inputStream);
		Sheet sheet = workbook.getSheetAt(0);
		
		int row = 1;
		
		int serialCol = 0;
		int nameCol = 1;
		int contactCol = 2;
		int urlCol = 3;
		
		for(int i=0;i<elements.size();i++) {			
			Row currentRow = sheet.createRow(row);
			
			Cell serialCell = currentRow.createCell(serialCol);
			Cell nameCell = currentRow.createCell(nameCol);
			Cell contactCell = currentRow.createCell(contactCol);
			Cell urlCell = currentRow.createCell(urlCol);
			
			serialCell.setCellValue(i+1);
			nameCell.setCellValue(elements.get(i).getName());
			contactCell.setCellValue(elements.get(i).getContact());
			urlCell.setCellValue(elements.get(i).getUrl());	
			
			row++;
		}
		
		for(int col=0;col<=3;col++)
			sheet.autoSizeColumn(col);
		
		inputStream.close();
		FileOutputStream outputStream = new FileOutputStream(newFileName);
		workbook.write(outputStream);		
	}


	private static void processPage(int i, String baseURL, List<Element> elements) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		WebClient client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		
		String pageURL = baseURL+"/page-"+i;
		HtmlPage page = client.getPage(pageURL);
		
		int high = (i*10)-1;
		int low = high-9;
		
		for(int j=low;j<=high;j++) {
			String name = null;
			String url = null;
			String contact = "";
					
			HtmlElement section = page.getHtmlElementById("bcard"+j);
			Iterable<DomNode> nodes = section.getChildren();
			for(DomNode node : nodes) {
				boolean nodeNameCondition = node.getNodeName().equalsIgnoreCase("div");
				boolean nodeClassCondition = node.getAttributes().getNamedItem("class").getNodeValue().contains("colsp");
				if(nodeNameCondition && nodeClassCondition) {
					Iterable<DomNode> divChildren = node.getChildren();
					for(DomNode divChild : divChildren) {
						if(divChild.getNodeName().equalsIgnoreCase("section")) {
							Iterable<DomNode> sectionChildren = divChild.getChildren();
							for(DomNode sectionChild : sectionChildren) {
							Optional<Boolean> nodesecChildClassCond = Optional.ofNullable(sectionChild.getAttributes().getNamedItem("class").getNodeValue().contains("store-details"));
							boolean secChildNameCondition = sectionChild.getNodeName().equalsIgnoreCase("div");
							
							if(secChildNameCondition && nodesecChildClassCond.isPresent() && nodesecChildClassCond.get()) {
								Iterable<DomNode> storeChildren = sectionChild.getChildren();
								for(DomNode storeChild : storeChildren) {									
									
									if(storeChild.getNodeName().equalsIgnoreCase("#comment"))
										continue;
									
									if(storeChild.getNodeName().equalsIgnoreCase("h2")) {
										Iterable<DomNode> h2Children = storeChild.getChildren();
										for(DomNode h2Child : h2Children) {
											if(h2Child.getNodeName().equalsIgnoreCase("span")) {
												Iterable<DomNode> h2SpanChildren = h2Child.getChildren();
												for(DomNode h2SpanChild : h2SpanChildren) {
													if(h2SpanChild.getNodeName().equalsIgnoreCase("a")) {
														HtmlAnchor itemAnchor = (HtmlAnchor) h2SpanChild;
														url = itemAnchor.getHrefAttribute();
														name = itemAnchor.getAttribute("title");														
													}
												}												
											}											
										}										
									}//end h2
									
									boolean contactInfoNameCondition = storeChild.getNodeName().equalsIgnoreCase("p");
									boolean contactInfoClassCondition = storeChild.getAttributes().getNamedItem("class").getNodeValue().contains("contact-info");
									if(contactInfoNameCondition && contactInfoClassCondition) {
										Iterable<DomNode> contactInfoChildren = storeChild.getChildren();
										for(DomNode contactInfoChild : contactInfoChildren)
											if(contactInfoChild.getNodeName().equalsIgnoreCase("span")){
												Iterable<DomNode> spanChildren = contactInfoChild.getChildren();
												for(DomNode spanChild : spanChildren)
													if(spanChild.getNodeName().equalsIgnoreCase("a")) {
														Iterable<DomNode> aChildren = spanChild.getChildren();
														for(DomNode aChild : aChildren) {
															Iterable<DomNode> bChildren = aChild.getChildren();
															for(DomNode bChild : bChildren) {
																HtmlSpan spanNode = (HtmlSpan) bChild;
																String encrValue = spanNode.getAttribute("class");
																contact = contact+Constants.getNumbersmap().get(encrValue);
															}																
														}
													}												
											}										
									}//end contact								
								}
							}
							break;
							}
						}
					}
				}			
		}
			Element element = new Element();
			element.setName(name);
			element.setContact(contact);
			element.setUrl(url);
			
			elements.add(element);
		}
		
	}


	private static void setElementsList(List<Element> elementsList, List<String> contactsList) {
		for(int i=0;i<elementsList.size();i++) {
			Element element = elementsList.get(i);
			element.setContact(contactsList.get(i));
		}
		
		for(Element element : elementsList)
			System.out.println(element.toString());
		
	}


	private static Properties getProperties() {
		Properties properties = null;
		try (InputStream in = new FileInputStream("scraping.properties")) {
            properties = new Properties();
            properties.load(in);            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		return properties;		
	}
}
