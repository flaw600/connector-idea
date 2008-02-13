
package com.atlassian.theplugin.crucible.api.soap.xfire.review;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import com.atlassian.theplugin.crucible.api.soap.xfire.review.RpcReviewServiceName;

/**
 * This class was generated by Apache CXF (incubator) 2.0.4-incubator
 * Tue Feb 12 17:36:19 CET 2008
 * Generated source version: 2.0.4-incubator
 * 
 */

@WebServiceClient(name = "Review", targetNamespace = "http://rpc.spi.crucible.atlassian.com/", wsdlLocation = "file:/C:/Dev/ThePlugin/src/main/java/com/atlassian/theplugin/crucible/api/soap/xfire/review/review.wsdl")
public class Review extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://rpc.spi.crucible.atlassian.com/", "Review");
    public final static QName ReviewPort = new QName("http://rpc.spi.crucible.atlassian.com/", "ReviewPort");
    static {
        URL url = null;
        try {
            url = new URL("file:/C:/Dev/ThePlugin/src/main/java/com/atlassian/theplugin/crucible/api/soap/xfire/review/review.wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from file:/C:/Dev/ThePlugin/src/main/java/com/atlassian/theplugin/crucible/api/soap/xfire/review/review.wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public Review(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public Review(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public Review() {
        super(WSDL_LOCATION, SERVICE);
    }

    /**
     * 
     * @return
     *     returns ReviewPort
     */
    @WebEndpoint(name = "ReviewPort")
    public RpcReviewServiceName getReviewPort() {
        return (RpcReviewServiceName)super.getPort(ReviewPort, RpcReviewServiceName.class);
    }

}
