package edu.cmu.lti.util.net;

public class EmptyMessageException extends MessageProtocolException {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	public EmptyMessageException(String s, Throwable t) {
		super(s, t);
	}

	public EmptyMessageException(String s) {
		super(s);
	}

	public EmptyMessageException(Throwable t) {
		super(t);
	}

	public EmptyMessageException() {
		super();
	}
}
