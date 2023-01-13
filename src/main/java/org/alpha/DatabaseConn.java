package org.alpha;

import java.sql.*;
import java.util.HashMap;
import java.lang.String;
import java.util.Map;

public class DatabaseConn {

    private final String url = "jdbc:postgresql://localhost/StockData";
    private final String user = "postgres";
    private final String password = "root";
    private static String INSERT_QUERY_SQL = null;
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void push(Connection conn, HashMap data, String tagid, int case_no, String table_name) throws SQLException {
        switch (case_no) {
            case 0:
                INSERT_QUERY_SQL = "INSERT INTO "+table_name +
                        "  (tagid,marketcap,currentprice,highlow,stockpe,bookvalue,dividendyield,roce,roe,facevalue) VALUES " +
                        " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                break;
            case 1:
                String key = data.keySet().toArray()[0].toString();
                Map m = ((Map)data.get(key));
                String cols = String.join(",", m.keySet()).replaceAll("[^a-zA-Z0-9,]", "").toLowerCase();
                INSERT_QUERY_SQL = "INSERT INTO "+table_name +
                        "  (tagid, header,"+cols+") VALUES " +
                        " (?, ?"+ ", ?".repeat(m.size())+");";
                break;
            default:
                break;
        }
        try(PreparedStatement preparedStatement = conn.prepareStatement(INSERT_QUERY_SQL);){
            int ind;
            if(case_no != 0) {
                Map map;
                for (Object key : data.keySet()) {
                    ind = 3;
                    preparedStatement.setString(1, tagid);
                    preparedStatement.setString(2, key.toString());
                    map = ((Map) data.get(key.toString()));
                    for (Object mkey : map.keySet()) {
                        preparedStatement.setString(ind, map.get(mkey).toString());
                        ind++;
                    }
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }else{
                ind = 2;
                preparedStatement.setString(1, tagid);
                for(Object key: data.keySet()){
                    preparedStatement.setString(ind, data.get(key).toString());
                    ind++;
                }
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
