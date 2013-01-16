package org.chardev;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.chardev.cjt.DBCParser;

public class DevelChardevDB {
	private static Connection databaseConnection;
	private static boolean connectToDatabase( String url ) {
		try {
			String driverClass = "org.gjt.mm.mysql.Driver";
			Class.forName(driverClass).newInstance();
			databaseConnection = DriverManager.getConnection(url, "root", "");
			return true;
		} catch (SQLException sqle) {
			System.out.println(sqle);
		} catch (InstantiationException ie) {
			System.out.println(ie);
		} catch (IllegalAccessException iae) {
			System.out.println(iae);
		} catch (ClassNotFoundException cnfe) {
			System.out.println(cnfe);
		}
		return false;
	}
	private static final String dbs[] = new String[]{
		"jdbc:mysql://localhost:3306/chardev_mop?",
		"jdbc:mysql://localhost:3306/chardev_mop_fr?",
		"jdbc:mysql://localhost:3306/chardev_mop_de?",
		"jdbc:mysql://localhost:3306/chardev_mop_es?",
		"jdbc:mysql://localhost:3306/chardev_mop_ru?"
	};
	private static final String basePaths[] = new String[]{
		"Y:/chardev/mop/DBFilesClient/",
		"Y:/chardev/mop/fr/DBFilesClient/",
		"Y:/chardev/mop/de/DBFilesClient/",
		"Y:/chardev/mop/es/DBFilesClient/",
		"Y:/chardev/mop/ru/DBFilesClient/"
	};

	public static void main(String[] args) {
		connectToDatabase(dbs[0]);
		DBCParser p;
		p = new DBCParser(databaseConnection, "y:\\chardev\\mop\\dbfilesclient\\chrspecialization.dbc", "chrspecialization");
		p.parse();
	}
}
