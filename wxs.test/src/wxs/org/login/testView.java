package wxs.org.login;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.effect.Shadow;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class testView {
	private static ObservableList<HBox> list;
	public static void set(ObservableList<HBox> list) {
		testView.list = list;
	}
	public static void show(String message) {
		list.add(createHbox(message,Site.LEFT));
	}
	 public static HBox createHbox(String message,Site site) {
	    	HBox hbox = new HBox(1);
	    	hbox.getChildren().addAll(new Text(message));
	    	if(Site.isRight(site)) {
	    		hbox.setAlignment(Pos.BASELINE_RIGHT);
	    	}
	    	else if (Site.isCenter(site)) {
	    		hbox.setAlignment(Pos.BASELINE_CENTER);
			}
	    	else {
				hbox.setAlignment(Pos.BASELINE_LEFT);
			}
	    	return hbox;
	    }
}
