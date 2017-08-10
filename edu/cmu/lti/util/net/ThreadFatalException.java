package edu.cmu.lti.util.net;

public class ThreadFatalException extends Exception {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	public ThreadFatalException(String s, Throwable t) {
		super(s, t);
	}

	public ThreadFatalException(String s) {
		super(s);
	}

	public ThreadFatalException(Throwable t) {
		super(t);
	}

	public ThreadFatalException() {
		super();
	}
}
