package fi.aalto.cse.hedwig.activity;

public interface ResultAppender {
	public void appendCommand(String value);
	public void updateUI(String flag);
	public void displayError(String msg);
}
