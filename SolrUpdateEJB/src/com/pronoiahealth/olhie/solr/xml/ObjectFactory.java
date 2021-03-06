//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.13 at 11:26:34 AM EST 
//


package com.pronoiahealth.olhie.solr.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Book_QNAME = new QName("", "book");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link com.pronoiahealth.olhie.solr.xml.BookAsset }
     * 
     */
    public BookAsset createBookAsset() {
        return new BookAsset();
    }

    /**
     * Create an instance of {@link com.pronoiahealth.olhie.solr.xml.User }
     * 
     */
    public User createUser() {
        return new User();
    }

    /**
     * Create an instance of {@link com.pronoiahealth.olhie.solr.xml.Book }
     * 
     */
    public Book createBook() {
        return new Book();
    }

    /**
     * Create an instance of {@link BookAssetDescription }
     * 
     */
    public BookAssetDescription createBookAssetDescription() {
        return new BookAssetDescription();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link com.pronoiahealth.olhie.solr.xml.Book }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "book")
    public JAXBElement<Book> createBook(Book value) {
        return new JAXBElement<Book>(_Book_QNAME, Book.class, null, value);
    }

}
