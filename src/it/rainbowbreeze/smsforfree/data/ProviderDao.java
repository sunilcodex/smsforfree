/**
 * 
 */
package it.rainbowbreeze.smsforfree.data;

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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
	private final static String XMLNODE_PARAMETERSARRAY = "ParametersArray";
	private final static String XMLNODE_PARAMETER = "Parameter";
	private final static String XMLNODE_PARAMETERDESC = "Desc";
	private final static String XMLNODE_PARAMETERVALUE = "Value";
	private final static String XMLATTRIBUTE_PARAMETERSNUMBER = "ParametersNumber";
	

	
	
	//---------- Public properties


	
	
	//---------- Public methods


	/**
	 * Persist a provider parameters into a file in xml format
	 * 
	 * Could throws FileNotFoundException, IOException, IllegalArgumentException, IllegalStateException
	 */
	public ResultOperation saveProviderParameters(Context context, String filename, SmsProvider provider)
	{
		ResultOperation res = new ResultOperation();
		
		try {
			String xmlExport = serializeProviderParameters(provider);
			persistFile(context, filename, xmlExport);
		} catch (Exception e) {
			res.setException(e);
		}
		
		if (!res.HasErrors())
			//all went good, no errors to return
			res.setResultAsBoolean(true);
		return res;
	}
	
	/**
	 * Persist a provider templates into a file in xml format
	 * 
	 * Could throws FileNotFoundException, IOException, IllegalArgumentException, IllegalStateException
	 */
	public ResultOperation saveProviderTemplates(Context context, String filename, SmsProvider provider)
	{
		ResultOperation res = new ResultOperation();

		try {
			String xmlExport = serializeProviderTemplates(provider);
			persistFile(context, filename, xmlExport);
		} catch (Exception e) {
			res.setException(e);
			return res;
		}
		
		if (!res.HasErrors())
			//all went good, no errors to return
			res.setResultAsBoolean(true);
		return res;
	}


	/**
	 * Persist a provider subservices into a file in xml format
	 * 
	 * Could throws FileNotFoundException, IOException, IllegalArgumentException, IllegalStateException
	 */
	public ResultOperation saveProviderSubservices(Context context, String filename, SmsProvider provider)
	{
		ResultOperation res = new ResultOperation();


		try {
			String xmlExport = serializeProviderSubservices(provider);
			persistFile(context, filename, xmlExport);
		} catch (Exception e) {
			res.setException(e);
			return res;
		}
		
		if (!res.HasErrors())
			//all went good, no errors to return
			res.setResultAsBoolean(true);
		return res;
	}


	public ResultOperation loadProviderParameters(InputStream in, SmsProvider provider)
	{
		int parametersIndex = -1;
		ResultOperation res = new ResultOperation();
		
		XmlPullParser parser = Xml.newPullParser();
		
		try {
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
		} catch (IOException e) {
			res.setException(e);
			return res;
		} catch (XmlPullParserException e) {
			res.setException(e);
			return res;
		}
		
		res.setResultAsBoolean(true);
		return res;
	}
	
	
	public ResultOperation loadProvidersTemplates(InputStream in, SmsMultiProvider provider)
	{
		ResultOperation res = new ResultOperation();
		XmlPullParser parser = Xml.newPullParser();

		try {
			parser.setInput(in, null);
			List<SmsService> templates = loadServiceData(parser, XMLNODE_TEMPLATE);
			provider.setAllTemplateSubservices(templates);
		} catch (IOException e) {
			res.setException(e);
			return res;
		} catch (XmlPullParserException e) {
			res.setException(e);
			return res;
		}
		
		res.setResultAsBoolean(true);
		return res;
	}
	

	public ResultOperation loadProvidersSubservices(InputStream in, SmsMultiProvider provider)
	{
		ResultOperation res = new ResultOperation();
		XmlPullParser parser = Xml.newPullParser();

		try {
			parser.setInput(in, null);
			List<SmsService> subservices = loadServiceData(parser, XMLNODE_SUBSERVICE);
			provider.setAllConfiguredSubservices(subservices);
		} catch (IOException e) {
			res.setException(e);
			return res;
		} catch (XmlPullParserException e) {
			res.setException(e);
			return res;
		}
		
		res.setResultAsBoolean(true);
		return res;
	}
	
	
	

	//---------- Private methods

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
		writeServiceData(serializer, provider, XMLNODE_PROVIDER);
		serializer.endDocument();
		
		return writer.toString();
	}
	
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
				writeServiceData(serializer, template, XMLNODE_TEMPLATE);
			}
		}
		
		//close document
		serializer.endTag("", XMLNODE_TEMPLATESARRAY);
		serializer.endDocument();
		
		return writer.toString();
	}
	
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
				writeServiceData(serializer, subservice, XMLNODE_SUBSERVICE);
			}
		}
		
		//close document
		serializer.endTag("", XMLNODE_SUBSERVICESARRAY);
		serializer.endDocument();
		
		return writer.toString();
	}
	

	/**
	 * Serialize the SmsProvider data into an XML object
	 * 
	 * @param service
	 * @param serializer
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	private void writeServiceData(XmlSerializer serializer, SmsService service, String openingTag)
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
	
	
	private List<SmsService> loadServiceData(XmlPullParser parser, String xmlNode_openingTag)
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
	

	private String parseString(String input) {
		return TextUtils.isEmpty(input) ? "" : input;
		
	}

	/**
	 * Persist a string into a file
	 * 
	 * @param context
	 * @param filename
	 * @param content
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void persistFile(Context context, String filename, String content)
		throws FileNotFoundException, IOException
	{
		FileOutputStream fos = null;
		fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
		fos.write(content.getBytes());
		fos.close();
		fos = null;
	}

}
