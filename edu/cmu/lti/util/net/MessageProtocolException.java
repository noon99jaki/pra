package edu.cmu.lti.util.net;

public class MessageProtocolException extends Exception {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	public MessageProtocolException(String s, Throwable t) {
		super(s, t);
	}

	public MessageProtocolException(String s) {
		super(s);
	}

	public MessageProtocolException(Throwable t) {
		super(t);
	}

	public MessageProtocolException() {
		super();
	}
}
