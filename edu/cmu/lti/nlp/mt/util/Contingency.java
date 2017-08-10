/*
 * Frank Lin
 * 
 */

package edu.cmu.lti.nlp.mt.util;

import java.math.*;

public class Contingency{

	public static final MathContext MC=MathContext.DECIMAL128;
	
	private static BigDecimal add(BigDecimal x,BigDecimal y){
		return x.add(y,MC);
	}
	
	private static BigDecimal subtract(BigDecimal x,BigDecimal y){
		return x.subtract(y,MC);
	}
	
	private static BigDecimal multiply(BigDecimal x,BigDecimal y){
		return x.multiply(y,MC);
	}
	
	private static BigDecimal divide(BigDecimal x,BigDecimal y){
		return x.divide(y,MC);
	}
	
	private static BigDecimal pow(BigDecimal x,int n){
		return x.pow(n,MC);
	}

	public static BigDecimal chiSquare(BigDecimal a,BigDecimal b,BigDecimal c,BigDecimal d){
		if(a.equals(BigDecimal.ZERO)){
			return BigDecimal.ZERO;
		}
		else{
			return divide(pow(subtract(multiply(a,d),multiply(b,c)),2),multiply(add(a,b),multiply(add(a,c),multiply(add(b,d),add(c,d)))));
		}
	}

	public static double chiSquare(double a,double b,double c,double d){
		return chiSquare(new BigDecimal(a),new BigDecimal(b),new BigDecimal(c),new BigDecimal(d)).doubleValue();
	}
	
	public static BigDecimal pointWiseMI(BigDecimal a,BigDecimal b,BigDecimal c,BigDecimal d){
		if(a.equals(BigDecimal.ZERO)){
			return BigDecimal.ZERO;
		}
		else{
			return new BigDecimal(Math.log(divide(multiply(a,add(a,add(b,add(c,d)))),multiply(add(a,b),add(a,c))).doubleValue()));
		}
	}
	
	public static double pointWiseMI(double a,double b,double c,double d){
		return pointWiseMI(new BigDecimal(a),new BigDecimal(b),new BigDecimal(c),new BigDecimal(d)).doubleValue();
	}
	
	public static double twoPoisson(double a,double b,double c,double d){
		return (Math.log(a)+Math.log(d))-(Math.log(b)+Math.log(c));
	}
	
	

}
