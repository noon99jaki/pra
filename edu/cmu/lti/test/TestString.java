package edu.cmu.lti.test;

public class TestString {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.print(Double.parseDouble(" 0.9"));
		String txtEnt="[1.00]";
		System.out.println(txtEnt);
		System.out.println(txtEnt.replaceAll("\\[1.00\\]", ", "));
	}
}
