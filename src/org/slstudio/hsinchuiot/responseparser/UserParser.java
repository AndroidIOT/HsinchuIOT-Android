package org.slstudio.hsinchuiot.responseparser;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slstudio.hsinchuiot.model.User;
import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.http.ResponseParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class UserParser extends XmlResponseParser<User>{

	@Override
	public User parse(Document doc) throws IOTException {
		NodeList userNodeList = doc.getElementsByTagName("LoginResponse"); 
		Element userNode = (Element) userNodeList.item(0);
		boolean isSuperUser = Boolean.parseBoolean(userNode.getElementsByTagName("super_user").item(0).getTextContent());
		String adminDomain = userNode.getElementsByTagName("admin_domain").item(0).getTextContent();
		boolean isFirstLogin = Boolean.parseBoolean(userNode.getElementsByTagName("first_login").item(0).getTextContent());
		String userID = userNode.getElementsByTagName("user_id").item(0).getTextContent();
		Date changePwdDate;
		try {
			changePwdDate = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss").parse(userNode.getElementsByTagName("change_pwd_date").item(0).getTextContent());
		} catch (Exception e) {
			throw new IOTException(-3, "format date error");
		}
		String permission = userNode.getElementsByTagName("permission").item(0).getTextContent();
		int pwdExpiredDays = Integer.parseInt(userNode.getElementsByTagName("pwd_expiry_day").item(0).getTextContent());
		
		Date serverTime;
		try {
			serverTime = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss").parse(userNode.getElementsByTagName("server_time").item(0).getTextContent());
		} catch (Exception e) {
			throw new IOTException(-3, "format date error");
		}
		String status = userNode.getElementsByTagName("Status").item(0).getTextContent();
		int pwdExpiredAlartBeforeDays = Integer.parseInt(userNode.getElementsByTagName("failpwd_alert_before_day").item(0).getTextContent());
		int licenseCount = Integer.parseInt(userNode.getElementsByTagName("license_count").item(0).getTextContent());
		
		
		User result = new User();
		result.setSuperUser(isSuperUser);
		result.setUserID(userID);
		result.setFirstLogin(isFirstLogin);
		result.setAdminDomain(adminDomain);
		result.setChangePWDDate(changePwdDate);
		
		//for debug purpose
		result.setPermission(permission);
		//result.setPermission(User.PERMISSION_USER);
		//result.setPermission(User.PERMISSION_ADMINUSER);
		
		result.setPasswordExpiryDays(pwdExpiredDays);
		result.setServerTime(serverTime);
		result.setStatus(status);
		result.setAlertBeforeDays(pwdExpiredAlartBeforeDays);
		result.setLicenseCount(licenseCount);
		
		return result;
	}

}
