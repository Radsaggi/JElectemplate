package database;

import java.sql.*;

public class DatabaseConnection {

    public DatabaseConnection() {
    }

    public void openConnection() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, SQLException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/schoolelections", "root", "");
    }

    public void updateVotes(String id) throws Exception {
        String query = "select Votes from candidates where CID ='" + id + "'";
        Object[][] obj = getRecords(query);
        int val = Integer.parseInt(obj[1][0].toString()) + 1;

        Object obj2[][] = new Object[][]{{"candidates", null},
            {"CID", id},
            {"Votes", val}};
        update(obj2);
    }

    public int insert(Object obj[][]) {
        int c = 0;
        String Query = "insert into " + obj[0][0].toString() + " values (";
        for (int i = 1; i < obj.length - 1; i++) {
            Query += "?,";
        }
        Query += "?)";
        try {
            prest = con.prepareStatement(Query);
            for (int i = 1; i < obj.length; i++) {
                prest.setObject(i, obj[i][1]);
            }
            System.out.println(Query);
            c = prest.executeUpdate();
        } catch (SQLException eet) {
            eet.printStackTrace(System.err);
        }
        return c;
    }

    public void closeConnection() throws SQLException {
        con.close();

    }

    public int update(Object obj[][]) {
        int c = 0, i = 0;
        String Query = "UPDATE " + obj[0][0].toString() + " set ";
        for (i = 2; i < obj.length - 1; i++) {
            Query = Query + obj[i][0] + "=?,";
        }
        Query = Query + obj[i][0] + "=? where " + obj[1][0] + "=?";
        try {
            prest = con.prepareStatement(Query);
            for (i = 2; i < obj.length; i++) {
                prest.setObject(i - 1, obj[i][1]);
            }
            prest.setObject(i - 1, obj[1][1]);
            System.out.println(Query);
            c = prest.executeUpdate();
        } catch (SQLException eet) {
            eet.printStackTrace(System.err);
        }
        return c;
    }

    public int delete(Object obj[][]) {
        int c = 0, i = 0;
        String Query = "DELETE FROM " + obj[0][0].toString() + " where ";
        Query = Query + obj[1][0] + "=?";
        try {
            prest = con.prepareStatement(Query);
            prest.setObject(1, obj[1][1]);
            System.out.println(Query);
            c = prest.executeUpdate();
        } catch (SQLException eet) {
            eet.printStackTrace(System.err);
        }
        return c;
    }

    public Object[][] getRecords(String Query) {
        Object data[][] = null;
        int c = 0;
        try {
            try {
                int rows = 0;
                Statement st = con.createStatement();
                Statement st1 = con.createStatement();
                rs = st.executeQuery(Query);
                rs1 = st1.executeQuery(Query);
                while (rs1.next()) {
                    rows++;
                }
                rs1.close();
                int size = rs.getMetaData().getColumnCount();
                String colNames[] = new String[size];
                for (int i = 0; i < size; i++) {
                    colNames[i] = rs.getMetaData().getColumnName(i + 1);
                }
                data = new Object[rows + 1][colNames.length];
                System.arraycopy(colNames, 0, data[0], 0, colNames.length);
                c++;
                while (rs.next()) {
                    for (int j = 0; j < colNames.length; j++) {
                        data[c][j] = rs.getObject(j + 1);
                    }
                    c++;
                }

            } catch (SQLException s) {
                System.out.println(s);
            }
        } catch (Exception eee) {
            System.out.println(eee);
        }

        return data;
    }
    private Connection con = null;
    private PreparedStatement prest;
    private ResultSet rs = null, rs1 = null;
}
