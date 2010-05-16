package it.rainbowbreeze.smsforfree.logic;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.ResultOperation;
import it.rainbowbreeze.smsforfree.domain.SmsProvider;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class SendMessageTask
	extends AsyncTask<String, Void, ResultOperation> {

	//---------- Ctors
	public SendMessageTask(Context context, SmsProvider provider, String servideId)
	{
		mContext = context;
		mProvider = provider;
		mServiceId = servideId;
	}
	
	//---------- Private fields
	private Context mContext;
	private ProgressDialog mDialog;
	private SmsProvider mProvider;
	private String mServiceId;

	//---------- Public properties

	//---------- Public methods

	//---------- Private methods
	@Override
	protected void onPreExecute() {
		mDialog = new ProgressDialog(mContext);
		mDialog.setMessage(mContext.getString(R.string.common_msg_sendingMessage));
		mDialog.show();
	}
	
	protected ResultOperation doInBackground(String... params)
	{
		//check parameters
		if (params.length != 2)
			return new ResultOperation(new IllegalArgumentException(mContext.getString(R.string.common_msg_errorWrongDestinationOrBody)));
			
		String destination = params[0];
		String messageBody = params[1];
		
		return mProvider.sendMessage(mServiceId, destination, messageBody);
	}

	@Override
	protected void onPostExecute(ResultOperation result) {
		if (mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}
}
