package edu.ncsu.soc.project2.part2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.InfModel;
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
import com.hp.hpl.jena.util.PrintUtil;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Checker extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checker);
		
		TextView textView = (TextView) findViewById(R.id.activity_checker_text_view);
		
		//works! next open a file and print to the textView 
		//textView.setText("Hey Mike!");
		
		// open demoData.rdf
		// create an empty model
		Model schema = ModelFactory.createDefaultModel();
		Model data = ModelFactory.createDefaultModel();
		AssetManager assetManager = getAssets();
		String output = "";
		
		InputStream inputStream;
		
			try {
				inputStream = assetManager.open("PO_new.owl");
				schema.read(inputStream, null);
				inputStream.close();
				inputStream = assetManager.open("purchase_order.rdf");
				data.read(inputStream, null);
				inputStream.close();
				
				Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
				reasoner = reasoner.bindSchema(schema);
				InfModel infmodel = ModelFactory.createInfModel(reasoner, data);
				
				ValidityReport validity = infmodel.validate();
				if (validity.isValid()) {
				    output = "OK";
				} else {
				    output = "Conflicts";
				    for (Iterator<Report> i = validity.getReports(); i.hasNext(); ) {
				        ValidityReport.Report report = (ValidityReport.Report)i.next();
				        output += report.toString();
				    }
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			textView.setText(output);
		
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
