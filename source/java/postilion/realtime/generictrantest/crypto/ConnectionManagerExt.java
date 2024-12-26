package postilion.realtime.generictrantest.crypto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import postilion.realtime.sdk.ipc.SecurityManager.ConnectionManager;
import postilion.realtime.sdk.jdbc.JdbcManager;

public class ConnectionManagerExt extends ConnectionManager {
	@Override
	public void cleanup(Connection arg0, Statement arg1, ResultSet arg2) throws SQLException {
		JdbcManager.cleanup(arg0, arg1, arg2);
	}

	@Override
	public void commit(Connection arg0, Statement arg1, ResultSet arg2) throws SQLException {
		JdbcManager.commit(arg0, arg1, arg2);
	}

	@Override
	public Connection getConnection() throws SQLException {
		Connection cn = JdbcManager.getConnection("postilion_mirror2");
		return cn;
	}
}
