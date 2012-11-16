package buaa.sei.xyb.analyse.document.pdfbox;

import java.io.File;
import java.io.IOException;

import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

/**
 *  PDF ²Ù×÷²âÊÔ
 * @author Xu Yebing
 *
 */
public class PdfboxTest {

	public static void main(String[] args) {
//		try {
//			FileInputStream fis = new FileInputStream("D:\\XuYebing_BeihangUniversity.pdf");
//			COSDocument cosDoc = null;
//			PDFParser parser = new PDFParser(fis);
//			parser.parse();
//			cosDoc = parser.getDocument();
//			PDFTextStripper stripper = new PDFTextStripper();
//			String docText = stripper.getText(new PDDocument(cosDoc));
//			System.out.println(docText);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		try {
			PDDocument doc = PDDocument.load(new File("D:\\Lucene_In_Action_cn.pdf"));
			PDDocumentOutline root = doc.getDocumentCatalog().getDocumentOutline();
			PDOutlineItem item = root.getFirstChild();
			while (item != null) {
				System.out.println("Item: " + item.getTitle());
				PDOutlineItem child = item.getFirstChild();
				while (child != null) {
					System.out.println(" Child: " + child.getTitle());
					child = child.getNextSibling();
				}
				item = item.getNextSibling();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
