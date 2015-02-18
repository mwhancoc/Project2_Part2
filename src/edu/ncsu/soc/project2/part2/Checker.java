package edu.ncsu.soc.project2.part2;


import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Date;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.ValidityReport.Report;
import com.hp.hpl.jena.datatypes.xsd.*;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Checker extends Activity {
	
	final static String OWL_REF  = "http://csc750/p2/reference.owl#";
	
	String firstName = "";
	String lastName = "";
	String street = "";
	String city = "";
	String state = "";
	String zip = "";
	String itemName = "";	
	Float unitPrice;
	int orderNumber;
	int quantity;	
	Date orderDate;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checker);
		
		// for our output
		TextView textView = (TextView) findViewById(R.id.activity_checker_text_view);
		
		textView.setMovementMethod(new ScrollingMovementMethod());
		
		// open demoData.rdf
		// create an empty model
		Model schema = ModelFactory.createDefaultModel();
		Model data = ModelFactory.createDefaultModel();
		AssetManager assetManager = getAssets();
		String output = "";
		
		InputStream inputStream;
		
			try {
				// read in both the owl schema and instance document
				inputStream = assetManager.open("PO_new.owl");
				schema.read(inputStream, null);
				inputStream.close();
				inputStream = assetManager.open("purchase_order.owl");
				data.read(inputStream, null);
				inputStream.close();
				
				// construct a reasoner then bind to our schema to create an inference model
				Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
				reasoner = reasoner.bindSchema(schema);
				InfModel infmodel = ModelFactory.createInfModel(reasoner, data);
				
				// run a validity report, if valid parse instance document and output data
				ValidityReport validity = infmodel.validate();
				if (validity.isValid()) {
				    output = "Order Confirmed";
				    
				    StmtIterator iter = data.listStatements();
					
					// print out the predicate, subject and object of each statement
					while (iter.hasNext())
					{
					    Statement stmt      = iter.nextStatement();  // get next statement
					    Resource  subject   = stmt.getSubject();     // get the subject
					    Property  predicate = stmt.getPredicate();   // get the predicate
					    RDFNode   object    = stmt.getObject();      // get the object
					    
										    
					    if(predicate.toString().equals(OWL_REF + "LastName"))
					    	lastName = object.toString();
					    else if (predicate.toString().equals(OWL_REF + "FirstName"))
					    	firstName = object.toString();
					    else if (predicate.toString().equals(OWL_REF + "OrderNumber")) {					   
						   Literal l = (Literal)object;
						   orderNumber = l.getInt();						   
					    }
					    else if (predicate.toString().equals(OWL_REF + "unitPrice")) {
					    	Literal l = (Literal)object;
					    	unitPrice = l.getFloat();
					    }
					    else if (predicate.toString().equals(OWL_REF + "Quantity")) {
					    	Literal l = (Literal)object;
					    	quantity = l.getInt();					    	
					    }
					    else if (predicate.toString().equals(OWL_REF + "OrderDate")) {
					    	Literal l = (Literal)object;
					    	XSDDateTime xdate = (XSDDateTime)l.getValue();
					    	orderDate = xdate.asCalendar().getTime();
					    }						 					    
					    else if (predicate.toString().equals(OWL_REF + "Street"))
					    	street = object.toString();
					    else if (predicate.toString().equals(OWL_REF + "City"))
					    	city = object.toString();
					    else if (predicate.toString().equals(OWL_REF + "State"))
					    	state = object.toString();
					    else if(predicate.toString().equals(OWL_REF + "Zip"))
					    	zip = object.toString();
					    else if (predicate.toString().equals(OWL_REF + "ItemName"))
					    	itemName = object.toString();
					    
//###########################################################################################
//            UNCOMMENT TO VIEW SUBJECT/PREDICATE/OBJECT RESULTS
//					    
//					    output += " Subject: " + subject.toString();
//					    output += " Predicate: " + predicate.toString() + " ";
//					    if(object instanceof Resource)
//					    {
//					       output += " Object: " + object.toString() + ".\n\n";
//					    } 
//					    else{
//					        // object is a literal
//					        output += " Object:  \"" + object.toString() + "\"" + ".\n\n";
//					    }
//############################################################################################
					    
					}
				
					// build the output for a parsed purchase order
					output += "\nOrder Number: " + orderNumber;					
					output += "\nOrder Date: " + sdf.format(orderDate);
					output += "\nFirst Name: " + firstName;
					output += "\nLast Name: " + lastName;
					output += "\nItem Name: " + itemName;
					output += "\nQuantity: " + quantity;
					output += "\nTotal Price: " + quantity * unitPrice;
					output += "\nAddress: " + street + ", " + city + ", " + state + " " + zip;
					//output += "\nUnit Price: " + unitPrice;	
					
				} else {
				    output = "Invalid Order\n\n";
				    for (Iterator<Report> i = validity.getReports(); i.hasNext(); ) {
				        ValidityReport.Report report = (ValidityReport.Report)i.next();
				        output += report.toString() + "\n";
				    }
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			textView.setText(output);
			System.out.println(output);
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.checker, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
