/**
 * 
 */
package it.rainbowbreeze.smsforfree.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsConfigurableService;
import it.rainbowbreeze.smsforfree.domain.SmsMultiProvider;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import android.content.Context;
import android.text.TextUtils;
import android.util.Xml;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ProviderDao
{
	//---------- Ctors

	//---------- Private fields
	private final static String XMLNODE_PROVIDER = "Provider";
	private final static String XMLNODE_TEMPLATE = "Template";
	private final static String XMLNODE_TEMPLATESARRAY = "TemplatesArray";
	private final static String XMLNODE_SUBSERVICESARRAY = "SubservicesArray";
	private final static String XMLNODE_SUBSERVICE = "Subservice";
	private final static String XMLNODE_ID = "Id";
	private final static String XMLNODE_NAME = "Name";
	private final static String XMLNODE_PARAMETERSNUMBER = "ParametersNumber";
	private final static String XMLNODE_MAXMESSAGELENGHT = "MaxMessageLenght";
	private final static String XMLNODE_TEMPLATEID = "TemplateId";
	private final static String XMLNODE_DESCRIPTION = "Description";
	private final static String XMLNODE_PARAMETERSARRAY = "ParametersArray";
	private final static String XMLNODE_PARAMETER = "Parameter";
	private final static String XMLNODE_PARAMETERDESC = "Desc";
	private final static String XMLNODE_PARAMETERVALUE = "Value";
	private final static String XMLATTRIBUTE_PARAMETERSNUMBER = "ParametersNumber";
	
	private final static int PROVIDERDATA_PARAMETERS = 1;
	private final static int PROVIDERDATA_TEMPLATES = 2;
	private final static int PROVIDERDATA_SUBSERVICES = 3;
	
	
	
	//---------- Public properties


	
	
	//---------- Public methods


	/**
	 * Persist a provider parameters into a file in xml format
	 * 
	 * Could throws FileNotFoundException, IOException, IllegalArgumentException, IllegalStateException
	 */
	public ResultOperation saveProviderParameters(Context context, String filename, SmsProvider provider)
	{
		return saveProviderData(context, filename, provider, PROVIDERDATA_PARAMETERS);
	}

	/**
	 * Persist a provider templates into a file in xml format
	 * 
	 * Could throws FileNotFoundException, IOException, IllegalArgumentException, IllegalStateException
	 */
	public ResultOperation saveProviderTemplates(Context context, String filename, SmsProvider provider)
	{
		return saveProviderData(context, filename, provider, PROVIDERDATA_TEMPLATES);
	}

	/**
	 * Persist a provider subservices into a file in xml format
	 * 
	 * Could throws FileNotFoundException, IOException, IllegalArgumentException, IllegalStateException
	 */
	public ResultOperation saveProviderSubservices(Context context, String filename, SmsProvider provider)
	{
		return saveProviderData(context, filename, provider, PROVIDERDATA_SUBSERVICES);
	}


	/**
	 * Loads provider parameters from an xml file
	 * 
	 * Could throws FileNotFoundException, IOException, XmlPullParserException
	 */
	public ResultOperation loadProviderParameters(Context context, String filename, SmsProvider provider)
	{
		return loadProviderData(context, filename, provider, PROVIDERDATA_PARAMETERS);
	}

	/**
	 * Loads provider templates from an xml file
	 * 
	 * Could throws FileNotFoundException, IOException, XmlPullParserException
	 */
	public ResultOperation loadProviderTemplates(Context context, String filename, SmsProvider provider)
	{
		return loadProviderData(context, filename, provider, PROVIDERDATA_TEMPLATES);
	}

	/**
	 * Loads provider configured subservices from an xml file
	 * 
	 * Could throws FileNotFoundException, IOException, XmlPullParserException
	 */
	public ResultOperation loadProviderSubservices(Context context, String filename, SmsProvider provider)
	{
		return loadProviderData(context, filename, provider, PROVIDERDATA_SUBSERVICES);
	}

	
	

	//---------- Private methods

	/**
	 * Persist provider data into an xml file
	 * 
	 * @param context
	 * @param filename
	 * @param provider provider to save
	 * @param dataToSave part of the provider to save
	 * @return
	 */
	private ResultOperation saveProviderData(
			Context context,
			String filename,
			SmsProvider provider,
			int dataToSave)
	{
		ResultOperation res = new ResultOperation(true);
		
		FileOutputStream fos = null;
		try {
			String xmlExport = null;
			switch (dataToSave) {
			case PROVIDERDATA_PARAMETERS:
				xmlExport = serializeProviderParameters(provider);
				break;
			case PROVIDERDATA_TEMPLATES:
				xmlExport = serializeProviderTemplates(provider);
				break;
			case PROVIDERDATA_SUBSERVICES:
				xmlExport = serializeProviderSubservices(provider);
				break;
			}

			//save file
			fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			fos.write(xmlExport.getBytes());

		} catch (Exception e) {
			res.setException(e);
			
		} finally {
			if (null != fos) {
				try {
					fos.close();
					fos = null;
				} catch (IOException e) {
					res.setException(e);
				}
			}
		}
		
		return res;
	}
	
	/**
	 * Creates an xml representation of provide's parameters
	 * 
	 * @param provider
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	private String serializeProviderParameters(SmsProvider provider)
		throws IllegalArgumentException, IllegalStateException, IOException
	{
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		serializeServiceData(serializer, provider, XMLNODE_PROVIDER);
		serializer.endDocument();
		
		return writer.toString();
	}
	
	
	/**
	 * Creates an xml representation of provide's templates
	 * 
	 * @param provider
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	private String serializeProviderTemplates(SmsProvider provider)
		throws IllegalArgumentException, IllegalStateException, IOException
	{
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		
		//open document
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);

		//write template data
		serializer.startTag("", XMLNODE_TEMPLATESARRAY);
		if (null != provider.getAllTemplate()) {
			for(SmsService template : provider.getAllTemplate()) {
				serializeServiceData(serializer, template, XMLNODE_TEMPLATE);
			}
		}
		
		//close document
		serializer.endTag("", XMLNODE_TEMPLATESARRAY);
		serializer.endDocument();
		
		return writer.toString();
	}

	
	/**
	 * Creates an xml representation of provide's subservices
	 * 
	 * @param provider
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	private String serializeProviderSubservices(SmsProvider provider)
		throws IllegalArgumentException, IllegalStateException, IOException
	{
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);

		//write subservices data
		serializer.startTag("", XMLNODE_SUBSERVICESARRAY);
		if (null != provider.getAllSubservices()) {
			for(SmsService subservice : provider.getAllSubservices()) {
				serializeServiceData(serializer, subservice, XMLNODE_SUBSERVICE);
			}
		}
		
		//close document
		serializer.endTag("", XMLNODE_SUBSERVICESARRAY);
		serializer.endDocument();
		
		return writer.toString();
	}
	

	/**
	 * Serialize SmsService data into an XML object
	 * 
	 * @param service
	 * @param serializer
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	private void serializeServiceData(XmlSerializer serializer, SmsService service, String openingTag)
			throws IOException, IllegalArgumentException, IllegalStateException
	{
		serializer.startTag("", openingTag);
		serializer.attribute("", XMLATTRIBUTE_PARAMETERSNUMBER, String.valueOf(service.getParametersNumber()));

		//service data
		serializer.startTag("", XMLNODE_ID);
		serializer.text(service.getId());
		serializer.endTag("", XMLNODE_ID);
		
		serializer.startTag("", XMLNODE_NAME);
		serializer.text(service.getName());
		serializer.endTag("", XMLNODE_NAME);
		
		serializer.startTag("", XMLNODE_MAXMESSAGELENGHT);
		serializer.text(String.valueOf(service.getMaxMessageLenght()));
		serializer.endTag("", XMLNODE_MAXMESSAGELENGHT);
		
		serializer.startTag("", XMLNODE_TEMPLATEID);
		serializer.text(parseString(service.getTemplateId()));
		serializer.endTag("", XMLNODE_TEMPLATEID);
		
		serializer.startTag("", XMLNODE_DESCRIPTION);
		serializer.text(parseString(service.getDescription()));
		serializer.endTag("", XMLNODE_DESCRIPTION);
		
		serializer.startTag("", XMLNODE_PARAMETERSNUMBER);
		serializer.text(String.valueOf(service.getParametersNumber()));
		serializer.endTag("", XMLNODE_PARAMETERSNUMBER);
		
		serializer.startTag("", XMLNODE_PARAMETERSARRAY);
		for(int i = 0; i < service.getParametersNumber(); i++) {
			serializer.startTag("", XMLNODE_PARAMETER);
				serializer.startTag("", XMLNODE_PARAMETERDESC);
				serializer.text(parseString(service.getParameterDesc(i)));
				serializer.endTag("", XMLNODE_PARAMETERDESC);
				serializer.startTag("", XMLNODE_PARAMETERVALUE);
				serializer.text(parseString(service.getParameterValue(i)));
				serializer.endTag("", XMLNODE_PARAMETERVALUE);
			serializer.endTag("", XMLNODE_PARAMETER);
		}
		serializer.endTag("", XMLNODE_PARAMETERSARRAY);
		
		serializer.endTag("", openingTag);
	}
	
	
	private String parseString(String input) {
		return TextUtils.isEmpty(input) ? "" : input;
		
	}

	
	/**
	 * Loads provider data from xml files.
	 * Throws FileNotFoundException, XmlPullParserException, IOException
	 * 
	 * @param context
	 * @param fileName
	 * @param provider
	 * @param datatosave
	 * @return
	 */
	private ResultOperation loadProviderData(
			Context context,
			String fileName,
			SmsProvider provider,
			int dataToLoad)
	{
		ResultOperation res = new ResultOperation(true);
		FileInputStream fis = null;

		//checks if file exists
		File file = context.getFileStreamPath(fileName);
		if (!file.exists()) {
			//if file doesn't exist, no problem, reset data
			return res;
		}
		
		try {
			fis = context.openFileInput(fileName);
			switch (dataToLoad) {
			case PROVIDERDATA_PARAMETERS:
				deserializeProviderParameters(fis, provider);
				break;
			case PROVIDERDATA_TEMPLATES:
				deserializeProvidersTemplates(fis, (SmsMultiProvider) provider);
				break;
			case PROVIDERDATA_SUBSERVICES:
				deserializeProvidersSubservices(fis, (SmsMultiProvider) provider);
				break;
			}
		} catch (FileNotFoundException e) {
			res.setException(e);
		} catch (XmlPullParserException e) {
			res.setException(e);
		} catch (IOException e) {
			res.setException(e);
		} finally {
			if (null != fis) {
				try {
					fis.close();
					fis = null;
				} catch (IOException e) {
					res.setException(e);
				}
			}
		}

		return res;
	}

	/**
	 * Deserializes provider parameters from xml input
	 */
	private void deserializeProviderParameters(InputStream in, SmsProvider provider)
		throws XmlPullParserException, IOException
	{
		int parametersIndex = -1;
		XmlPullParser parser = Xml.newPullParser();

		parser.setInput(in, null);
		int eventType = parser.getEventType();
		
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
				
			case XmlPullParser.START_TAG:
				name = parser.getName();
				
				if (name.equalsIgnoreCase(XMLNODE_PARAMETER)) {
					parametersIndex++;
				} else if (name.equalsIgnoreCase(XMLNODE_PARAMETERVALUE)) {
					provider.setParameterValue(parametersIndex, parser.nextText());
				}
				break;
				
			case XmlPullParser.END_TAG:
				break;
			
			}
			
			eventType = parser.next();
		}
	}

	/**
	 * Deserializes provider templates from xml input
	 */
	private void deserializeProvidersTemplates(InputStream in, SmsMultiProvider provider)
		throws XmlPullParserException, IOException
	{
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(in, null);
		List<SmsService> templates = deserializeServiceData(parser, XMLNODE_TEMPLATE);
		provider.setAllTemplateSubservices(templates);
	}
	
	
	/**
	 * Deserializes provider configured subservices from xml input
	 */
	private void deserializeProvidersSubservices(InputStream in, SmsMultiProvider provider)
		throws XmlPullParserException, IOException
	{
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(in, null);
		List<SmsService> subservices = deserializeServiceData(parser, XMLNODE_SUBSERVICE);
		provider.setAllConfiguredSubservices(subservices);
	}

	
	/**
	 * Deserializes a SmsService from xml input
	 */
	private List<SmsService> deserializeServiceData(XmlPullParser parser, String xmlNode_openingTag)
	throws XmlPullParserException, IOException
	{
		List<SmsService> services = new ArrayList<SmsService>();
		SmsConfigurableService service = null;
		int eventType = parser.getEventType();
		int parametersIndex = -1;
		
		boolean done = false;
		while (eventType != XmlPullParser.END_DOCUMENT && !done) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
				
			case XmlPullParser.START_TAG:
				name = parser.getName();
				
				if (name.equalsIgnoreCase(xmlNode_openingTag)) {
					//get parameters number
					String attributesNumbers = parser.getAttributeValue(0);
					int attributes = Integer.parseInt(attributesNumbers);
					//initialized the service
					service = new SmsConfigurableService(attributes);
				} else if (name.equalsIgnoreCase(XMLNODE_ID)) {
					service.setId(parser.nextText());
				} else if (name.equalsIgnoreCase(XMLNODE_NAME)) {
					service.setName(parser.nextText());
				} else if (name.equalsIgnoreCase(XMLNODE_MAXMESSAGELENGHT)) {
					service.setMaxMessageLenght(Integer.parseInt(parser.nextText()));
				} else if (name.equalsIgnoreCase(XMLNODE_TEMPLATEID)) {
					service.setTemplateId(parser.nextText());
				} else if (name.equalsIgnoreCase(XMLNODE_DESCRIPTION)) {
					service.setDescription(parser.nextText());
					
				} else if (name.equalsIgnoreCase(XMLNODE_PARAMETERSARRAY)) {
					parametersIndex = -1;
				} else if (name.equalsIgnoreCase(XMLNODE_PARAMETER)) {
					parametersIndex++;
				} else if (name.equalsIgnoreCase(XMLNODE_PARAMETERDESC)) {
					service.setParameterDesc(parametersIndex, parser.nextText());
				} else if (name.equalsIgnoreCase(XMLNODE_PARAMETERVALUE)) {
					service.setParameterValue(parametersIndex, parser.nextText());
				}
				break;
				
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase(xmlNode_openingTag))
					services.add(service);
				break;
			
			}
			eventType = parser.next();
		}
		return services;
	}
}
