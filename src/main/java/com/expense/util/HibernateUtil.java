package com.expense.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.expense.config.HibernateConfig;

public class HibernateUtil {
		private HibernateUtil() {}
		
		private static SessionFactory sessionFactory = null;
		
		
		public static SessionFactory getSessionFactory() {
			
			try {
				if(sessionFactory == null) {
					
					sessionFactory = new Configuration()
										.configure(HibernateConfig.FILE_NAME)
										.buildSessionFactory();
					
				}
			}
			catch(Exception e) {
				System.out.println("\nSession Factory Object Not Creating!!!!\n");
				e.printStackTrace();
				return null;
			}
			
			return sessionFactory;
		}
		
		
		public static void closeSessionFactory() {
			try
			{
				if(sessionFactory!=null)
					sessionFactory.close();
			}
			catch(Exception e)
			{
				System.out.println("SessionFactory Not Closed !!");
				e.printStackTrace();
			}
		}
		
}
