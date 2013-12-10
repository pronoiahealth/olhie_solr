package com.pronoiahealth.olhie.solr.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.SolrInputDocument;

public class SolrUtil {
	
	private static Logger log 	= Logger.getLogger(SolrUtil.class);
	private static SolrUtil instance = null;
	
	private SolrServer server = null;
	
	
	private SolrUtil(String solrUrl){
		server = new HttpSolrServer(solrUrl);
	}
	
	public static synchronized SolrUtil getInstance(String solrURL){
		if(instance == null){
			instance = new SolrUtil(solrURL);
		}
		
		return instance;
	}
	
	public void update(String id, String keywords){
		try{
			Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField( "id", id, 1.0f );
			doc.addField( "keywords", keywords, 1.0f );
			docs.add(doc);
			
			server.add(docs);
			server.commit();
		}
		catch(Exception e){
			log.error(e.getMessage(),e);
		}
	}
	
	public void delete(String id){
		try{
			server.deleteById(id);
			server.commit();
		}
		catch(Exception e){
			log.error(e.getMessage(),e);
		}
	}
	
	public void index(String id, String filepath, String contentType) {
		try{
			
			// create the content update request
			ContentStreamUpdateRequest up 	= new ContentStreamUpdateRequest("/update/extract");
			up.addFile(new File(filepath),contentType);
			up.setParam("literal.id", id);
			up.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
			
			// submits content update request
    		server.request(up);
    		
    		log.info("completed indexing id => " + id);
			
		}
		catch(Exception e){
			log.error(e.getMessage(),e);
		}
	}

}
