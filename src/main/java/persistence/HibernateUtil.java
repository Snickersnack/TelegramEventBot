package persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	private static final SessionFactory sessionFactory = buildSessionFactory();

	private static SessionFactory buildSessionFactory() {
		try {
			
			Configuration cfg = new Configuration();
			cfg.configure("hibernate.cfg.xml"); //hibernate config xml file name


			cfg.getProperties().setProperty("hibernate.connection.password", "postgres");
			cfg.getProperties().setProperty("hibernate.connection.username", "postgres");


//			    return DriverManager.getConnection(dbUrl, username, password);
			// Create the SessionFactory from hibernate.cfg.xml
//			return new Configuration().configure().buildSessionFactory();
			return cfg.buildSessionFactory();

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
