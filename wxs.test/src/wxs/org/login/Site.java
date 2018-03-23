package wxs.org.login;

public enum Site {
	RIGHT,LEFT,CENTER;
	public static boolean isRight(Site site) {
		return site.equals(RIGHT);
	}
	public static boolean isCenter(Site site) {
		return site.equals(CENTER);
	}
	public static boolean isLeft(Site site) {
		return site.equals(LEFT);
	}
}
