package edu.cmu.lti.util.html;

import edu.cmu.lti.util.html.FColor.RGB;

/**
 * http://colorbrewer2.org/
 * http://www.graphviz.org/doc/info/colors.html
 * @author nlao
 *
 */

public enum EColorScheme {

	//public static ColorScheme rdbu7 = new ColorScheme(new String[]{
	//rdbu7(new String[]{"#b2182b","#ef8a62","#fddbc7","#f7f7f7","#d1e5f0","#67a9cf","#2166ac"})//, 3)
	//,rdylgn11(new String[]{"#a50026","#d73027","#f46d43","#fdae61"
	//,"#fee08b","#ffffbf","#d9ef8b","#a6d96a","#66bd63","#1a9850","006837"})//,5)
		
	BrBG3(new String[]{"#D8B365","#F5F5F5","#5AB4AC"})
	,BrBG4(new String[]{"#A6611A","#DFC27D","#80CDC1","#018571"})
	,BrBG5(new String[]{"#A6611A","#DFC27D","#F5F5F5","#80CDC1","#018571"})
	,BrBG6(new String[]{"#8C510A","#D8B365","#F6E8C3","#C7EAE5","#5AB4AC","#01665E"})
	,BrBG7(new String[]{"#8C510A","#D8B365","#F6E8C3","#F5F5F5","#C7EAE5","#5AB4AC","#01665E"})
	,BrBG8(new String[]{"#8C510A","#BF812D","#DFC27D","#F6E8C3","#C7EAE5","#80CDC1","#35978F","#01665E"})
	,BrBG9(new String[]{"#8C510A","#BF812D","#DFC27D","#F6E8C3","#F5F5F5","#C7EAE5","#80CDC1","#35978F","#01665E"})
	,BrBG10(new String[]{"#543005","#8C510A","#BF812D","#DFC27D","#F6E8C3","#C7EAE5","#80CDC1","#35978F","#01665E","#003C30"})
	,BrBG11(new String[]{"#543005","#8C510A","#BF812D","#DFC27D","#F6E8C3","#F5F5F5","#C7EAE5","#80CDC1","#35978F","#01665E","#003C30"})
	,PiYG3(new String[]{"#E9A3C9","#F7F7F7","#A1D76A"})
	,PiYG4(new String[]{"#D01C8B","#F1B6DA","#B8E186","#4DAC26"})
	,PiYG5(new String[]{"#D01C8B","#F1B6DA","#F7F7F7","#B8E186","#4DAC26"})
	,PiYG6(new String[]{"#C51B7D","#E9A3C9","#FDE0EF","#E6F5D0","#A1D76A","#4D9221"})
	,PiYG7(new String[]{"#C51B7D","#E9A3C9","#FDE0EF","#F7F7F7","#E6F5D0","#A1D76A","#4D9221"})
	,PiYG8(new String[]{"#C51B7D","#DE77AE","#F1B6DA","#FDE0EF","#E6F5D0","#B8E186","#7FBC41","#4D9221"})
	,PiYG9(new String[]{"#C51B7D","#DE77AE","#F1B6DA","#FDE0EF","#F7F7F7","#E6F5D0","#B8E186","#7FBC41","#4D9221"})
	,PiYG10(new String[]{"#8E0152","#C51B7D","#DE77AE","#F1B6DA","#FDE0EF","#E6F5D0","#B8E186","#7FBC41","#4D9221","#276419"})
	,PiYG11(new String[]{"#8E0152","#C51B7D","#DE77AE","#F1B6DA","#FDE0EF","#F7F7F7","#E6F5D0","#B8E186","#7FBC41","#4D9221","#276419"})
	,PRGn3(new String[]{"#AF8DC3","#F7F7F7","#7FBF7B"})
	,PRGn4(new String[]{"#7B3294","#C2A5CF","#A6DBA0","#008837"})
	,PRGn5(new String[]{"#7B3294","#C2A5CF","#F7F7F7","#A6DBA0","#008837"})
	,PRGn6(new String[]{"#762A83","#AF8DC3","#E7D4E8","#D9F0D3","#7FBF7B","#1B7837"})
	,PRGn7(new String[]{"#762A83","#AF8DC3","#E7D4E8","#F7F7F7","#D9F0D3","#7FBF7B","#1B7837"})
	,PRGn8(new String[]{"#762A83","#9970AB","#C2A5CF","#E7D4E8","#D9F0D3","#A6DBA0","#5AAE61","#1B7837"})
	,PRGn9(new String[]{"#762A83","#9970AB","#C2A5CF","#E7D4E8","#F7F7F7","#D9F0D3","#A6DBA0","#5AAE61","#1B7837"})
	,PRGn10(new String[]{"#40004B","#762A83","#9970AB","#C2A5CF","#E7D4E8","#D9F0D3","#A6DBA0","#5AAE61","#1B7837","#00441B"})
	,PRGn11(new String[]{"#40004B","#762A83","#9970AB","#C2A5CF","#E7D4E8","#F7F7F7","#D9F0D3","#A6DBA0","#5AAE61","#1B7837","#00441B"})
	,PuOr3(new String[]{"#F1A340","#F7F7F7","#998EC3"})
	,PuOr4(new String[]{"#E66101","#FDB863","#B2ABD2","#5E3C99"})
	,PuOr5(new String[]{"#E66101","#FDB863","#F7F7F7","#B2ABD2","#5E3C99"})
	,PuOr6(new String[]{"#B35806","#F1A340","#FEE0B6","#D8DAEB","#998EC3","#542788"})
	,PuOr7(new String[]{"#B35806","#F1A340","#FEE0B6","#F7F7F7","#D8DAEB","#998EC3","#542788"})
	,PuOr8(new String[]{"#B35806","#E08214","#FDB863","#FEE0B6","#D8DAEB","#B2ABD2","#8073AC","#542788"})
	,PuOr9(new String[]{"#B35806","#E08214","#FDB863","#FEE0B6","#F7F7F7","#D8DAEB","#B2ABD2","#8073AC","#542788"})
	,PuOr10(new String[]{"#7F3B08","#B35806","#E08214","#FDB863","#FEE0B6","#D8DAEB","#B2ABD2","#8073AC","#542788","#2D004B"})
	,PuOr11(new String[]{"#7F3B08","#B35806","#E08214","#FDB863","#FEE0B6","#F7F7F7","#D8DAEB","#B2ABD2","#8073AC","#542788","#2D004B"})
	,RdBu3(new String[]{"#EF8A62","#F7F7F7","#67A9CF"})
	,RdBu4(new String[]{"#CA0020","#F4A582","#92C5DE","#0571B0"})
	,RdBu5(new String[]{"#CA0020","#F4A582","#F7F7F7","#92C5DE","#0571B0"})
	,RdBu6(new String[]{"#B2182B","#EF8A62","#FDDBC7","#D1E5F0","#67A9CF","#2166AC"})
	,RdBu7(new String[]{"#B2182B","#EF8A62","#FDDBC7","#F7F7F7","#D1E5F0","#67A9CF","#2166AC"})
	,RdBu8(new String[]{"#B2182B","#D6604D","#F4A582","#FDDBC7","#D1E5F0","#92C5DE","#4393C3","#2166AC"})
	,RdBu9(new String[]{"#B2182B","#D6604D","#F4A582","#FDDBC7","#F7F7F7","#D1E5F0","#92C5DE","#4393C3","#2166AC"})
	,RdBu10(new String[]{"#67001F","#B2182B","#D6604D","#F4A582","#FDDBC7","#D1E5F0","#92C5DE","#4393C3","#2166AC","#053061"})
	,RdBu11(new String[]{"#67001F","#B2182B","#D6604D","#F4A582","#FDDBC7","#F7F7F7","#D1E5F0","#92C5DE","#4393C3","#2166AC","#053061"})
	,RdGy3(new String[]{"#EF8A62","#FFFFFF","#999999"})
	,RdGy4(new String[]{"#CA0020","#F4A582","#BABABA","#404040"})
	,RdGy5(new String[]{"#CA0020","#F4A582","#FFFFFF","#BABABA","#404040"})
	,RdGy6(new String[]{"#B2182B","#EF8A62","#FDDBC7","#E0E0E0","#999999","#4D4D4D"})
	,RdGy7(new String[]{"#B2182B","#EF8A62","#FDDBC7","#FFFFFF","#E0E0E0","#999999","#4D4D4D"})
	,RdGy8(new String[]{"#B2182B","#D6604D","#F4A582","#FDDBC7","#E0E0E0","#BABABA","#878787","#4D4D4D"})
	,RdGy9(new String[]{"#B2182B","#D6604D","#F4A582","#FDDBC7","#FFFFFF","#E0E0E0","#BABABA","#878787","#4D4D4D"})
	,RdGy10(new String[]{"#67001F","#B2182B","#D6604D","#F4A582","#FDDBC7","#E0E0E0","#BABABA","#878787","#4D4D4D","#1A1A1A"})
	,RdGy11(new String[]{"#67001F","#B2182B","#D6604D","#F4A582","#FDDBC7","#FFFFFF","#E0E0E0","#BABABA","#878787","#4D4D4D","#1A1A1A"})
	,RdYlBu3(new String[]{"#FC8D59","#FFFFBF","#91BFDB"})
	,RdYlBu4(new String[]{"#D7191C","#FDAE61","#ABD9E9","#2C7BB6"})
	,RdYlBu5(new String[]{"#D7191C","#FDAE61","#FFFFBF","#ABD9E9","#2C7BB6"})
	,RdYlBu6(new String[]{"#D73027","#FC8D59","#FEE090","#E0F3F8","#91BFDB","#4575B4"})
	,RdYlBu7(new String[]{"#D73027","#FC8D59","#FEE090","#FFFFBF","#E0F3F8","#91BFDB","#4575B4"})
	,RdYlBu8(new String[]{"#D73027","#F46D43","#FDAE61","#FEE090","#E0F3F8","#ABD9E9","#74ADD1","#4575B4"})
	,RdYlBu9(new String[]{"#D73027","#F46D43","#FDAE61","#FEE090","#FFFFBF","#E0F3F8","#ABD9E9","#74ADD1","#4575B4"})
	,RdYlBu10(new String[]{"#A50026","#D73027","#F46D43","#FDAE61","#FEE090","#E0F3F8","#ABD9E9","#74ADD1","#4575B4","#313695"})
	,RdYlBu11(new String[]{"#A50026","#D73027","#F46D43","#FDAE61","#FEE090","#FFFFBF","#E0F3F8","#ABD9E9","#74ADD1","#4575B4","#313695"})
	,RdYlGn3(new String[]{"#FC8D59","#FFFFBF","#91CF60"})
	,RdYlGn4(new String[]{"#D7191C","#FDAE61","#A6D96A","#1A9641"})
	,RdYlGn5(new String[]{"#D7191C","#FDAE61","#FFFFBF","#A6D96A","#1A9641"})
	,RdYlGn6(new String[]{"#D73027","#FC8D59","#FEE08B","#D9EF8B","#91CF60","#1A9850"})
	,RdYlGn7(new String[]{"#D73027","#FC8D59","#FEE08B","#FFFFBF","#D9EF8B","#91CF60","#1A9850"})
	,RdYlGn8(new String[]{"#D73027","#F46D43","#FDAE61","#FEE08B","#D9EF8B","#A6D96A","#66BD63","#1A9850"})
	,RdYlGn9(new String[]{"#D73027","#F46D43","#FDAE61","#FEE08B","#FFFFBF","#D9EF8B","#A6D96A","#66BD63","#1A9850"})
	,RdYlGn10(new String[]{"#A50026","#D73027","#F46D43","#FDAE61","#FEE08B","#D9EF8B","#A6D96A","#66BD63","#1A9850","#006837"})
	,RdYlGn11(new String[]{"#A50026","#D73027","#F46D43","#FDAE61","#FEE08B","#FFFFBF","#D9EF8B","#A6D96A","#66BD63","#1A9850","#006837"})
	,Spectral3(new String[]{"#FC8D59","#FFFFBF","#99D594"})
	,Spectral4(new String[]{"#D7191C","#FDAE61","#ABDDA4","#2B83BA"})
	,Spectral5(new String[]{"#D7191C","#FDAE61","#FFFFBF","#ABDDA4","#2B83BA"})
	,Spectral6(new String[]{"#D53E4F","#FC8D59","#FEE08B","#E6F598","#99D594","#3288BD"})
	,Spectral7(new String[]{"#D53E4F","#FC8D59","#FEE08B","#FFFFBF","#E6F598","#99D594","#3288BD"})
	,Spectral8(new String[]{"#D53E4F","#F46D43","#FDAE61","#FEE08B","#E6F598","#ABDDA4","#66C2A5","#3288BD"})
	,Spectral9(new String[]{"#D53E4F","#F46D43","#FDAE61","#FEE08B","#FFFFBF","#E6F598","#ABDDA4","#66C2A5","#3288BD"})
	,Spectral10(new String[]{"#9E0142","#D53E4F","#F46D43","#FDAE61","#FEE08B","#E6F598","#ABDDA4","#66C2A5","#3288BD","#5E4FA2"})
	,Spectral11(new String[]{"#9E0142","#D53E4F","#F46D43","#FDAE61","#FEE08B","#FFFFBF","#E6F598","#ABDDA4","#66C2A5","#3288BD","#5E4FA2"})
	,Accent3(new String[]{"#7FC97F","#BEAED4","#FDC086"})
	,Accent4(new String[]{"#7FC97F","#BEAED4","#FDC086","#FFFF99"})
	,Accent5(new String[]{"#7FC97F","#BEAED4","#FDC086","#FFFF99","#386CB0"})
	,Accent6(new String[]{"#7FC97F","#BEAED4","#FDC086","#FFFF99","#386CB0","#F0027F"})
	,Accent7(new String[]{"#7FC97F","#BEAED4","#FDC086","#FFFF99","#386CB0","#F0027F","#BF5B17"})
	,Accent8(new String[]{"#7FC97F","#BEAED4","#FDC086","#FFFF99","#386CB0","#F0027F","#BF5B17","#666666"})
	,Dark23(new String[]{"#1B9E77","#D95F02","#7570B3"})
	,Dark24(new String[]{"#1B9E77","#D95F02","#7570B3","#E7298A"})
	,Dark25(new String[]{"#1B9E77","#D95F02","#7570B3","#E7298A","#66A61E"})
	,Dark26(new String[]{"#1B9E77","#D95F02","#7570B3","#E7298A","#66A61E","#E6AB02"})
	,Dark27(new String[]{"#1B9E77","#D95F02","#7570B3","#E7298A","#66A61E","#E6AB02","#A6761D"})
	,Dark28(new String[]{"#1B9E77","#D95F02","#7570B3","#E7298A","#66A61E","#E6AB02","#A6761D","#666666"})
	,Paired3(new String[]{"#A6CEE3","#1F78B4","#B2DF8A"})
	,Paired4(new String[]{"#A6CEE3","#1F78B4","#B2DF8A","#33A02C"})
	,Paired5(new String[]{"#A6CEE3","#1F78B4","#B2DF8A","#33A02C","#FB9A99"})
	,Paired6(new String[]{"#A6CEE3","#1F78B4","#B2DF8A","#33A02C","#FB9A99","#E31A1C"})
	,Paired7(new String[]{"#A6CEE3","#1F78B4","#B2DF8A","#33A02C","#FB9A99","#E31A1C","#FDBF6F"})
	,Paired8(new String[]{"#A6CEE3","#1F78B4","#B2DF8A","#33A02C","#FB9A99","#E31A1C","#FDBF6F","#FF7F00"})
	,Paired9(new String[]{"#A6CEE3","#1F78B4","#B2DF8A","#33A02C","#FB9A99","#E31A1C","#FDBF6F","#FF7F00","#CAB2D6"})
	,Paired10(new String[]{"#A6CEE3","#1F78B4","#B2DF8A","#33A02C","#FB9A99","#E31A1C","#FDBF6F","#FF7F00","#CAB2D6","#6A3D9A"})
	,Paired11(new String[]{"#A6CEE3","#1F78B4","#B2DF8A","#33A02C","#FB9A99","#E31A1C","#FDBF6F","#FF7F00","#CAB2D6","#6A3D9A","#FFFF99"})
	,Paired12(new String[]{"#A6CEE3","#1F78B4","#B2DF8A","#33A02C","#FB9A99","#E31A1C","#FDBF6F","#FF7F00","#CAB2D6","#6A3D9A","#FFFF99"})
	,Pastel13(new String[]{"#FBB4AE","#B3CDE3","#CCEBC5"})
	,Pastel14(new String[]{"#FBB4AE","#B3CDE3","#CCEBC5","#DECBE4"})
	,Pastel15(new String[]{"#FBB4AE","#B3CDE3","#CCEBC5","#DECBE4","#FED9A6"})
	,Pastel16(new String[]{"#FBB4AE","#B3CDE3","#CCEBC5","#DECBE4","#FED9A6","#FFFFCC"})
	,Pastel17(new String[]{"#FBB4AE","#B3CDE3","#CCEBC5","#DECBE4","#FED9A6","#FFFFCC","#E5D8BD"})
	,Pastel18(new String[]{"#FBB4AE","#B3CDE3","#CCEBC5","#DECBE4","#FED9A6","#FFFFCC","#E5D8BD","#FDDAEC"})
	,Pastel19(new String[]{"#FBB4AE","#B3CDE3","#CCEBC5","#DECBE4","#FED9A6","#FFFFCC","#E5D8BD","#FDDAEC","#F2F2F2"})
	,Pastel23(new String[]{"#B3E2CD","#FDCDAC","#CBD5E8"})
	,Pastel24(new String[]{"#B3E2CD","#FDCDAC","#CBD5E8","#F4CAE4"})
	,Pastel25(new String[]{"#B3E2CD","#FDCDAC","#CBD5E8","#F4CAE4","#E6F5C9"})
	,Pastel26(new String[]{"#B3E2CD","#FDCDAC","#CBD5E8","#F4CAE4","#E6F5C9","#FFF2AE"})
	,Pastel27(new String[]{"#B3E2CD","#FDCDAC","#CBD5E8","#F4CAE4","#E6F5C9","#FFF2AE","#F1E2CC"})
	,Pastel28(new String[]{"#B3E2CD","#FDCDAC","#CBD5E8","#F4CAE4","#E6F5C9","#FFF2AE","#F1E2CC","#CCCCCC"})
	,Set13(new String[]{"#E41A1C","#377EB8","#4DAF4A"})
	,Set14(new String[]{"#E41A1C","#377EB8","#4DAF4A","#984EA3"})
	,Set15(new String[]{"#E41A1C","#377EB8","#4DAF4A","#984EA3","#FF7F00"})
	,Set16(new String[]{"#E41A1C","#377EB8","#4DAF4A","#984EA3","#FF7F00","#FFFF33"})
	,Set17(new String[]{"#E41A1C","#377EB8","#4DAF4A","#984EA3","#FF7F00","#FFFF33","#A65628"})
	,Set18(new String[]{"#E41A1C","#377EB8","#4DAF4A","#984EA3","#FF7F00","#FFFF33","#A65628","#F781BF"})
	,Set19(new String[]{"#E41A1C","#377EB8","#4DAF4A","#984EA3","#FF7F00","#FFFF33","#A65628","#F781BF","#999999"})
	,Set23(new String[]{"#66C2A5","#FC8D62","#8DA0CB"})
	,Set24(new String[]{"#66C2A5","#FC8D62","#8DA0CB","#E78AC3"})
	,Set25(new String[]{"#66C2A5","#FC8D62","#8DA0CB","#E78AC3","#A6D854"})
	,Set26(new String[]{"#66C2A5","#FC8D62","#8DA0CB","#E78AC3","#A6D854","#FFD92F"})
	,Set27(new String[]{"#66C2A5","#FC8D62","#8DA0CB","#E78AC3","#A6D854","#FFD92F","#E5C494"})
	,Set28(new String[]{"#66C2A5","#FC8D62","#8DA0CB","#E78AC3","#A6D854","#FFD92F","#E5C494","#B3B3B3"})
	,Set33(new String[]{"#8DD3C7","#FFFFB3","#BEBADA"})
	,Set34(new String[]{"#8DD3C7","#FFFFB3","#BEBADA","#FB8072"})
	,Set35(new String[]{"#8DD3C7","#FFFFB3","#BEBADA","#FB8072","#80B1D3"})
	,Set36(new String[]{"#8DD3C7","#FFFFB3","#BEBADA","#FB8072","#80B1D3","#FDB462"})
	,Set37(new String[]{"#8DD3C7","#FFFFB3","#BEBADA","#FB8072","#80B1D3","#FDB462","#B3DE69"})
	,Set38(new String[]{"#8DD3C7","#FFFFB3","#BEBADA","#FB8072","#80B1D3","#FDB462","#B3DE69","#FCCDE5"})
	,Set39(new String[]{"#8DD3C7","#FFFFB3","#BEBADA","#FB8072","#80B1D3","#FDB462","#B3DE69","#FCCDE5","#D9D9D9"})
	,Set310(new String[]{"#8DD3C7","#FFFFB3","#BEBADA","#FB8072","#80B1D3","#FDB462","#B3DE69","#FCCDE5","#D9D9D9","#BC80BD"})
	,Set311(new String[]{"#8DD3C7","#FFFFB3","#BEBADA","#FB8072","#80B1D3","#FDB462","#B3DE69","#FCCDE5","#D9D9D9","#BC80BD","#CCEBC5"})
	,Set312(new String[]{"#8DD3C7","#FFFFB3","#BEBADA","#FB8072","#80B1D3","#FDB462","#B3DE69","#FCCDE5","#D9D9D9","#BC80BD","#CCEBC5"})
	,Blues3(new String[]{"#DEEBF7","#9ECAE1","#3182BD"})
	,Blues4(new String[]{"#EFF3FF","#BDD7E7","#6BAED6","#2171B5"})
	,Blues5(new String[]{"#EFF3FF","#BDD7E7","#6BAED6","#3182BD","#08519C"})
	,Blues6(new String[]{"#EFF3FF","#C6DBEF","#9ECAE1","#6BAED6","#3182BD","#08519C"})
	,Blues7(new String[]{"#EFF3FF","#C6DBEF","#9ECAE1","#6BAED6","#4292C6","#2171B5","#084594"})
	,Blues8(new String[]{"#F7FBFF","#DEEBF7","#C6DBEF","#9ECAE1","#6BAED6","#4292C6","#2171B5","#084594"})
	,Blues9(new String[]{"#F7FBFF","#DEEBF7","#C6DBEF","#9ECAE1","#6BAED6","#4292C6","#2171B5","#08519C","#08306B"})
	,BuGn3(new String[]{"#E5F5F9","#99D8C9","#2CA25F"})
	,BuGn4(new String[]{"#EDF8FB","#B2E2E2","#66C2A4","#238B45"})
	,BuGn5(new String[]{"#EDF8FB","#B2E2E2","#66C2A4","#2CA25F","#006D2C"})
	,BuGn6(new String[]{"#EDF8FB","#CCECE6","#99D8C9","#66C2A4","#2CA25F","#006D2C"})
	,BuGn7(new String[]{"#EDF8FB","#CCECE6","#99D8C9","#66C2A4","#41AE76","#238B45","#005824"})
	,BuGn8(new String[]{"#F7FCFD","#E5F5F9","#CCECE6","#99D8C9","#66C2A4","#41AE76","#238B45","#005824"})
	,BuGn9(new String[]{"#F7FCFD","#E5F5F9","#CCECE6","#99D8C9","#66C2A4","#41AE76","#238B45","#006D2C","#00441B"})
	,BuPu3(new String[]{"#E0ECF4","#9EBCDA","#8856A7"})
	,BuPu4(new String[]{"#EDF8FB","#B3CDE3","#8C96C6","#88419D"})
	,BuPu5(new String[]{"#EDF8FB","#B3CDE3","#8C96C6","#8856A7","#810F7C"})
	,BuPu6(new String[]{"#EDF8FB","#BFD3E6","#9EBCDA","#8C96C6","#8856A7","#810F7C"})
	,BuPu7(new String[]{"#EDF8FB","#BFD3E6","#9EBCDA","#8C96C6","#8C6BB1","#88419D","#6E016B"})
	,BuPu8(new String[]{"#F7FCFD","#E0ECF4","#BFD3E6","#9EBCDA","#8C96C6","#8C6BB1","#88419D","#6E016B"})
	,BuPu9(new String[]{"#F7FCFD","#E0ECF4","#BFD3E6","#9EBCDA","#8C96C6","#8C6BB1","#88419D","#810F7C","#4D004B"})
	,GnBu3(new String[]{"#E0F3DB","#A8DDB5","#43A2CA"})
	,GnBu4(new String[]{"#F0F9E8","#BAE4BC","#7BCCC4","#2B8CBE"})
	,GnBu5(new String[]{"#F0F9E8","#BAE4BC","#7BCCC4","#43A2CA","#0868AC"})
	,GnBu6(new String[]{"#F0F9E8","#CCEBC5","#A8DDB5","#7BCCC4","#43A2CA","#0868AC"})
	,GnBu7(new String[]{"#F0F9E8","#CCEBC5","#A8DDB5","#7BCCC4","#4EB3D3","#2B8CBE","#08589E"})
	,GnBu8(new String[]{"#F7FCF0","#E0F3DB","#CCEBC5","#A8DDB5","#7BCCC4","#4EB3D3","#2B8CBE","#08589E"})
	,GnBu9(new String[]{"#F7FCF0","#E0F3DB","#CCEBC5","#A8DDB5","#7BCCC4","#4EB3D3","#2B8CBE","#0868AC","#084081"})
	,Greens3(new String[]{"#E5F5E0","#A1D99B","#31A354"})
	,Greens4(new String[]{"#EDF8E9","#BAE4B3","#74C476","#238B45"})
	,Greens5(new String[]{"#EDF8E9","#BAE4B3","#74C476","#31A354","#006D2C"})
	,Greens6(new String[]{"#EDF8E9","#C7E9C0","#A1D99B","#74C476","#31A354","#006D2C"})
	,Greens7(new String[]{"#EDF8E9","#C7E9C0","#A1D99B","#74C476","#41AB5D","#238B45","#005A32"})
	,Greens8(new String[]{"#F7FCF5","#E5F5E0","#C7E9C0","#A1D99B","#74C476","#41AB5D","#238B45","#005A32"})
	,Greens9(new String[]{"#F7FCF5","#E5F5E0","#C7E9C0","#A1D99B","#74C476","#41AB5D","#238B45","#006D2C","#00441B"})
	,Greys3(new String[]{"#F0F0F0","#BDBDBD","#636363"})
	,Greys4(new String[]{"#F7F7F7","#CCCCCC","#969696","#525252"})
	,Greys5(new String[]{"#F7F7F7","#CCCCCC","#969696","#636363","#252525"})
	,Greys6(new String[]{"#F7F7F7","#D9D9D9","#BDBDBD","#969696","#636363","#252525"})
	,Greys7(new String[]{"#F7F7F7","#D9D9D9","#BDBDBD","#969696","#737373","#525252","#252525"})
	,Greys8(new String[]{"#FFFFFF","#F0F0F0","#D9D9D9","#BDBDBD","#969696","#737373","#525252","#252525"})
	,Greys9(new String[]{"#FFFFFF","#F0F0F0","#D9D9D9","#BDBDBD","#969696","#737373","#525252","#252525","#000000"})
	,Oranges3(new String[]{"#FEE6CE","#FDAE6B","#E6550D"})
	,Oranges4(new String[]{"#FEEDDE","#FDBE85","#FD8D3C","#D94701"})
	,Oranges5(new String[]{"#FEEDDE","#FDBE85","#FD8D3C","#E6550D","#A63603"})
	,Oranges6(new String[]{"#FEEDDE","#FDD0A2","#FDAE6B","#FD8D3C","#E6550D","#A63603"})
	,Oranges7(new String[]{"#FEEDDE","#FDD0A2","#FDAE6B","#FD8D3C","#F16913","#D94801","#8C2D04"})
	,Oranges8(new String[]{"#FFF5EB","#FEE6CE","#FDD0A2","#FDAE6B","#FD8D3C","#F16913","#D94801","#8C2D04"})
	,Oranges9(new String[]{"#FFF5EB","#FEE6CE","#FDD0A2","#FDAE6B","#FD8D3C","#F16913","#D94801","#A63603","#7F2704"})
	,OrRd3(new String[]{"#FEE8C8","#FDBB84","#E34A33"})
	,OrRd4(new String[]{"#FEF0D9","#FDCC8A","#FC8D59","#D7301F"})
	,OrRd5(new String[]{"#FEF0D9","#FDCC8A","#FC8D59","#E34A33","#B30000"})
	,OrRd6(new String[]{"#FEF0D9","#FDD49E","#FDBB84","#FC8D59","#E34A33","#B30000"})
	,OrRd7(new String[]{"#FEF0D9","#FDD49E","#FDBB84","#FC8D59","#EF6548","#D7301F","#990000"})
	,OrRd8(new String[]{"#FFF7EC","#FEE8C8","#FDD49E","#FDBB84","#FC8D59","#EF6548","#D7301F","#990000"})
	,OrRd9(new String[]{"#FFF7EC","#FEE8C8","#FDD49E","#FDBB84","#FC8D59","#EF6548","#D7301F","#B30000","#7F0000"})
	,PuBu3(new String[]{"#ECE7F2","#A6BDDB","#2B8CBE"})
	,PuBu4(new String[]{"#F1EEF6","#BDC9E1","#74A9CF","#0570B0"})
	,PuBu5(new String[]{"#F1EEF6","#BDC9E1","#74A9CF","#2B8CBE","#045A8D"})
	,PuBu6(new String[]{"#F1EEF6","#D0D1E6","#A6BDDB","#74A9CF","#2B8CBE","#045A8D"})
	,PuBu7(new String[]{"#F1EEF6","#D0D1E6","#A6BDDB","#74A9CF","#3690C0","#0570B0","#034E7B"})
	,PuBu8(new String[]{"#FFF7FB","#ECE7F2","#D0D1E6","#A6BDDB","#74A9CF","#3690C0","#0570B0","#034E7B"})
	,PuBu9(new String[]{"#FFF7FB","#ECE7F2","#D0D1E6","#A6BDDB","#74A9CF","#3690C0","#0570B0","#045A8D","#023858"})
	,PuBuGn3(new String[]{"#ECE2F0","#A6BDDB","#1C9099"})
	,PuBuGn4(new String[]{"#F6EFF7","#BDC9E1","#67A9CF","#02818A"})
	,PuBuGn5(new String[]{"#F6EFF7","#BDC9E1","#67A9CF","#1C9099","#016C59"})
	,PuBuGn6(new String[]{"#F6EFF7","#D0D1E6","#A6BDDB","#67A9CF","#1C9099","#016C59"})
	,PuBuGn7(new String[]{"#F6EFF7","#D0D1E6","#A6BDDB","#67A9CF","#3690C0","#02818A","#016450"})
	,PuBuGn8(new String[]{"#FFF7FB","#ECE2F0","#D0D1E6","#A6BDDB","#67A9CF","#3690C0","#02818A","#016450"})
	,PuBuGn9(new String[]{"#FFF7FB","#ECE2F0","#D0D1E6","#A6BDDB","#67A9CF","#3690C0","#02818A","#016C59","#014636"})
	,PuRd3(new String[]{"#E7E1EF","#C994C7","#DD1C77"})
	,PuRd4(new String[]{"#F1EEF6","#D7B5D8","#DF65B0","#CE1256"})
	,PuRd5(new String[]{"#F1EEF6","#D7B5D8","#DF65B0","#DD1C77","#980043"})
	,PuRd6(new String[]{"#F1EEF6","#D4B9DA","#C994C7","#DF65B0","#DD1C77","#980043"})
	,PuRd7(new String[]{"#F1EEF6","#D4B9DA","#C994C7","#DF65B0","#E7298A","#CE1256","#91003F"})
	,PuRd8(new String[]{"#F7F4F9","#E7E1EF","#D4B9DA","#C994C7","#DF65B0","#E7298A","#CE1256","#91003F"})
	,PuRd9(new String[]{"#F7F4F9","#E7E1EF","#D4B9DA","#C994C7","#DF65B0","#E7298A","#CE1256","#980043","#67001F"})
	,Purples3(new String[]{"#EFEDF5","#BCBDDC","#756BB1"})
	,Purples4(new String[]{"#F2F0F7","#CBC9E2","#9E9AC8","#6A51A3"})
	,Purples5(new String[]{"#F2F0F7","#CBC9E2","#9E9AC8","#756BB1","#54278F"})
	,Purples6(new String[]{"#F2F0F7","#DADAEB","#BCBDDC","#9E9AC8","#756BB1","#54278F"})
	,Purples7(new String[]{"#F2F0F7","#DADAEB","#BCBDDC","#9E9AC8","#807DBA","#6A51A3","#4A1486"})
	,Purples8(new String[]{"#FCFBFD","#EFEDF5","#DADAEB","#BCBDDC","#9E9AC8","#807DBA","#6A51A3","#4A1486"})
	,Purples9(new String[]{"#FCFBFD","#EFEDF5","#DADAEB","#BCBDDC","#9E9AC8","#807DBA","#6A51A3","#54278F","#3F007D"})
	,RdPu3(new String[]{"#FDE0DD","#FA9FB5","#C51B8A"})
	,RdPu4(new String[]{"#FEEBE2","#FBB4B9","#F768A1","#AE017E"})
	,RdPu5(new String[]{"#FEEBE2","#FBB4B9","#F768A1","#C51B8A","#7A0177"})
	,RdPu6(new String[]{"#FEEBE2","#FCC5C0","#FA9FB5","#F768A1","#C51B8A","#7A0177"})
	,RdPu7(new String[]{"#FEEBE2","#FCC5C0","#FA9FB5","#F768A1","#DD3497","#AE017E","#7A0177"})
	,RdPu8(new String[]{"#FFF7F3","#FDE0DD","#FCC5C0","#FA9FB5","#F768A1","#DD3497","#AE017E","#7A0177"})
	,RdPu9(new String[]{"#FFF7F3","#FDE0DD","#FCC5C0","#FA9FB5","#F768A1","#DD3497","#AE017E","#7A0177","#49006A"})
	,Reds3(new String[]{"#FEE0D2","#FC9272","#DE2D26"})
	,Reds4(new String[]{"#FEE5D9","#FCAE91","#FB6A4A","#CB181D"})
	,Reds5(new String[]{"#FEE5D9","#FCAE91","#FB6A4A","#DE2D26","#A50F15"})
	,Reds6(new String[]{"#FEE5D9","#FCBBA1","#FC9272","#FB6A4A","#DE2D26","#A50F15"})
	,Reds7(new String[]{"#FEE5D9","#FCBBA1","#FC9272","#FB6A4A","#EF3B2C","#CB181D","#99000D"})
	,Reds8(new String[]{"#FFF5F0","#FEE0D2","#FCBBA1","#FC9272","#FB6A4A","#EF3B2C","#CB181D","#99000D"})
	,Reds9(new String[]{"#FFF5F0","#FEE0D2","#FCBBA1","#FC9272","#FB6A4A","#EF3B2C","#CB181D","#A50F15","#67000D"})
	,YlGn3(new String[]{"#F7FCB9","#ADDD8E","#31A354"})
	,YlGn4(new String[]{"#FFFFCC","#C2E699","#78C679","#238443"})
	,YlGn5(new String[]{"#FFFFCC","#C2E699","#78C679","#31A354","#006837"})
	,YlGn6(new String[]{"#FFFFCC","#D9F0A3","#ADDD8E","#78C679","#31A354","#006837"})
	,YlGn7(new String[]{"#FFFFCC","#D9F0A3","#ADDD8E","#78C679","#41AB5D","#238443","#005A32"})
	,YlGn8(new String[]{"#FFFFE5","#F7FCB9","#D9F0A3","#ADDD8E","#78C679","#41AB5D","#238443","#005A32"})
	,YlGn9(new String[]{"#FFFFE5","#F7FCB9","#D9F0A3","#ADDD8E","#78C679","#41AB5D","#238443","#006837","#004529"})
	,YlGnBu3(new String[]{"#EDF8B1","#7FCDBB","#2C7FB8"})
	,YlGnBu4(new String[]{"#FFFFCC","#A1DAB4","#41B6C4","#225EA8"})
	,YlGnBu5(new String[]{"#FFFFCC","#A1DAB4","#41B6C4","#2C7FB8","#253494"})
	,YlGnBu6(new String[]{"#FFFFCC","#C7E9B4","#7FCDBB","#41B6C4","#2C7FB8","#253494"})
	,YlGnBu7(new String[]{"#FFFFCC","#C7E9B4","#7FCDBB","#41B6C4","#1D91C0","#225EA8","#0C2C84"})
	,YlGnBu8(new String[]{"#FFFFD9","#EDF8B1","#C7E9B4","#7FCDBB","#41B6C4","#1D91C0","#225EA8","#0C2C84"})
	,YlGnBu9(new String[]{"#FFFFD9","#EDF8B1","#C7E9B4","#7FCDBB","#41B6C4","#1D91C0","#225EA8","#253494","#081D58"})
	,YlOrBr3(new String[]{"#FFF7BC","#FEC44F","#D95F0E"})
	,YlOrBr4(new String[]{"#FFFFD4","#FED98E","#FE9929","#CC4C02"})
	,YlOrBr5(new String[]{"#FFFFD4","#FED98E","#FE9929","#D95F0E","#993404"})
	,YlOrBr6(new String[]{"#FFFFD4","#FEE391","#FEC44F","#FE9929","#D95F0E","#993404"})
	,YlOrBr7(new String[]{"#FFFFD4","#FEE391","#FEC44F","#FE9929","#EC7014","#CC4C02","#8C2D04"})
	,YlOrBr8(new String[]{"#FFFFE5","#FFF7BC","#FEE391","#FEC44F","#FE9929","#EC7014","#CC4C02","#8C2D04"})
	,YlOrBr9(new String[]{"#FFFFE5","#FFF7BC","#FEE391","#FEC44F","#FE9929","#EC7014","#CC4C02","#993404","#662506"})
	,YlOrRd3(new String[]{"#FFEDA0","#FEB24C","#F03B20"})
	,YlOrRd4(new String[]{"#FFFFB2","#FECC5C","#FD8D3C","#E31A1C"})
	,YlOrRd5(new String[]{"#FFFFB2","#FECC5C","#FD8D3C","#F03B20","#BD0026"})
	,YlOrRd6(new String[]{"#FFFFB2","#FED976","#FEB24C","#FD8D3C","#F03B20","#BD0026"})
	,YlOrRd7(new String[]{"#FFFFB2","#FED976","#FEB24C","#FD8D3C","#FC4E2A","#E31A1C","#B10026"})
	,YlOrRd8(new String[]{"#FFFFCC","#FFEDA0","#FED976","#FEB24C","#FD8D3C","#FC4E2A","#E31A1C","#B10026"})
	,YlOrRd9(new String[]{"#FFFFCC","#FFEDA0","#FED976","#FEB24C","#FD8D3C","#FC4E2A","#E31A1C","#BD0026","#800026"})
	,hsvRdBl3(new String[]{"#FF0000","#FFFFFF","#0000FF"})
	,hsvRdBl4(new String[]{"#FF0000","#FFAAAA","#AAAAFF","#0000FF"})
	,hsvRdBl5(new String[]{"#FF0000","#FF8080","#FFFFFF","#8080FF","#0000FF"})
	,hsvRdBl6(new String[]{"#FF0000","#FF6666","#FFCCCC","#CCCCFF","#6666FF","#0000FF"})
	,hsvRdBl7(new String[]{"#FF0000","#FF5555","#FFAAAA","#FFFFFF","#AAAAFF","#5555FF","#0000FF"})
	,hsvRdBl8(new String[]{"#FF0000","#FF4949","#FF9292","#FFDBDB","#DBDBFF","#9292FF","#4949FF","#0000FF"})
	,hsvRdBl9(new String[]{"#FF0000","#FF4040","#FF8080","#FFBFBF","#FFFFFF","#BFBFFF","#8080FF","#4040FF","#0000FF"})
	,hsvRdBl10(new String[]{"#FF0000","#FF3939","#FF7171","#FFAAAA","#FFE3E3","#E3E3FF","#AAAAFF","#7171FF","#3939FF","#0000FF"})
	,hsvRdBl11(new String[]{"#FF0000","#FF3333","#FF6666","#FF9999","#FFCCCC","#FFFFFF","#CCCCFF","#9999FF","#6666FF","#3333FF","#0000FF"})
	,hsvCyMg3(new String[]{"#00FFFF","#FFFFFF","#FF00FF"})
	,hsvCyMg4(new String[]{"#00FFFF","#AAFFFF","#FFAAFF","#FF00FF"})
	,hsvCyMg5(new String[]{"#00FFFF","#80FFFF","#FFFFFF","#FF80FF","#FF00FF"})
	,hsvCyMg6(new String[]{"#00FFFF","#66FFFF","#CCFFFF","#FFCCFF","#FF66FF","#FF00FF"})
	,hsvCyMg7(new String[]{"#00FFFF","#55FFFF","#AAFFFF","#FFFFFF","#FFAAFF","#FF55FF","#FF00FF"})
	,hsvCyMg8(new String[]{"#00FFFF","#49FFFF","#92FFFF","#DBFFFF","#FFDBFF","#FF92FF","#FF49FF","#FF00FF"})
	,hsvCyMg9(new String[]{"#00FFFF","#40FFFF","#80FFFF","#BFFFFF","#FFFFFF","#FFBFFF","#FF80FF","#FF40FF","#FF00FF"})
	,hsvCyMg10(new String[]{"#00FFFF","#39FFFF","#71FFFF","#AAFFFF","#E3FFFF","#FFE3FF","#FFAAFF","#FF71FF","#FF39FF","#FF00FF"})
	,hsvCyMg11(new String[]{"#00FFFF","#33FFFF","#66FFFF","#99FFFF","#CCFFFF","#FFFFFF","#FFCCFF","#FF99FF","#FF66FF","#FF33FF","#FF00FF"})
	;
	
	
	String vsCode[]=null;
	boolean vbDark[]=null;
	RGB vRGB[]=null;
	//int iDefault=-1;
	EColorScheme(String vc[]){//, int iDefault){
		//this.iDefault=(vc.length-1)/2;//iDefault;
		this.vsCode=vc;
		vbDark= new boolean[vc.length];
		vRGB= new RGB[vc.length];
		for (int i=0;i<vc.length;++i){
			//skip white colors
			vRGB[i]= new RGB(vc[i]);
			vbDark[i]= vRGB[i].getBrightness()<0.2;
			
			if (vc[i].equals("#FFFFFF")) 
				vc[i]=null;
		}
		return;
	}
	/*public void setDefault(int iDefault){
		this.iDefault=iDefault;
	}*/
	
	public double min=-1.0;
	public double max=1.0;
	public EColorScheme setRange(double min, double max){
		bLogRange=false;
		this.min=min;
		this.max=max;
		return this;
	}
	boolean bLogRange=false;
	double minLog10;
	//double scaleLog10;
	
	public EColorScheme setLogRange(double minLog10, double min, double max){
		bLogRange=true;
		this.minLog10=minLog10;
		this.min=min;
		this.max=max;
		return this;
	}
	// the space between min and max is devided into vc.length chuncks
	public int getID(double x){
		if (bLogRange){
			if (x>0){
				x=Math.log10(x);
				x=Math.max(0,x-minLog10);
			}
			else if (x<0){
				x=Math.log10(-x);
				x=-Math.max(0,x-minLog10);
			}
		}
		int i= (int) Math.floor(vsCode.length*(x-min)/(max-min));
		if (i>=vsCode.length) i=vsCode.length-1;
		if (i<0) i=0;
		return i;
	}
	
	public String getColor(double x){
		return vsCode[getID(x)];
	}
	
	public String getBg(Double x){
		if (x==null) return null;
		return FHtml.backGround(getColor(x));
	}
	
	public String getFgBg(Double x){
		if (x==null) return null;
		int i=getID(x);
		if (!vbDark[i])return getBg(x);
			
		return null;//TODO: not working
	}
	//if (c.equals("#FFFFFF")) return null;
	//<td bgcolor=#67001f><a title=#67001f><font color=white>

}

