package com.orders.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.orders.entity.Orders;
import com.orders.entity.Ordersdetails;
import com.user.entity.User;

@Repository
public class OrdersDao {

	@Resource
	private SessionFactory sessionfactory;

	public SessionFactory getSessionfactory() {
		return sessionfactory;
	}

	public void setSessionfactory(SessionFactory sessionfactory) {
		this.sessionfactory = sessionfactory;
	}
	
	public void insert(int productid,String name,Double price,int count,HttpSession session1,String date,int state){
		Session session = sessionfactory.openSession();
		Transaction tran=session.beginTransaction();
		User u = (User) session1.getAttribute("u");
		User user = session.get(User.class,new String(u.getAddress()));
		/*List<Orders> list1 = new ArrayList<Orders>();
		Query<Orders> query = session.createQuery("from Orders");
		List<Orders> list = query.list();
		Iterator<Orders> i = list.iterator();
		int k = 0;*/
		/*while(i.hasNext()){
			
			Orders o = i.next();
			String useraddress1 = o.getUser().getAddress();
			if(useraddress1==user.getAddress()){
				if(o.getProductid()==productid){
					int count1=o.getCount();
					count+=count1;
					o.setCount(count);
					session.update(o);
					tran.commit();
					session.close();
					break;
				}
			}
			k++;
		}*/
		//if(k>=list.size()){
			Orders o = new Orders();
			o.setProductid(productid);
			o.setName(name);
			o.setPrice(price);
			o.setCount(count);
			o.setDate(date);
			o.setState(state);
			user.getOrdersset().add(o);
			o.setUser(user);
			session.save(o);
			tran.commit();
			session.close();
		//}	
			
	}
	
	public List<Orders> get(HttpSession session1,int state){
		Session session = sessionfactory.openSession();
		Transaction tran = session.beginTransaction();
		User u = (User)session1.getAttribute("u");
		String address = u.getAddress();
		Query<Orders> query = session.createQuery("from Orders where state=?");
		query.setParameter(0, state);
		List list = query.list();
	
		tran.commit();
		return list;
	}
	
	public void xiaorders(String date){
		Session session =sessionfactory.openSession();
		Transaction tran = session.beginTransaction();

		Query query = session.createQuery("from Orders where date=?");
		query.setParameter(0,date);
		List list = query.list();
		Iterator i = list.iterator();
		while(i.hasNext()){
			Orders o = (Orders)i.next();
			o.setState(1);
			session.update(o);
		}
		
		tran.commit();
	}
	
	public void del(int productid,String date,int state,HttpSession session1){
		Session session = sessionfactory.openSession();
		Transaction tran = session.beginTransaction();
		Query query = session.createQuery("delete from Orders where productid=? and date=? and state=?");
		query.setParameter(0,productid);
		query.setParameter(1,date);
		query.setParameter(2,state);
		int count = query.executeUpdate();
		
		User user  = (User)session1.getAttribute("u");
		String address = user.getAddress();
		
		
		Query<Orders> query2 = session.createQuery("from Orders o where o.date=? and o.state=?");
		query2.setParameter(0, date);
		query2.setParameter(1,state);
		List<Orders> list = query2.list();
		System.out.println(list.size());
	
		if(list.size()==0){		
			Query<Ordersdetails> query1 = session.createQuery("delete from Ordersdetails o where o.useraddress=? and o.date=?");
			query1.setParameter(0,address);
			query1.setParameter(1,date);
			query1.executeUpdate();
			tran.commit();
		}else{
			tran.commit();
		}
		
	}
	
	public void addshouhuo(Ordersdetails od){
		Session session = sessionfactory.openSession();
		Transaction tran =session.beginTransaction();
		session.save(od);
		tran.commit();
		session.close();
		
	}
	
	public Ordersdetails xianshi(HttpSession session1){
		Session session = sessionfactory.openSession();
		Transaction tran =session.beginTransaction();
		/*String date = (String)session1.getAttribute("date");
		
		System.out.println("date1==================="+date);
		User user = (User)session1.getAttribute("u");
		User u= session.get(User.class, new String(user.getAddress()));
		String useraddress  = u.getAddress();
		Query<Ordersdetails> query = session.createQuery("from Ordersdetails where useraddress=? and date=?");
		query.setParameter(0, useraddress);
		query.setParameter(1,date);
		Ordersdetails od = query.uniqueResult();*/
		User user = (User)session1.getAttribute("u");
		User u= session.get(User.class, new String(user.getAddress()));
		String useraddress  = u.getAddress();
		List<Orders> list1 = new ArrayList<Orders>();
		String hql = "from Orders";
		Query<Orders> query = session.createQuery(hql,Orders.class);
		List<Orders> list = query.list();
		Iterator<Orders> i = list.iterator();
		while(i.hasNext()){
			Orders o = i.next();
			if(o.getUser().getAddress()==useraddress){
				list1.add(o);
			}
		}
		if(list1.size()>=2){
			Orders o = list1.get(list1.size()-2);
			String date = o.getDate();
			Query<Ordersdetails> query2 = session.createQuery("from Ordersdetails where useraddress=? and date=?");
			query2.setParameter(0,useraddress);
			query2.setParameter(1,date);
			Ordersdetails od = query2.uniqueResult();
			tran.commit();
			return od;
		}else{
			tran.commit();
			return null;
		}
		 
		
		/*String sql = "select * from orders where useraddress="+useraddress;
		NativeQuery<Orders> query = session.createNativeQuery(sql,Orders.class);
		List<Orders> list = query.list();
		Orders o = list.get(list.size()-2);
		String date = o.getDate();
		
		Query<Ordersdetails> query2 = session.createQuery("from Ordersdetails where useraddress=? and date=?");
		query2.setParameter(0,useraddress);
		query2.setParameter(1,date);
		Ordersdetails od = query2.uniqueResult();*/
		
	}
	
	public void jian(int productId,String date,int state){
		Session session = sessionfactory.openSession();
		Transaction tran =session.beginTransaction();
		String hql = "from Orders where productId=? and date=? and state=?";
		Query<Orders> query = session.createQuery(hql,Orders.class);
		query.setParameter(0, productId);
		query.setParameter(1,date);
		query.setParameter(2,state);
		Orders o = (Orders)query.uniqueResult();
		int count = o.getCount();
		if(count==1){
			count=1;
		}else{
			count--;
		}
		o.setCount(count);
		session.update(o);
		tran.commit();
	}
	
	public void jia(int productId,String date,int state){
		Session session = sessionfactory.openSession();
		Transaction tran =session.beginTransaction();
		String hql = "from Orders where productId=? and date=? and state=?";
		Query<Orders> query = session.createQuery(hql,Orders.class);
		query.setParameter(0, productId);
		query.setParameter(1,date);
		query.setParameter(2,state);
		Orders o = (Orders)query.uniqueResult();
		int count = o.getCount();
		count++;
		o.setCount(count);
		session.update(o);
		tran.commit();
	}
	
	
}
