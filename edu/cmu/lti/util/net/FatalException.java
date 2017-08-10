package edu.cmu.lti.util.net;

public class FatalException extends Exception {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	public FatalException(String s, Throwable t) {
		super(s, t);
	}

	public FatalException(String s) {
		super(s);
	}

	public FatalException(Throwable t) {
		super(t);
	}

	public FatalException() {
		super();
	}
}
