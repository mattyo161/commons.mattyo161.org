package org.mattyo161.commons.mail;

import java.util.regex.Pattern;

public class EmailValidator {
	private static Pattern pattern = Pattern.compile("^[a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+(\\.[a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z]{2,6}$");
	
	
	public static boolean validate(String email) {
		if (email != null) {
			return pattern.matcher(email).matches();
		} else {
			return false;
		}
	}
}
