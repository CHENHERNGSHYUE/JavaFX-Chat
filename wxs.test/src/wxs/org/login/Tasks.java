package wxs.org.login;

public enum Tasks {
	sign_in,unsign_in,sign_up,unsign_up,modify_pwd,unmodify_pwd,show,un_show,refresh,ok;
	public static Tasks get(String s) {
		if(s.equals("sign up successfully!")) {
			return sign_up;
		}
		else if(s.equals("sign up unsuccessfully!")) {
			return unsign_up;
		} 
		else if (s.equals("sign in successfully!")) {
			return sign_in;
		}
		else if (s.equals("sign in unsuccessfully!")) {
			return unsign_in;
		}
		else if (s.equals("Change the password successfully!")) {
			return modify_pwd;
		}
		else if (s.equals("Incorrect answer!")) {
			return unmodify_pwd;
		} 
		else if (s.startsWith("@12")) {
			return show;
		}
		else if(s.equals("Not find the user!")) {
			return un_show;
		}
		return ok;
	}
}
