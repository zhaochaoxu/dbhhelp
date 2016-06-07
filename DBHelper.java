package com.kaishengit.tools;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;

public class DBHelper<T> {
	Connection conn = null;
	PreparedStatement stat = null;
	ResultSet rs = null;
	String url;

	public DBHelper(String url) {
		this.url = url;
	}

	public void getStat(String sql) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url);
			stat = conn.prepareStatement(sql);
		} catch (Exception e) {
			System.out.println("连接失败！");
			e.printStackTrace();
		}
	}

	public int doUpdate(String sql, Object... par) {
		getStat(sql);
		try {
			for (int i = 1; i <= par.length; i++) {
				stat.setObject(i, par[i - 1]);
			}
			return stat.executeUpdate();
		} catch (SQLException e) {
			System.out.println("增删除方法出错！");
			e.printStackTrace();
			return 0;
		} finally {
			close();
		}
	}

	public T QueryOne(String sql, BuildEntity<T> be, Object... par) {  
		//  RowMap<T> rw = new Mybulid()
		
		getStat(sql);
		try {
			for (int i = 0; i < par.length; i++) {
				stat.setObject(i + 1, par[i]);
			}
			rs = stat.executeQuery();
			if (rs.next()) {
				T st = be.build(rs);//T 
				return st;
				//return rw.build(rs);		
			}
			else{
				return null;	
			}
				
		} catch (SQLException e) {
			System.out.println("查询方法出错！");
			e.printStackTrace();
			return null;
		} finally {
			close();
		}
	}

	public List<T> QueryAll(String sql, BuildEntity<T> be, Object... par) {
		getStat(sql);
		try {
			for (int i = 0; i < par.length; i++) {
				stat.setObject(i + 1, par[i]);
			}
			rs = stat.executeQuery();
			List<T> list = new ArrayList<>();
			while (rs != null && rs.next()) {
				list.add(be.build(rs));
			}
			return list;
		} catch (SQLException e) {
			System.out.println("查询方法出错！");
			e.printStackTrace();
			return null;
		} finally {
			close();
		}

	}

	public void close() {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stat != null) {
					stat.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
	}

}
