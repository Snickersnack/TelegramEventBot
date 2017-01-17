package persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	private static final SessionFactory sessionFactory = buildSessionFactory();

	private static SessionFactory buildSessionFactory() {
		try {
			
			Configuration cfg = new Configuration();
			cfg.configure("hibernate.cfg.xml"); //hibernate config xml file name
			
	      String dbName = System.getenv("RDS_DB_NAME");
	      String userName = System.getenv("RDS_USERNAME");
	      String password = System.getenv("RDS_PASSWORD");
	      String hostname = System.getenv("RDS_HOSTNAME");
	      String port = System.getenv("RDS_PORT");

	      String jdbcUrl = "jdbc:postgresql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
		    System.out.println(jdbcUrl);
			cfg.getProperties().setProperty("hibernate.connection.password", password);
			cfg.getProperties().setProperty("hibernate.connection.username", userName);
			cfg.getProperties().setProperty("hibernate.connection.url", jdbcUrl);

			return cfg.buildSessionFactory();
			
			
			
//			Configuration cfg = new Configuration();
//			cfg.configure("hibernate.cfg.xml"); //hibernate config xml file name
//
//
//			cfg.getProperties().setProperty("hibernate.connection.password", "postgres");
//			cfg.getProperties().setProperty("hibernate.connection.username", "postgres");
//
//
//			return cfg.buildSessionFactory();

		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}
}
