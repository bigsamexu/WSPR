package process;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import webService.WebService;

public class WSHSP {
	/**
	 * this is the absolute path of the wsdl files
	 */
	private File testDataDir;
	
	/**
	 * this is the absolute path of the query file
	 */
	private File goal;
	
	public WSHSP() {
		
	}
	
	public File getTestDataDir() {
		return testDataDir;
	}

	public void setTestDataDir(File testDataDir) {
		this.testDataDir = testDataDir;
	}

	public File getGoal() {
		return goal;
	}
	
	public void setGoal(File goal) {
		this.goal = goal;
	}
	
	public WebService readIeeeWsdl(File file, String service_name) throws DocumentException {
		WebService service = new WebService(service_name);
		SAXReader reader = new SAXReader();		//建立SAX解析读取
		Document doc = null;
		doc = reader.read(file);		//读取文档
		Element root = doc.getRootElement();
		for(Iterator iter = root.elementIterator("message"); iter.hasNext();) {//遍历根节点以下所有子节点
			Element massage = (Element) iter.next();
			if(massage.attribute("name").getValue().equals(service_name + "_Request")) {
				for(Iterator child_iter = massage.elementIterator("message"); child_iter.hasNext();){
					
				}
					
			}
			
		}
		return service;
	}
	
	public void loadWSDL(File dir) throws DocumentException {
		Map<String, WebService> returnMap = new HashMap<String, WebService>(); 
		for(File file : dir.listFiles()) {
			String file_name = file.getName();
			String service_name = file_name.substring(0, file_name.lastIndexOf('.'));
			returnMap.put(service_name, readIeeeWsdl(file, service_name));
		}
	}
	
	/**
	 * 提取query文件信息
	 * @param goalFile
	 * @return
	 * @throws DocumentException
	 */
	public WebService readIeeeGoal(File goalFile) throws DocumentException {
		if(!goalFile.exists()){
			System.out.println("Failure to open " + goalFile + ".");
			System.exit(1);
		}
		WebService service = null;
		SAXReader reader = new SAXReader();		//建立SAX解析读取
		Document doc = null;
		doc = reader.read(goalFile);		//读取文档
		Element root = doc.getRootElement();
		for(Iterator iter = root.elementIterator("CompositionRoutine"); iter.hasNext();) {//遍历根节点以下所有子节点
			Element element = (Element) iter.next();
			String name = element.attribute("name").getValue();
			String input = element.elementText("Provided");
			String output = element.elementText("Resultant");
			service = new WebService(name);
			for(String val : input.split(",")) {
				service.addInput(val.trim());
			}
			for(String val : output.split(",")) {
				service.addOutput(val.trim());
			}
		}
		return service;
	}
	
	/**
	 * check the input file and the goal file, if not null, then invoke the process() 
	 */
	public void run() {
		if(this.goal == null || this.testDataDir == null) {
			if(this.testDataDir == null)
				System.out.println("You need to enter the directory for the test data");
			if(this.goal == null)
				System.out.println("You need to enter a filename for the goal wsdl");
		}else {
			try {
				process();
			} catch (DocumentException e) {
			}
			System.out.println("end");
		}
	}
	
	public void process() throws DocumentException {
		System.out.println("Loading the goal file \""+this.goal+"\"...");
		WebService service = readIeeeGoal(this.goal);
		System.out.println("Done loaded the goal file of " + this.goal);
		loadWSDL(this.testDataDir);
		System.out.println("Loading the WSDL files in \"" + this.testDataDir + "\"...");
		
	}
}
