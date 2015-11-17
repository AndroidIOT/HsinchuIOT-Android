package org.slstudio.hsinchuiot.service.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slstudio.hsinchuiot.util.IOTLog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public abstract class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "freight.db";
	private static final int DATABASE_VERSION = 1;

	private Map<Class,Dao> daos=new HashMap<Class,Dao>();
	protected Context context;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context=context;
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			IOTLog.i(DatabaseHelper.class.getName(), "onCreate");
			Class[] clazzes = getTableClasses();
			for (Class clazz : clazzes) {
				TableUtils.createTable(connectionSource, clazz);
			}
			
		} catch (SQLException e) {
			IOTLog.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			IOTLog.i(DatabaseHelper.class.getName(), "onUpgrade");
			Class[] clazzes = getTableClasses();
			for (Class clazz : clazzes) {
				TableUtils.dropTable(connectionSource, clazz, true);
			}
			
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			IOTLog.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
	 * value.
	 */
	public Dao getDataDao(Class clazz) throws SQLException {
		Dao dao =daos.get(clazz);
		if (dao == null) {
			dao = getDao(clazz);
			daos.put(clazz, dao);
		}
		return dao;
	}


	@Override
	public void close() {
		super.close();
		daos.clear();
		//TODO release all table
	}
	
	protected abstract Class[] getTableClasses();
}

