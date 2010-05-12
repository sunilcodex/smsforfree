/**
 * 
 */
package it.rainbowbreeze.smsforfree.data;

import java.io.IOException;
import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import it.rainbowbreeze.smsforfree.domain.SmsService;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Xml;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ProviderPreferencesDao
	extends BasePreferencesDao
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
	

	
	
	//---------- Public properties


	
	
	//---------- Public methods
	public ResultOperation saveProvidersPreferences(SmsProvider provider, String providerFileName)
	{
		ResultOperation res = new ResultOperation();

		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", XMLNODE_PROVIDER);

			//write service data
			writeServiceData(serializer, provider);
			
			//write template data
			serializer.startTag("", XMLNODE_TEMPLATESARRAY);
			if (null != provider.getAllTemplateSubservices()) {
				for(SmsService template : provider.getAllTemplateSubservices()) {
					writeServiceData(serializer, template, XMLNODE_TEMPLATE);
				}
			}
			serializer.endTag("", XMLNODE_TEMPLATESARRAY);
				 
			//write subservices data
			serializer.startTag("", XMLNODE_SUBSERVICESARRAY);
			if (null != provider.getAllConfiguredSubservices()) {
				for(SmsService subservice : provider.getAllConfiguredSubservices()) {
					writeServiceData(serializer, subservice, XMLNODE_SUBSERVICE);
				}
			}
			serializer.endTag("", XMLNODE_SUBSERVICESARRAY);

			serializer.endTag("", XMLNODE_PROVIDER);
			
			serializer.endDocument();
		} catch (Exception e) {
			res.setException(e);
			return res;
		}
		
		//save xml to file

		res.setResultAsString(writer.toString());
		return res;
	}

	
	public ResultOperation loadProvidersPreferences(String providerFileName)
	{
		ResultOperation res = new ResultOperation();
		
		return null;
	}

	//---------- Private methods

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.data.BasePreferencesDao#backupProperties(android.content.SharedPreferences.Editor)
	 */
	@Override
	protected void backupProperties(Editor editorBackup) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.data.BasePreferencesDao#getPreferencesKey()
	 */
	@Override
	protected String getPreferencesKey() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.rainbowbreeze.smsforfree.data.BasePreferencesDao#restoreProperties(android.content.SharedPreferences)
	 */
	@Override
	protected void restoreProperties(SharedPreferences settingsBackup) {
		// TODO Auto-generated method stub

	}

	/**
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
		//service data
		writeServiceData(serializer, service);
		serializer.endTag("", openingTag);
	}
	
	/**
	 * @param service
	 * @param serializer
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	private void writeServiceData(XmlSerializer serializer, SmsService service)
			throws IOException, IllegalArgumentException, IllegalStateException {
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
	}
	

	private String parseString(String input) {
		return TextUtils.isEmpty(input) ? "" : input;
		
	}
}
