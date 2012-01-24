package org.husio.web.jetty;

import java.io.PrintWriter;

/**
 * Proxy printwriter that stops Jackson from automatically flushing
 * the output. It also adds the callback.
 * 
 * There seems to be no other way to get jackson to stop flushing/closing the output stream but
 * to start writing Jackon components. I find this much easier.
 * 
 * @author rafael
 *
 */
public class JsonCallbackPrintWriter extends PrintWriter {
        
    private PrintWriter out;

    public JsonCallbackPrintWriter(PrintWriter out,String callback) {
	super(out,false);
	this.out=out;
	this.out.print(callback+"(");
    }

    @Override
    public void flush(){
	this.write(")");
	super.flush();
    }
   
    @Override
    public void close(){
	flush();
	super.close();
    }

}
