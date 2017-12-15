package com.background.dorders.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.orders.entity.Orders;
import com.orders.entity.Ordersdetails;

@Repository
public class DordersDao {

	@Resource
	private SessionFactory sessionfactory;
	
	public List<Ordersdetails> findAll(){
		Session session =sessionfactory.openSession();
		Transaction tran=session.beginTransaction();
		NativeQuery<Ordersdetails> query = session.createNativeQuery("select * from ordersdetails", Ordersdetails.class);
		List<Ordersdetails> list = query.list();
		tran.commit();
		return list;
	}
	
	public List<Orders> findAll1(){
		Session session =sessionfactory.openSession();
		Transaction tran=session.beginTransaction();
		NativeQuery<Orders> query = session.createNativeQuery("select * from orders", Orders.class);
		List<Orders> list = query.list();
		tran.commit();
		return list;
	}
	
	public void del(String useraddress,String date,int productid,int state){
		Session session =sessionfactory.openSession();
		Transaction tran = session.beginTransaction();
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/design?useUnicode=true&characterEncoding=UTF-8","root","");
			PreparedStatement pstm = con.prepareStatement("delete from orders where useraddress=? and date=? and productid=? and state=?");
			pstm.setString(1,useraddress);
			pstm.setString(2,date);
			pstm.setInt(3,productid);
			pstm.setInt(4,state);
			pstm.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
	
		Query<Orders> query2 = session.createQuery("from Orders o where o.state=? and o.date=?", Orders.class);
		query2.setParameter(0,state);
		query2.setParameter(1,date);
		List<Orders> list = query2.list();
		
		if(list.size()==0){
			Query<Ordersdetails> query = session.createQuery("delete from Ordersdetails od where od.useraddress=? and od.date=?");
			query.setParameter(0, useraddress);
			query.setParameter(1, date);
			query.executeUpdate();
		}
	
		tran.commit();
	}
	
	public List<Orders> selectorders(String selectorders){
		String hql = "from Orders o where o.name like '%"+selectorders+"%'";
		Session session =sessionfactory.openSession();
		Transaction tran=session.beginTransaction();
		Query<Orders> query = session.createQuery(hql, Orders.class);
		List<Orders> list = query.list();
		tran.commit();
		return list;
	}
	
	
}
