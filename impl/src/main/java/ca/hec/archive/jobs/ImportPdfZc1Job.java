
package ca.hec.archive.jobs;

import java.awt.Dimension;
import java.awt.Insets;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.zefer.pd4ml.PD4Constants;
import org.zefer.pd4ml.PD4ML;

/**
 * A one-time job to import the course outlines from ZC1 HTML format to ZC2 PDF format
 */
public class ImportPdfZc1Job implements Job {
    
    protected Dimension format = PD4Constants.A4;
	protected int userSpaceWidth = 900;
	protected boolean landscapeValue = false;
	   protected int topValue = 10;
	   protected int leftValue = 10;
	   protected int rightValue = 10;
	   protected int bottomValue = 10;
	   protected String unitsValue = "mm";
	   protected String proxyHost = "";
	   protected int proxyPort = 0;

    public void execute(JobExecutionContext arg0) throws JobExecutionException {	
  	  try {
  	    
              java.io.FileOutputStream fos = new FileOutputStream("D:\\Downloads\\iTextArch.pdf");
              PD4ML pd4ml = new PD4ML();                                                              
                      pd4ml.setPageSize( landscapeValue ? pd4ml.changePageOrientation( format ): format );
                 
                     
               if ( unitsValue.equals("mm") ) {
                      pd4ml.setPageInsetsMM( new Insets(topValue, leftValue,
bottomValue, rightValue) );
               } else {
                      pd4ml.setPageInsets( new Insets(topValue, leftValue,
bottomValue, rightValue) );
               }

               pd4ml.setHtmlWidth( userSpaceWidth );
               pd4ml.disableHyperlinks();
               pd4ml.enableDebugInfo();
               
               File file=new File("D:\\test_style.css");
               URL url = file.toURI().toURL();
               pd4ml.addStyle(url, true);
               
              
             pd4ml.render( "http://zonecours.hec.ca/af1CodexImp.jsp?instId=H2013-1-180412-A01&lang=fr", fos );
             //pd4ml.render( "http://expressowebsites.com/testhec", fos );
        } catch (Exception e) {
           e.printStackTrace();
       }
    }

}

