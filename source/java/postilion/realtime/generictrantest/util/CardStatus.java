package postilion.realtime.generictrantest.util;

import postilion.realtime.generictrantest.database.DBHandler;
import postilion.realtime.generictrantest.systemConstans.SystemConstants;
import postilion.realtime.sdk.eventrecorder.EventRecorder;
import postilion.realtime.sdk.message.bitmap.Iso8583;
import postilion.realtime.sdk.message.bitmap.Iso8583Post;
import postilion.realtime.sdk.message.bitmap.ProcessingCode;
import postilion.realtime.sdk.message.bitmap.StructuredData;
import postilion.realtime.sdk.message.bitmap.XFieldUnableToConstruct;
import postilion.realtime.sdk.util.XPostilion;
import postilion.realtime.sdk.util.convert.Transform;

public class CardStatus extends Iso8583 {

	public Iso8583 resultCardStatus(Iso8583 p_msg, Iso8583Post msgToTM, boolean enableLog, String process) {

		StructuredData field_structured = new StructuredData();

		try {
			// Se obtiene el codigo de proceso (ProcessingCode)
			ProcessingCode ps = new ProcessingCode(p_msg.getField(Iso8583.Bit._003_PROCESSING_CODE));

			// Obtener la informacion de la tarjeta usando el procedimiento almacenado
			DBHandler.getClientData(ps.toString(), p_msg.getTrack2Data().getPan(), ps.getFromAccount(),
					p_msg.getTrack2Data().getExpiryDate(), SystemConstants.DEFAULT_ACCOUNT_INPUT, field_structured);

			boolean resultCardStatus = processCardStatus(field_structured);

			// Establecer tipo de mensaje de respuesta
			p_msg.putMsgType(Iso8583.MsgType._0210_TRAN_REQ_RSP);

			// Logica para procesar el codigo de respuesta y la informacion de la tarjeta
			if (p_msg.isFieldSet(Iso8583.Bit._102_ACCOUNT_ID_1)) {
				String primerasDosPosiciones = p_msg.getField(Iso8583.Bit._102_ACCOUNT_ID_1).toString().substring(0, 2);

				if (SystemConstants.TWO_ZEROS.equals(primerasDosPosiciones)) {
					// Caso: "00" y bloqueada o no bloqueada
					p_msg.putField(Iso8583.Bit._039_RSP_CODE, Iso8583.RspCode._00_SUCCESSFUL);
					field_structured.put(SystemConstants.KEY_NOV_CAPA, SystemConstants.VALUE_NOV_CAPA);
					String cleanCustomerId = getFieldValue(field_structured, SystemConstants.KEY_CUSTOMER_ID,
							SystemConstants.DEFAULT_VALUE_CUSTOMER_ID).replaceFirst("^PC0*", "");
					field_structured.put(SystemConstants.KEY_CUSTOMER_ID, cleanCustomerId);
					constructMsgToTm(p_msg, msgToTM, field_structured);
				} else if (resultCardStatus) {
					// Caso: diferente de "00" y no bloqueada
					p_msg.putField(Iso8583.Bit._039_RSP_CODE, Iso8583.RspCode._00_SUCCESSFUL);
					field_structured.put(SystemConstants.KEY_NOV_CAPA, SystemConstants.VALUE_NOV_CAPA);
					String cleanCustomerId = getFieldValue(field_structured, SystemConstants.KEY_CUSTOMER_ID,
							SystemConstants.DEFAULT_VALUE_CUSTOMER_ID).replaceFirst("^PC0*", "");
					field_structured.put(SystemConstants.KEY_CUSTOMER_ID, cleanCustomerId);
					constructMsgToTm(p_msg, msgToTM, field_structured);
					DBHandler.updateHoldResponseCode(p_msg.getTrack2Data().getPan(),
							p_msg.getTrack2Data().getExpiryDate(), primerasDosPosiciones);
				} else {
					// Caso: diferente de "00" y bloqueada
					p_msg.putField(Iso8583.Bit._039_RSP_CODE, Iso8583.RspCode._36_RESTRICTED_CARD_PICK_UP);
				}
			}

		} catch (XFieldUnableToConstruct e) {
			EventRecorder.recordEvent(e);
		} catch (XPostilion e) {
			EventRecorder.recordEvent(e);
		} catch (Exception e) {
			EventRecorder.recordEvent(e);
		}
		return p_msg;
	}

	public boolean processCardStatus(StructuredData field_structured) {
		try {

			// Se recupera la informacion necesaria del campo estructurado
			String holdResponseCode = getFieldValue(field_structured, SystemConstants.HOLDRSPCODE,
					SystemConstants.SPACE);

			// Se valida el estado de la tarjeta usando los datos obtenidos
			if (holdResponseCode == null || holdResponseCode.equals("NULL")) {
				return true; // Tarjeta no bloqueada
			}

		} catch (Exception e) {
			EventRecorder.recordEvent(e);
		}
		return false; // Tarjeta bloqueada
	}

	private String getFieldValue(StructuredData fieldStructured, String key, String defaultValue) {
		return fieldStructured.get(key) != null ? fieldStructured.get(key) : defaultValue;
	}

	public void constructMsgToTm(Iso8583 p_msgIso, Iso8583Post msgToTM, StructuredData field_structured) {

		try {

			msgToTM.putMsgType(Iso8583.MsgType._0220_TRAN_ADV);

			msgToTM.putField(Iso8583.Bit._003_PROCESSING_CODE, p_msgIso.getField(Iso8583.Bit._003_PROCESSING_CODE));
			if (p_msgIso.isFieldSet(Iso8583.Bit._004_AMOUNT_TRANSACTION))
				msgToTM.putField(Iso8583.Bit._004_AMOUNT_TRANSACTION,
						p_msgIso.getField(Iso8583.Bit._004_AMOUNT_TRANSACTION).toString());
			if (p_msgIso.isFieldSet(Iso8583.Bit._007_TRANSMISSION_DATE_TIME))
				msgToTM.putField(Iso8583.Bit._007_TRANSMISSION_DATE_TIME,
						p_msgIso.getField(Iso8583.Bit._007_TRANSMISSION_DATE_TIME).toString());

			if (p_msgIso.isFieldSet(Iso8583.Bit._011_SYSTEMS_TRACE_AUDIT_NR))
				msgToTM.putField(Iso8583.Bit._011_SYSTEMS_TRACE_AUDIT_NR,
						p_msgIso.getField(Iso8583.Bit._011_SYSTEMS_TRACE_AUDIT_NR).toString());

			if (p_msgIso.isFieldSet(Iso8583.Bit._012_TIME_LOCAL))
				msgToTM.putField(Iso8583.Bit._012_TIME_LOCAL,
						p_msgIso.getField(Iso8583.Bit._012_TIME_LOCAL).toString());

			if (p_msgIso.isFieldSet(Iso8583.Bit._013_DATE_LOCAL))
				msgToTM.putField(Iso8583.Bit._013_DATE_LOCAL,
						p_msgIso.getField(Iso8583.Bit._013_DATE_LOCAL).toString());

			if (p_msgIso.isFieldSet(Iso8583.Bit._015_DATE_SETTLE))
				msgToTM.putField(Iso8583.Bit._015_DATE_SETTLE,
						p_msgIso.getField(Iso8583.Bit._015_DATE_SETTLE).toString());

			if (p_msgIso.isFieldSet(Iso8583.Bit._017_DATE_CAPTURE))
				msgToTM.putField(Iso8583.Bit._017_DATE_CAPTURE,
						p_msgIso.getField(Iso8583.Bit._017_DATE_CAPTURE).toString());

			if (p_msgIso.isFieldSet(Iso8583.Bit._022_POS_ENTRY_MODE))
				msgToTM.putField(Iso8583.Bit._022_POS_ENTRY_MODE,
						p_msgIso.getField(Iso8583.Bit._022_POS_ENTRY_MODE).toString());
			else
				msgToTM.putField(Iso8583.Bit._022_POS_ENTRY_MODE, SystemConstants.POS_ENTRY_MODE);

			if (p_msgIso.isFieldSet(Iso8583.Bit._032_ACQUIRING_INST_ID_CODE))
				msgToTM.putField(Iso8583.Bit._032_ACQUIRING_INST_ID_CODE,
						p_msgIso.getField(Iso8583.Bit._032_ACQUIRING_INST_ID_CODE).toString()
								.replace(SystemConstants.SPACE, SystemConstants.ONE_ZERO));

			if (p_msgIso.isFieldSet(Iso8583.Bit._035_TRACK_2_DATA)) {
				msgToTM.putField(Iso8583.Bit._035_TRACK_2_DATA, p_msgIso.getField(Iso8583.Bit._035_TRACK_2_DATA)
						.toString().replace(SystemConstants.SPACE, SystemConstants.ONE_ZERO));
			} else {
				msgToTM.putField(Iso8583.Bit._035_TRACK_2_DATA, SystemConstants.DEFAULT_TRACK_2_DATA);
			}

			if (p_msgIso.isFieldSet(Iso8583.Bit._037_RETRIEVAL_REF_NR))
				msgToTM.putField(Iso8583.Bit._037_RETRIEVAL_REF_NR,
						p_msgIso.getField(Iso8583.Bit._037_RETRIEVAL_REF_NR).toString());

			if (p_msgIso.isFieldSet(Iso8583.Bit._041_CARD_ACCEPTOR_TERM_ID)) {
				msgToTM.putField(Iso8583.Bit._041_CARD_ACCEPTOR_TERM_ID,
						p_msgIso.getField(Iso8583.Bit._041_CARD_ACCEPTOR_TERM_ID).substring(0, 8));
			}

			msgToTM.putField(Iso8583.Bit._042_CARD_ACCEPTOR_ID_CODE, SystemConstants.DEFAULT_P42);

			if (p_msgIso.isFieldSet(Iso8583.Bit._043_CARD_ACCEPTOR_NAME_LOC))
				msgToTM.putField(Iso8583.Bit._043_CARD_ACCEPTOR_NAME_LOC,
						p_msgIso.getField(Iso8583.Bit._043_CARD_ACCEPTOR_NAME_LOC).toString());

			if (p_msgIso.isFieldSet(Iso8583.Bit._048_ADDITIONAL_DATA))
				msgToTM.putField(Iso8583.Bit._048_ADDITIONAL_DATA,
						p_msgIso.getField(Iso8583.Bit._048_ADDITIONAL_DATA).toString());

			if (p_msgIso.isFieldSet(Iso8583.Bit._049_CURRENCY_CODE_TRAN))
				msgToTM.putField(Iso8583.Bit._049_CURRENCY_CODE_TRAN,
						p_msgIso.getField(Iso8583.Bit._049_CURRENCY_CODE_TRAN).toString());

			if (p_msgIso.isFieldSet(Iso8583.Bit._052_PIN_DATA))
				msgToTM.putField(Iso8583.Bit._052_PIN_DATA,
						Transform.fromHexToBin(p_msgIso.getField(Iso8583.Bit._052_PIN_DATA)));

			msgToTM.putField(Iso8583.Bit._100_RECEIVING_INST_ID_CODE, SystemConstants.INSTITUTION_ID);

			if (p_msgIso.isFieldSet(Iso8583.Bit._102_ACCOUNT_ID_1))
				msgToTM.putField(Iso8583.Bit._102_ACCOUNT_ID_1,
						p_msgIso.getField(Iso8583.Bit._102_ACCOUNT_ID_1).toString());

			msgToTM.putField(Iso8583.Bit._039_RSP_CODE,
					p_msgIso.getField(Iso8583.Bit._102_ACCOUNT_ID_1).toString().substring(0, 2));

			if (p_msgIso.getField(Iso8583.Bit._102_ACCOUNT_ID_1).toString().substring(0, 2)
					.equals(SystemConstants.TWO_ZEROS)) {
				field_structured.put(SystemConstants.KEY_NOVEDAD, SystemConstants.VALUE_NOVEDAD_DESBLOQUEO);
			} else {
				field_structured.put(SystemConstants.KEY_NOVEDAD, SystemConstants.VALUE_NOVEDAD_BLOQUEO);
			}

			if (p_msgIso.isFieldSet(Iso8583.Bit._103_ACCOUNT_ID_2))
				msgToTM.putField(Iso8583.Bit._103_ACCOUNT_ID_2,
						p_msgIso.getField(Iso8583.Bit._103_ACCOUNT_ID_2).toString());

			msgToTM.putField(Iso8583.Bit._025_POS_CONDITION_CODE, Iso8583.PosCondCode._00_NORMAL_PRESENTMENT);
			msgToTM.putField(Iso8583.Bit._026_POS_PIN_CAPTURE_CODE, SystemConstants.POS_PIN_CAPTURE_CODE);
			msgToTM.putField(Iso8583Post.Bit._123_POS_DATA_CODE, SystemConstants.POS_DATA_CODE);
			msgToTM.putField(Iso8583.Bit._098_PAYEE, SystemConstants.PAYEE);

			String key = SystemConstants.TYPE_MESSAGE_0220.concat(p_msgIso.getField(Iso8583.Bit._037_RETRIEVAL_REF_NR))
					.concat(p_msgIso.getField(Iso8583.Bit._013_DATE_LOCAL))
					.concat(p_msgIso.getField(Iso8583.Bit._012_TIME_LOCAL))
					.concat(p_msgIso.getField(Iso8583.Bit._011_SYSTEMS_TRACE_AUDIT_NR));

			// 127.2 SWITCHKEY
			msgToTM.putPrivField(Iso8583Post.PrivBit._002_SWITCH_KEY, key);

			msgToTM.putStructuredData(field_structured);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
