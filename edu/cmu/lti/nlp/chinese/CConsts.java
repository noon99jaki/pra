package edu.cmu.lti.nlp.chinese;

public class CConsts {
  public static final String s_PU = "—…。，、；：？！‘’“”∶〔〕〈〉《》「」『』．（）［］｛｝";
  public static final String s_NUM = "一二三四五六七八九〇千万亿十百兆厘壹贰叁肆伍陆柒捌玖零仟拾佰４０１３５２６７８９"; 
  public static final String s_LETR = "ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ";
  public static final String s_FN = "弗厦罗丁叶邹胡崔柯朱刘彭唐张萨陈黄邱吴许谭蒋徐梁冯吕薛李苏郭王孙卢浙郑戴沈杜郝赵蔡邓廖宋毕卞曹程池范方樊费符傅葛龚韩洪侯胡姜孔林骆孟潘邱魏邬邢俞袁詹张朱庄"; 
  public static final String sOpen = "‘“〔〈《「『（［｛〖【";
  public static final String paOpen = "["+sOpen+"]";
  public static final String sClose = "’”〕〉》」』）］｝〗】";
  public static final String paClose = "["+sClose+"]";
  public static final String sPronoun ="他 她 他们 她们 它 其 它们";
  //public static final String paPNP ="["+sPNP+"]";
  public static final String sPNO ="这那";
  
}
