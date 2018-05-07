package fileforce.Helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.record.TextHeaderAtom;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xslf.usermodel.DrawingParagraph;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fileforce.Service.CommonService;

public class SelfParserUtility {
	
	public static void readPDFFile(String url, String fileId, Map<String, String> mapPlatformIdBody){
		// Create a PdfDocument instance
		Set<String> tokens = new HashSet<String>();
		PDFParser parser;
		String parsedText = "";
		PDFTextStripper pdfStripper = null;
		PDDocument pdDoc = null;
		COSDocument cosDoc = null;
		boolean isError = false;
		String errorMessage = "";
		try {
			InputStream inputStream = new URL(url).openStream();
			parser = new PDFParser(inputStream);
			parser.parse();
			cosDoc = parser.getDocument();
			pdfStripper = new PDFTextStripper();
			pdDoc = new PDDocument(cosDoc);
			pdfStripper.setStartPage(1);
			pdfStripper.setEndPage(pdfStripper.getEndPage());
			parsedText = pdfStripper.getText(pdDoc);
			
		} catch (Exception e) {
			isError = true;
			System.err
					.println("An exception occured in parsing the PDF Document."
							+ e.getMessage());
			errorMessage = e.getMessage();
		} finally {
			try {
				if (cosDoc != null)
					cosDoc.close();
				if (pdDoc != null)
					pdDoc.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(!isError){
			for(String str : parsedText.split(" ")){
				tokens.add(str);
			}
			
			//now making the string again
			StringBuilder response = new StringBuilder();
			for(String value : tokens){
		    	response.append(value + " ");
		    }
			mapPlatformIdBody.put(fileId, response.toString());
		}else{
			mapPlatformIdBody.put(CommonService.ERROR_MSG, errorMessage);
		}
	}
	
	public static void readXLSFile(String url, Map<String, String> mapPlatformIdBody){
		try {
			InputStream inputStream = new URL(url).openStream();
			HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
			HSSFSheet worksheet = workbook.getSheet("Org File");
			HSSFRow row1 = worksheet.getRow(0);
			HSSFCell cellA1 = row1.getCell((short) 0);
			String a1Val = cellA1.getStringCellValue();
			HSSFCell cellB1 = row1.getCell((short) 1);
			String b1Val = cellB1.getStringCellValue();
			HSSFCell cellC1 = row1.getCell((short) 2);
			//boolean c1Val = cellC1.getBooleanCellValue();
			String c1Val = cellC1.getStringCellValue();
			HSSFCell cellD1 = row1.getCell((short) 3);
			//Date d1Val = cellD1.getDateCellValue();
			String d1Val = cellD1.getStringCellValue();

			System.out.println("A1: " + a1Val);
			System.out.println("B1: " + b1Val);
			System.out.println("C1: " + c1Val);
			System.out.println("D1: " + d1Val);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readXLSXFile(String url, String fileId, Map<String, String> mapPlatformIdBody){
		try { 
			InputStream fis = new URL(url).openStream();
			XSSFWorkbook book = new XSSFWorkbook(fis);
			Set<String> tokens = new HashSet<String>();
			StringBuilder response = new StringBuilder();
			for(int i=0; i < book.getNumberOfSheets(); i++){
				XSSFSheet sheet = book.getSheetAt(i); 
				Iterator<Row> itr = sheet.iterator(); 
				
				// Iterating over Excel file in Java 
				while (itr.hasNext()) { 
					Row row = itr.next(); 
					// Iterating over each column of Excel file 
					Iterator<Cell> cellIterator = row.cellIterator(); 
					while (cellIterator.hasNext()) { 
						Cell cell = cellIterator.next(); 
							switch (cell.getCellType()) {
								case Cell.CELL_TYPE_STRING: 
									tokens.add(cell.getStringCellValue());
									//response.append(cell.getStringCellValue() + "\t");
									//System.out.print(cell.getStringCellValue() + "\t"); 
								break; 
								case Cell.CELL_TYPE_NUMERIC:
									tokens.add(String.valueOf(cell.getNumericCellValue()));
									//response.append(cell.getNumericCellValue() + "\t");
									//System.out.print(cell.getNumericCellValue() + "\t"); 
								break; 
								case Cell.CELL_TYPE_BOOLEAN: 
									tokens.add(String.valueOf(cell.getBooleanCellValue()));
									//response.append(cell.getBooleanCellValue() + "\t");
									//System.out.print(cell.getBooleanCellValue() + "\t"); 
								break; default: 
							} 
					} 
				//System.out.println("");
				//response.append("\r");
				}
			}
			for(String value : tokens){
		    	response.append(value + " ");
		    }
			mapPlatformIdBody.put(fileId, response.toString());
		}catch (Exception e) {
			e.printStackTrace();
			mapPlatformIdBody.put(CommonService.ERROR_MSG, e.getMessage());
		}
	}
	
	public static void readXMLFile(String fileName){
		try {	
	         File inputFile = new File(fileName);
	         DocumentBuilderFactory dbFactory 
	            = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.parse(inputFile);
	         doc.getDocumentElement().normalize();
	         System.out.println("Root element :" 
	            + doc.getDocumentElement().getNodeName());
	         NodeList nList = doc.getElementsByTagName("student");
	         System.out.println("----------------------------");
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	            Node nNode = nList.item(temp);
	            System.out.println("\nCurrent Element :" 
	               + nNode.getNodeName());
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	               Element eElement = (Element) nNode;
	               System.out.println("Student roll no : " 
	                  + eElement.getAttribute("rollno"));
	               System.out.println("First Name : " 
	                  + eElement
	                  .getElementsByTagName("firstname")
	                  .item(0)
	                  .getTextContent());
	               System.out.println("Last Name : " 
	               + eElement
	                  .getElementsByTagName("lastname")
	                  .item(0)
	                  .getTextContent());
	               System.out.println("Nick Name : " 
	               + eElement
	                  .getElementsByTagName("nickname")
	                  .item(0)
	                  .getTextContent());
	               System.out.println("Marks : " 
	               + eElement
	                  .getElementsByTagName("marks")
	                  .item(0)
	                  .getTextContent());
	            }
	         }
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	  }
	
	public static void readDocFile(String fileName) {

		try {
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file.getAbsolutePath());

			HWPFDocument doc = new HWPFDocument(fis);

			WordExtractor we = new WordExtractor(doc);

			String[] paragraphs = we.getParagraphText();
			
			System.out.println("Total no of paragraph "+paragraphs.length); 
			for (String para : paragraphs) {
				System.out.println(para.toString());
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void readDocxFile(String fileName) {

		try {
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file.getAbsolutePath());

			XWPFDocument document = new XWPFDocument(fis);
			List<XWPFParagraph> paragraphs = document.getParagraphs();
			System.out.println("Total no of paragraph "+paragraphs.size());
			for (XWPFParagraph para : paragraphs) {
				System.out.println(para.getText());
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void readSingleCsvFile(String fileName){
		try{
          FileReader fr = new FileReader(fileName);
          String temp = fileName;
          BufferedReader br = new BufferedReader(fr);
          if ((temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase()).equals("csv")){
        	  String content = "";
        	  while (br.ready()) {
        		  String line = br.readLine();
        		  String arr[] = line.split(",");
        		  content += line + "\n";
        		  if(!arr[0].isEmpty() && Integer.valueOf(arr[0]) > 1){
        			  //System.out.println(arr[0]);
        			  for(int i = 0; i< Integer.valueOf(arr[0])-1 ; i++){
        				  //line.replace(arr[0])
        				  content += line+ "\n";
        			  }
        		  }
        	  }
        	  System.out.println(content);
          }
		}catch(Exception e){
				System.out.println("Exception caught= " +e);
			}
	}
	
	public static void readSTFFile(final File folder){
		try{
		    for (final File fileEntry : folder.listFiles()) {
		      if (fileEntry.isDirectory()) {
		        // System.out.println("Reading files under the folder "+folder.getAbsolutePath());
		    	  readSTFFile(fileEntry);
		      } else {
		        if (fileEntry.isFile()) {
		          String temp = fileEntry.getName();
		          FileReader fr = new FileReader(fileEntry);
		          BufferedReader br = new BufferedReader(fr);
		          if ((temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase()).equals("stf")){
		        	  String content = "";
		              while (br.ready()) {
		            	  content = br.readLine().trim();
		            	  String[] fields = content.split("\t");
		            	  System.out.println(fields);
		              }
		          }
		        }
		      }
		    }
        }catch(Exception e){
			System.out.println("Exception caught= " +e);
		}
	}
	
	public static void readPPTFile(String url, String fileId, Map<String, String> mapPlatformIdBody){
		try {
			InputStream fis = new URL(url).openStream();
			POIFSFileSystem fs = new POIFSFileSystem(fis);
			HSLFSlideShow show = new HSLFSlideShow(fs);
			SlideShow ss = new SlideShow(show);
			Slide[] slides=ss.getSlides();
			for (int x = 0; x < slides.length; x++) {
				System.out.println("Slide = " + (x + 1) + " : " + slides[x].getTitle());
	
				TextRun[] runs = slides[x].getTextRuns();
				for (int i = 0; i < runs.length; i++) {
					TextRun run = runs[i];
					if (run.getRunType() == TextHeaderAtom.TITLE_TYPE) {
						System.out.println("Slide title " + (i + 1) + ": " + run.getText());
					} else {
						System.out.println("Slide text run " + (i + 1) + ": " + run.getRunType() + " : " + run.getText());
					}
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void readPPTXFile(String url, String fileId, Map<String, String> mapPlatformIdBody){
		try{
			Set<String> tokens = new HashSet<String>();
			InputStream fis = new URL(url).openStream();
			XMLSlideShow ppt = new XMLSlideShow(fis);
			XSLFSlide[] slides = ppt.getSlides();
			StringBuilder response = new StringBuilder();
			
			//iterating through all the slides
			for(int i=0;i<slides.length;i++){
				XSLFSlide slide = slides[i];
				String title=slide.getTitle();
				List<DrawingParagraph> data = slide.getCommonSlideData().getText();
				tokens.add(title);
				//response.append(title + "\r");
				//System.out.println(title);
				
				for(int j=0;j<data.size();j++)
				{
					DrawingParagraph para = data.get(j);
					//tokens.add(para.getText());
				}
			}
			mapPlatformIdBody.put(fileId, response.toString());
		}catch(Exception ioe){
			ioe.printStackTrace();
			mapPlatformIdBody.put(CommonService.ERROR_MSG, ioe.getMessage());
		}
	}


}
