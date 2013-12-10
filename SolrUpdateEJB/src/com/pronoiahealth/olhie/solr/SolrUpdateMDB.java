package com.pronoiahealth.olhie.solr;

import java.io.File;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.lowagie.text.pdf.codec.Base64;
import com.pronoiahealth.olhie.solr.util.SolrUtil;
import com.pronoiahealth.olhie.solr.vo.BookSearchVO;
import com.pronoiahealth.olhie.solr.xml.Book;
import com.pronoiahealth.olhie.solr.xml.BookAsset;
import com.pronoiahealth.olhie.solr.xml.BookAssetDescription;
import com.pronoiahealth.olhie.solr.xml.User;

/**
 * Message-Driven Bean implementation class for: SolrUpdateMDB
 * 
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/solr"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class SolrUpdateMDB implements MessageListener {

	private static Logger log = Logger.getLogger(SolrUpdateMDB.class);
	private static String[] processibleMimeTypes = new String[] {
			"application/pdf",
			"text/html",
			"text/plain",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			"application/msword" };
	private List<String> processibleMimeTypesLst = null;
	private String tempDocDir = null;
	private String solrURL = null;
	private BookSearchVO searchString = null;
	private static Priority logPriorityLevel = Priority.INFO;

	/**
	 * Default constructor.
	 */
	public SolrUpdateMDB() {
		solrURL = System.getProperty("solr.url");
		tempDocDir = System.getProperty("temp.doc.dir");
		processibleMimeTypesLst = Arrays.asList(processibleMimeTypes);
	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	@SuppressWarnings("unchecked")
	public void onMessage(Message message) {
		try {
			String msg = ((TextMessage) message).getText();
			if (log.isEnabledFor(logPriorityLevel)) {
				log.info("temp doc dir => " + tempDocDir);
				log.info("solr url => " + solrURL);
				log.info("******************");
				log.info("begin xml message");
				log.info("******************");
				log.info(msg);
				log.info("*****************");
				log.info("end xml message");
				log.info("*****************");
			}

			// parse XML string with JAXB to get Book object
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.pronoiahealth.olhie.solr.xml");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(msg);
			JAXBElement<Book> element = (JAXBElement<Book>) unmarshaller
					.unmarshal(reader);
			Book book = element.getValue();

			// create Solr
			if (log.isEnabledFor(logPriorityLevel)) {
				log.info("==========================================================");
				log.info("======================= Start Book =======================");
				log.info("==========================================================");

				log.info("************************ Book *************************");
				log.info("id => " + book.getId());
				log.info("bookTitle => " + book.getBookTitle());
				log.info("introduction => " + book.getIntroduction());
				log.info("keywords => " + book.getKeyWords());
				log.info("category => " + book.getCategory());
				log.info("coverName => " + book.getCoverName());
				log.info("createdDate => " + book.getCreatedDate());
				log.info("publishedDate => " + book.getPublishedDate());
				log.info("authorId => " + book.getAuthorId());
				log.info("active => " + book.getActive());
			}

			boolean isActive = new Boolean(book.getActive().toString());

			if (isActive == true) {

				searchString = new BookSearchVO();
				searchString.setBookId(book.getId());
				searchString.setBookTitle(nvl(book.getBookTitle()));
				searchString.setIntroduction(nvl(book.getIntroduction()));
				searchString.setKeywords(nvl(book.getKeyWords()));
				searchString.setCategory(nvl(book.getCategory()));
				searchString.setCoverName(nvl(book.getCoverName()));

				/************
				 * USER
				 ***********/

				int count = 0;
				Iterator<User> users = book.getUser().iterator();
				StringBuffer userSearch = new StringBuffer();

				while (users.hasNext() == true) {

					User user = users.next();

					if (log.isEnabledFor(logPriorityLevel)) {
						log.info("************************ User ************************* ");
						log.info("id => " + user.getId());
						log.info("firstName => " + user.getFirstName());
						log.info("lastName => " + user.getLastName());
						log.info("userId => " + user.getUserId());
						log.info("resetPwd => " + user.getResetPwd());
						log.info("role => " + user.getRole());
					}

					String uString = nvl(user.getFirstName()) + " "
							+ nvl(user.getLastName()) + " "
							+ nvl(user.getUserId());

					if (count == 0) {
						userSearch.append(uString.trim());
					} else {
						userSearch.append(uString.trim() + BookSearchVO.BLANK);
					}

					count++;
				}

				searchString.setAuthor(userSearch.toString().trim());

				/****************************
				 * BOOK ASSET DESCRIPTION
				 ****************************/
				Iterator<BookAssetDescription> bookAssetDesciptions = book
						.getBookAssetDescription().iterator();
				StringBuffer badSearch = new StringBuffer();

				while (bookAssetDesciptions.hasNext() == true) {

					BookAssetDescription bookAssetDescription = bookAssetDesciptions
							.next();

					if (log.isEnabledFor(logPriorityLevel)) {
						log.info("************************ Book Asset Description ************************* ");
						log.info("id => " + bookAssetDescription.getId());
						log.info("description => "
								+ bookAssetDescription.getDescription());
						log.info("createdDate => "
								+ bookAssetDescription.getCreatedDate());
						log.info("removed => "
								+ bookAssetDescription.getRemoved());
						log.info("removedDate => "
								+ bookAssetDescription.getRemovedDate());
						log.info("bookId => "
								+ bookAssetDescription.getBookId());
					}

					boolean isRemoved = new Boolean(
							bookAssetDescription.getRemoved());

					if (isRemoved == false) {

						badSearch.append(nvl(bookAssetDescription
								.getDescription()) + BookSearchVO.BLANK);

						/***************
						 * BOOK ASSET
						 ***************/

						Iterator<BookAsset> bookAssets = book.getBookAsset()
								.iterator();
						StringBuffer baSearch = new StringBuffer();

						while (bookAssets.hasNext() == true) {
							BookAsset bookAsset = bookAssets.next();

							// only process the book assets that are associated
							// with the current book asset description
							if (bookAsset.getBookassetdescriptionId().equals(
									bookAssetDescription.getId()) == true) {

								if (log.isEnabledFor(logPriorityLevel)) {
									log.info("************************ Book Asset ************************* ");
									log.info("id => " + bookAsset.getId());
									log.info("createDate => "
											+ bookAsset.getCreatedDate());
									log.info("authorId => "
											+ bookAsset.getAuthorId());
									log.info("itemType => "
											+ bookAsset.getItemType());
									log.info("itemName => "
											+ bookAsset.getItemName());
									log.info("contentType => "
											+ bookAsset.getContentType());
									log.info("size => " + bookAsset.getSize());
									// log.info("base64Data => " +
									// bookAsset.field("base64Data"));
									log.info("bookassetdescriptionId => "
											+ bookAsset
													.getBookassetdescriptionId());
								}

								baSearch.append(nvl(bookAsset.getItemName())
										+ BookSearchVO.BLANK);

								String b64Data = bookAsset.getBase64Data();
								String contentType = bookAsset.getContentType();
								if (b64Data != null
										&& b64Data.length() > 0
										&& processibleMimeTypesLst
												.contains(contentType)) {

									log.info("writing file " + tempDocDir
											+ bookAsset.getItemName());
									Base64.decodeToFile(
											bookAsset.getBase64Data(),
											tempDocDir
													+ bookAsset.getItemName());

									// index document
									log.info("indexing document => "
											+ bookAsset.getItemName());
									String key = book.getId() + "|"
											+ bookAssetDescription.getId();
									SolrUtil.getInstance(solrURL).index(
											key,
											tempDocDir
													+ bookAsset.getItemName(),
											bookAsset.getContentType());

									try {
										File file = new File(tempDocDir
												+ bookAsset.getItemName());
										if (file.exists() == true) {
											if (file.delete() == true) {
												log.info("deleted file => "
														+ tempDocDir
														+ bookAsset
																.getItemName());
											}
										}
									} catch (Exception e) {
										log.info("could not delete file => "
												+ tempDocDir
												+ bookAsset.getItemName());
									}
								}

							}

							searchString.setBookAsset(baSearch.toString()
									.trim());
						}
					} else {

						// delete book document from solr
						String key = book.getId() + "|"
								+ bookAssetDescription.getId();
						SolrUtil.getInstance(solrURL).delete(key);
						log.info("removed book asset" + key + " from Solr");
					}

				}

				searchString
						.setBookAssetDecription(badSearch.toString().trim());

				if (log.isEnabledFor(logPriorityLevel)) {
					log.info("==========================================================");
					log.info("======================== End Book ========================");
					log.info("==========================================================");

					log.info("!!!!!!!!!!!!!!!!!!!!! Search String !!!!!!!!!!!!!!!!!!!!!");
					log.info("searchString => "
							+ searchString.getBookSearchString());
					log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				}

				// Create docs to insert/update Solr
				SolrUtil.getInstance(solrURL).update(searchString.getBookId(),
						searchString.getBookSearchString().toUpperCase());
				log.info("update completed");

			} else {
				if (log.isEnabledFor(logPriorityLevel)) {
					log.info("**********************************************************");
					log.info("******************* Start Delete Book ********************");
					log.info("**********************************************************");
				}
				// delete delete book assets from solr
				Iterator<BookAssetDescription> bookAssetDesciptions = book
						.getBookAssetDescription().iterator();
				while (bookAssetDesciptions.hasNext() == true) {
					BookAssetDescription bookAssetDescription = bookAssetDesciptions
							.next();
					String key = book.getId() + "|"
							+ bookAssetDescription.getId();
					SolrUtil.getInstance(solrURL).delete(key);
					log.info("removed book asset" + key + " from Solr");
				}
				// delete document from solr
				SolrUtil.getInstance(solrURL).delete(book.getId());
				if (log.isEnabledFor(logPriorityLevel)) {
					log.info("removed book " + book.getId() + " from Solr");
					log.info("**********************************************************");
					log.info("********************* End Delete Book ********************");
					log.info("**********************************************************");
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	private String nvl(Object obj) {
		if (obj == null) {
			return BookSearchVO.BLANK;
		}

		return obj.toString().trim();
	}

}
