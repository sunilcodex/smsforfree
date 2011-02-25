/*
 * Copyright (C) 2010 Felix Bechstein
 * 
 * This file is part of WebSMS.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */
package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.libs.helper.RainbowStringHelper;
import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.common.App;
import it.rainbowbreeze.smsforfree.data.ContactsDao;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

/**
 * CursorAdapter getting Name, Phone from DB.
 * 
 * @author flx and Alfredo "Rainbowbreeze" Morresi
 */
public class ContactsPhonesAdapter extends ResourceCursorAdapter {
	/** Global ContentResolver. */
	private ContentResolver mContentResolver;

	/** {@link ContactsWrapper} to use. */
	private static final ContactsDao WRAPPER = ContactsDao.instance();

	/**
	 * Constructor.
	 * 
	 * @param context context
	 */
	public ContactsPhonesAdapter(final Context context) {
		super(context, R.layout.layoutcontactnumbers, null);
		this.mContentResolver = context.getContentResolver();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void bindView(final View view, final Context context,
			final Cursor cursor) {
		((TextView) view.findViewById(R.id.layoutcontactnumbers_txtName)).setText(cursor
				.getString(ContactsDao.FILTER_INDEX_NAME));
		((TextView) view.findViewById(R.id.layoutcontactnumbers_txtNumber)).setText(cursor
				.getString(ContactsDao.FILTER_INDEX_NUMBER));
		int i = cursor.getInt(ContactsDao.FILTER_INDEX_TYPE) - 1;
		String[] types = context.getResources().getStringArray(
				android.R.array.phoneTypes);
		if (i >= 0 && i < types.length) {
			((TextView) view.findViewById(R.id.layoutcontactnumbers_txtType)).setText(types[i]);
		} else {
			((TextView) view.findViewById(R.id.layoutcontactnumbers_txtType)).setText("");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String convertToString(final Cursor cursor) {
		String name = cursor.getString(ContactsDao.FILTER_INDEX_NAME);
		String number = cursor.getString(ContactsDao.FILTER_INDEX_NUMBER);
		if (name == null || name.length() == 0) {
			return RainbowStringHelper.cleanPhoneNumber(number);
		}
		return name + " <" + RainbowStringHelper.cleanPhoneNumber(number) + '>';
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Cursor runQueryOnBackgroundThread(// .
			final CharSequence constraint) {
		String where = null;
		if (constraint != null) {
			StringBuilder s = new StringBuilder(WRAPPER.getContactNumbersContentWhere(constraint.toString()));
			if (App.i().getShowOnlyMobileNumbers()) {
				s.insert(0, "(");
				s.append(WRAPPER.getContactNumbersMobilesOnlyString());
			}

			where = s.toString();
		}

		return this.mContentResolver.query(
		        WRAPPER.getContentUri(),
		        WRAPPER.getContactNumbersContentProjection(),
		        where,
		        null,
		        WRAPPER.getContactNumbersContentSort());
	}
}
