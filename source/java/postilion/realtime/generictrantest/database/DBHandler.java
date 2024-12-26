package postilion.realtime.generictrantest.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postilion.realtime.generictrantest.crypto.Crypto;
import postilion.realtime.generictrantest.model.CustomerInfo;
import postilion.realtime.generictrantest.util.Logger;
import postilion.realtime.sdk.eventrecorder.EventRecorder;
import postilion.realtime.sdk.jdbc.JdbcManager;
import postilion.realtime.sdk.message.bitmap.StructuredData;

/**
 * This class search for information in the realtime database and loads the
 * result in memory
 */
public class DBHandler {

	static final String seq_nr = "NIL";
	/*-------------------------------------------------------------------------*/

	/**
	 * El metodo se encarga de validar que la tarjeta del cliente del corresponsal
	 * exista. Si la tarjeta existe trae informacion de base de datos relacionada a
	 * la tarjeta y a su cuenta. Modifica al objeto Super almacenando la informacion
	 * encontrada en base de lo contrario genera una declinacion.
	 * 
	 * @param pCode
	 * @param pan
	 * @param typeAccountInput
	 * @param expiryDate
	 * @param accountInput
	 * @param objectValidations
	 * @throws Exception
	 */
	public static void getClientData(String pCode, String pan, String typeAccountInput, String expiryDate,
			String accountInput, StructuredData sd) throws Exception {

		String panHash = Crypto.getHashPanCNB(pan);

		CallableStatement cst = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		Connection con = null;

		try {

			con = JdbcManager.getConnection(Account.POSTCARD_DATABASE);
			stmt = con.prepareCall(StoreProcedures.GET_PINOFFSET_DATA);
			stmt.setString(1, panHash);
			stmt.setString(2, expiryDate);
			stmt.registerOutParameter(3, java.sql.Types.VARCHAR);// customer id
			stmt.registerOutParameter(4, java.sql.Types.VARCHAR);// default account type
			stmt.registerOutParameter(5, java.sql.Types.VARCHAR);// name
			stmt.registerOutParameter(6, java.sql.Types.INTEGER);// issuer number
			stmt.registerOutParameter(7, java.sql.Types.VARCHAR);// extended field
			stmt.registerOutParameter(8, java.sql.Types.VARCHAR);// sequence number
			stmt.registerOutParameter(9, java.sql.Types.CHAR);// id type
			stmt.registerOutParameter(10, java.sql.Types.VARCHAR);// PinOffset
			stmt.registerOutParameter(11, java.sql.Types.VARCHAR);// card status
			stmt.registerOutParameter(12, java.sql.Types.VARCHAR);// hold rsp code
			stmt.execute();

			String customerId = stmt.getString(3);// customer id
			String defaultAccountType = stmt.getString(4);// default account type
			String customerName = stmt.getString(5);// name
			int issuerNr = stmt.getInt(6);// issuer number
			String extendedField = stmt.getString(7);// extended field
			String sequenceNr = stmt.getString(8);// sequence number
			String idType = stmt.getString(9);// id type
			String pinOffset = stmt.getString(10);// Pinoffset
			String cardStatus = stmt.getString(11);// card status
			String holdRspCode = stmt.getString(12);// hold rsp code
			String processingCode = pCode;// p Code

			if (!(issuerNr == 0)) {

				sd.put("P_CODE", processingCode);
				sd.put("CLIENT_CARD_NR", pan);

				sd.put("CUSTOMER_ID", customerId);
				sd.put("CLIENT_CARD_CLASS", extendedField);
				sd.put("CUSTOMER_NAME", customerName);
				sd.put("CUSTOMER_ID_TYPE", idType);
				sd.put("PINOFFSET", pinOffset);
				sd.put("ISSUER", String.valueOf(issuerNr));
				sd.put("CARDSTATUS", cardStatus);
				sd.put("HOLDRSPCODE", holdRspCode);

			} else {
				sd.put("ERROR", "NO EXITE TARJETA CLIENTE");
				sd.put("ERROR_CODE", "56");
			}

			JdbcManager.commit(con, stmt, rs);
			JdbcManager.commit(con, cst, rs);
		} catch (SQLException e) {
			sd.put("ERROR", "Database Connection Failure.");
			sd.put("ERROR_CODE", "06");
			EventRecorder.recordEvent(e);
			EventRecorder.recordEvent(new Exception("SQL Ex: " + e.toString()));

		} finally {
			JdbcManager.cleanup(con, stmt, rs);
			JdbcManager.cleanup(con, cst, rs);
		}

	}

	public static void getClientDataPAN(String pCode, String pan, String typeAccountInput, String accountInput,
			StructuredData sd) throws Exception {

		String panHash = Crypto.getHashPanCNB(pan);

		CallableStatement cst = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		Connection con = null;

		try {

			con = JdbcManager.getConnection(Account.POSTCARD_DATABASE);
			stmt = con.prepareCall(StoreProcedures.GET_PINOFFSET_DATA_2);
			stmt.setString(1, panHash);
			stmt.registerOutParameter(3, java.sql.Types.VARCHAR);// customer id
			stmt.registerOutParameter(4, java.sql.Types.VARCHAR);// default account type
			stmt.registerOutParameter(5, java.sql.Types.VARCHAR);// name
			stmt.registerOutParameter(6, java.sql.Types.INTEGER);// issuer number
			stmt.registerOutParameter(7, java.sql.Types.VARCHAR);// extended field
			stmt.registerOutParameter(8, java.sql.Types.VARCHAR);// sequence number
			stmt.registerOutParameter(9, java.sql.Types.CHAR);// id type
			stmt.registerOutParameter(10, java.sql.Types.VARCHAR);// PinOffset
			stmt.registerOutParameter(11, java.sql.Types.VARCHAR);// card status
			stmt.registerOutParameter(12, java.sql.Types.VARCHAR);// hold rsp code
			stmt.execute();

			String customerId = stmt.getString(3);// customer id
			String defaultAccountType = stmt.getString(4);// default account type
			String customerName = stmt.getString(5);// name
			int issuerNr = stmt.getInt(6);// issuer number
			String extendedField = stmt.getString(7);// extended field
			String sequenceNr = stmt.getString(8);// sequence number
			String idType = stmt.getString(9);// id type
			String pinOffset = stmt.getString(10);// Pinoffset
			String cardStatus = stmt.getString(11);// card status
			String holdRspCode = stmt.getString(12);// hold rsp code
			String processingCode = pCode;// p Code

			if (!(issuerNr == 0)) {

				sd.put("P_CODE", processingCode);
				sd.put("CLIENT_CARD_NR", pan);

				sd.put("CUSTOMER_ID", customerId);
				sd.put("CLIENT_CARD_CLASS", extendedField);
				sd.put("CUSTOMER_NAME", customerName);
				sd.put("CUSTOMER_ID_TYPE", idType);
				sd.put("PINOFFSET", pinOffset);
				sd.put("ISSUER", String.valueOf(issuerNr));
				sd.put("CARDSTATUS", cardStatus);
				sd.put("HOLDRSPCODE", holdRspCode);

			} else {
				sd.put("ERROR", "NO EXITE TARJETA CLIENTE");
				sd.put("ERROR_CODE", "56");
			}

			JdbcManager.commit(con, stmt, rs);
			JdbcManager.commit(con, cst, rs);
		} catch (SQLException e) {
			sd.put("ERROR", "Database Connection Failure.");
			sd.put("ERROR_CODE", "06");
			EventRecorder.recordEvent(e);
			EventRecorder.recordEvent(new Exception("SQL Ex: " + e.toString()));

		} finally {
			JdbcManager.cleanup(con, stmt, rs);
			JdbcManager.cleanup(con, cst, rs);
		}

	}

	public static void chooseCodeForNoAccount(String pCode, StructuredData sd) {

		switch (pCode.substring(2, 4)) {
		case "20":
			sd.put("ERROR", "NO CHECK ACCOUNT");
			sd.put("ERROR_CODE", "52");
			break;
		case "10":
			sd.put("ERROR", "NO SAVINGS ACCOUNT");
			sd.put("ERROR_CODE", "53");
			break;
		default:
			sd.put("ERROR", "Cuenta No Inscrita");
			sd.put("ERROR_CODE", "53");
			break;
		}
	}

	/**
	 * Actualiza el offset de una tarjeta en la base de datos.
	 *
	 * @param issuer
	 *            El emisor de la tarjeta.
	 * @param newOffset
	 *            El nuevo offset a establecer.
	 * @param pan
	 *            El número de cuenta primaria (PAN) de la tarjeta.
	 * @param expiryDate
	 *            La fecha de expiración de la tarjeta.
	 * @param enableLog
	 *            Indica si se debe habilitar el registro de logs.
	 * @return true si se actualizó al menos un registro, false en caso contrario.
	 */
	public static boolean updateOffset(String issuer, String newOffset, String pan, String expiryDate,
			boolean enableLog) {
		boolean result = false;

		try (Connection con = JdbcManager.getConnection(Account.POSTCARD_DATABASE)) {
			String panHash = Crypto.getHashPanCNB(pan);
			Logger.logLine("panHash:" + panHash, enableLog);

			if (checkAndUpdate(con, issuer, "A", newOffset, panHash, expiryDate, enableLog))
				result = true;

			if (checkAndUpdate(con, issuer, "B", newOffset, panHash, expiryDate, enableLog))
				result = true;

			JdbcManager.commit(con);
		} catch (SQLException e) {
			EventRecorder.recordEvent(new Exception("SQL: " + e.toString()));
			EventRecorder.recordEvent(e);
		} catch (Exception e) {
			EventRecorder.recordEvent(new Exception("SQL Ex: " + e.toString()));
			EventRecorder.recordEvent(e);
		}
		return result;
	}

	/**
	 * Verifica si existe una tarjeta y actualiza su offset en la base de datos.
	 *
	 * @param con
	 *            La conexión a la base de datos.
	 * @param issuer
	 *            El emisor de la tarjeta.
	 * @param suffix
	 *            El sufijo de la tabla (A o B).
	 * @param newOffset
	 *            El nuevo offset a establecer.
	 * @param panHash
	 *            El hash del número de cuenta primaria (PAN) de la tarjeta.
	 * @param expiryDate
	 *            La fecha de expiración de la tarjeta.
	 * @param enableLog
	 *            Indica si se debe habilitar el registro de logs.
	 * @return true si se actualizó al menos un registro, false en caso contrario.
	 * @throws SQLException
	 *             Si ocurre un error al ejecutar la consulta.
	 */
	private static boolean checkAndUpdate(Connection con, String issuer, String suffix, String newOffset,
			String panHash, String expiryDate, boolean enableLog) {
		String selectQuery = String.format(Queries.SELECT_EXIST_CARD_WITH_PAN, issuer, suffix);
		try (PreparedStatement psSelect = con.prepareStatement(selectQuery)) {
			psSelect.setString(1, panHash);
			psSelect.setString(2, expiryDate);
			Logger.logLine("selectQuery: " + selectQuery, enableLog);
			Logger.logLine("CONSULTA PREVIA: " + psSelect, enableLog);
			Logger.logLine("CONSULTA PREVIA Metadata:" + psSelect.getMetaData(), enableLog);
			Logger.logLine("CONSULTA PREVIA Parametermetadata:" + psSelect.getParameterMetaData(), enableLog);
			Logger.logLine("CONSULTA PREVIA toString:" + psSelect.toString(), enableLog);

			try (ResultSet rs = psSelect.executeQuery()) {
				if (rs.next()) {
					Logger.logLine("Hay datos rs.toString():" + rs.toString(), enableLog);
					String updateQuery = String.format(Queries.UPDATE_CARD_OFFSET, issuer, suffix);
					try (PreparedStatement psUpdate = con.prepareStatement(updateQuery)) {
						Logger.logLine("updateQuery: " + updateQuery, enableLog);
						Logger.logLine("UPDATE: " + psUpdate, enableLog);
						Logger.logLine("UPDATE Metadata:" + psUpdate.getMetaData(), enableLog);
						Logger.logLine("UPDATE Parametermetadata:" + psUpdate.getParameterMetaData(), enableLog);
						Logger.logLine("UPDATE toString:" + psUpdate.toString(), enableLog);
						psUpdate.setString(1, newOffset);
						psUpdate.setString(2, panHash);
						psUpdate.setString(3, expiryDate);
						int rows = psUpdate.executeUpdate();
						Logger.logLine("UPDATE rows:" + rows, enableLog);
						return rows > 0;
					}
				}
			}
		} catch (SQLException e) {
			EventRecorder.recordEvent(new Exception("SQL: " + e.toString()));
			EventRecorder.recordEvent(e);
		} catch (Exception e) {
			EventRecorder.recordEvent(new Exception("SQL Ex: " + e.toString()));
			EventRecorder.recordEvent(e);
		}
		return false;
	}

	/**
	 * El método se encarga de obtener la información de un cliente basada en el
	 * PAN y la fecha de expiración. Si la información del cliente existe en la
	 * base de datos, se devuelve una lista de objetos CustomerInfo que contienen
	 * los detalles del cliente.
	 *
	 * @param pan
	 *            Numero de cuenta primaria (PAN) del cliente.
	 * @param expiryDate
	 *            Fecha de expiracion de la tarjeta.
	 * @return List<CustomerInfo> Una lista de objetos CustomerInfo con los detalles
	 *         del cliente.
	 * @throws Exception
	 *             Si ocurre un error de conexion a la base de datos o en la
	 *             ejecucion del procedimiento almacenado.
	 */
	public static List<CustomerInfo> getClientDataByDocument(String document) throws Exception {

		List<CustomerInfo> customerInfoList = new ArrayList<>();
		Connection con = null;
		CallableStatement stmt = null;
		ResultSet rs = null;

		try {
			con = JdbcManager.getConnection(Account.POSTCARD_DATABASE);
			stmt = con.prepareCall("{call GET_Customer_Info(?)}");
			stmt.setString(1, document);

			boolean hasResults = stmt.execute();

			while (hasResults) {
				rs = stmt.getResultSet();
				while (rs.next()) {
					CustomerInfo info = new CustomerInfo();
					info.pan = Crypto.decryptPan(rs.getString("pan"));
					info.expiryDate = rs.getString("expiry_date");
					info.customerId = rs.getString("customer_id");
					info.defaultAccountType = rs.getString("default_account_type");
					info.name = rs.getString("name");
					info.issuer = rs.getInt("issuer");
					info.extendedFields = rs.getString("extended_fields");
					info.seqNr = rs.getString("seq_nr");
					info.idType = rs.getString("id_type");
					info.pinOffset = rs.getString("pinoffset");
					info.cardStatus = rs.getString("card_status");
					info.holdRspCode = rs.getString("hold_rsp_code");
					customerInfoList.add(info);
				}
				hasResults = stmt.getMoreResults();
			}

			// Commit the transaction
			JdbcManager.commit(con, stmt, rs);

		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception("Database Connection Failure.", e);
		} finally {
			// Clean up the resources
			JdbcManager.cleanup(con, stmt, rs);
		}

		return customerInfoList;
	}

	// Metodo para ejecutar el SP UpdateHoldResponseCode
	public static void updateHoldResponseCode(String pan, String expiryDate, String newHoldRspCode) throws Exception {
		CallableStatement stmt = null;
		ResultSet rs = null;
		Connection con = null;

		try {
			// Obtener la conexion a la base de datos
			con = JdbcManager.getConnection(Account.POSTCARD_DATABASE);

			// Preparar la llamada al procedimiento almacenado
			stmt = con.prepareCall(StoreProcedures.UPDATE_HOLD_RESPONSE_CODE);
			;

			// Establecer los parametros de entrada para el procedimiento almacenado
			stmt.setString(1, pan); // PAN de la tarjeta
			stmt.setString(2, expiryDate); // Fecha de expiracion
			stmt.setString(3, newHoldRspCode); // Nuevo codigo de respuesta de hold

			// Ejecutar el procedimiento almacenado
			stmt.execute();

		} catch (SQLException e) {
			// Manejo de excepciones, registramos el error
			EventRecorder.recordEvent(e);
			throw new SQLException("Error ejecutando el procedimiento almacenado UpdateHoldResponseCode", e);
		} finally {
			// Limpieza de la conexion y statement
			JdbcManager.cleanup(con, stmt, rs);
		}
	}

	public static class Account {
		public static final int NUMBER_RESULT_ACCOUNTS = 6;
		public static final int ID = 0;
		public static final int TYPE = 1;
		public static final int CUSTOMER_ID = 2;
		public static final int CUSTOMER_NAME = 3;
		public static final int CUSTOMER_NAME_CNB = 2;
		public static final int CORRECT_PROCESSING_CODE = 4;
		public static final int PROTECTED_CARD_CLASS = 5;
		public static final int FULL_LENGHT_FIELD_102 = 18;
		public static final String TRUE = "true";
		public static final String FALSE = "false";
		public static final String NIL = "NIL";
		public static final String POSTCARD_DATABASE = "postcard";
		public static final String DEFAULT_PROCESSING_CODE = "000000";
		public static final String CUSTOMER_NO_NAME = "**CLIENTE NO ENCONTRADO**";
		public static final String NO_CARD_RECORD = "**ESTA TARJETA NO EXISTE**";
		public static final String NO_ACCOUNT_LINKED = "**ESTA TARJETA NO TIENE UNA CUENTA ASOCIADA**";
		public static final String NO_PROTECTED_CARD_CLASS = "**NO HAY CLASE ASOCIADA A ESTA TARJETA**";
		public static final char ZERO_FILLER = '0';
	}

	/**
	 * Almacena los Store Procedures utilizados en la clase
	 * 
	 * @author Cristian Cardozo
	 *
	 */
	public static class StoreProcedures {
		public static final String GET_PINOFFSET_DATA = "{call GET_PinOffset(?,?,?,?,?,?,?,?,?,?,?,?)}";
		public static final String GET_PINOFFSET_DATA_2 = "{call GET_PinOffset_Without_expDate(?,?,?,?,?,?,?,?,?,?,?)}";
		public static final String CM_LOAD_CARD_ACCOUNTS = "{call cm_load_card_accounts(?,?,?)}";
		public static final String UPDATE_HOLD_RESPONSE_CODE = "{CALL UpdateHoldResponseCode(?, ?, ?)}";
	}

	/**
	 * Define el nombre de las columnas
	 * 
	 * @author Cristian Cardozo
	 *
	 */
	public static class ColumnNames {
		public static final String ACCOUNT_TYPE = "account_type";
		public static final String ACCOUNT_ID = "account_id_encrypted";
		public static final String QUALIFIER = "account_type_qualifier";
		public static final String ACTIVE = "active";
	}

	/** Define los Queries utilizados en la clase **/
	public static class Queries {
		public static final String SELECT_EXIST_CARD_WITH_PAN = "SELECT pan FROM pc_cards_%s_%s WITH (NOLOCK) WHERE pan = ? and expiry_date = ?";
		public static final String UPDATE_CARD_OFFSET = "UPDATE pc_cards_%s_%s SET pvv_or_pin_offset = ? WHERE pan = ? and expiry_date = ?";
	}

}
