<%@ page import="org.eclipse.help.servlet.*" errorPage="err.jsp" contentType="text/html; charset=UTF-8"%>

<% 
	// calls the utility class to initialize the application
	application.getRequestDispatcher("/servlet/org.eclipse.help.servlet.InitServlet").include(request,response);
%>

<%
 String  ContentStr = WebappResources.getString("Content", request);
 String  SearchStr = WebappResources.getString("SearchResults", request);
 String  LinksStr = WebappResources.getString("Links", request);
 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--
 (c) Copyright IBM Corp. 2000, 2002.
 All Rights Reserved.
-->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<style type="text/css">

BODY {
	font: 8pt Tahoma;
	background:black;
	margin:0px;
	padding-bottom:1px;
	padding-left:1px;
	padding-right:1px;
}

DIV {
	background:#D4D0C8;
}

TABLE {
	background:#D4D0C8;
	font:8pt Tahoma;
	font-weight:bold;
}

 
</style>

<script language="Javascript">
// workaround for netscape resize bug
window.onresize = function (evt) { location.reload(); };
</script>

</head>

<body leftmargin="1" topmargin="1" bottommargin="1" marginheight="0" marginwidth="0">

<table id="toolbarTable"  cellpading=0 cellspacing=0 border=0 width="100%" height="100%" nowrap>
<tr border=1>
<td align=left valign=center ><div id="navTitle" name="navTitle" style="position:relative; text-indent:4px; font-weight:bold;"> &nbsp;<%=WebappResources.getString("Content", request)%> </div></td>
<td align=right ><a  href="#" onclick="parent.showBookshelf(this); this.blur();" onmouseover="window.status='<%=WebappResources.getString("Bookshelf", request)%>';return true;" onmouseout="window.status='';"><img src="../images/home_nav.gif" alt='<%=WebappResources.getString("Bookshelf", request)%>' border="0" ></a>&nbsp;</td>
</tr>
</table>	
<DIV style="width : 3000px; height : 1px; top : 0px; left : 0px;
  position : absolute;
  z-index : 2;
  visibility : visible;
" id="topBorder"><IMG src="../images/blackdot.gif" height="1" width="3000"></DIV>
<DIV style="width : 3000px; height : 1px; top : 25px; left : 0px;
  position : absolute;
  z-index : 2;
  visibility : visible;
" id="bottomBorder"><IMG src="../images/blackdot.gif" height="1" width="3000"></DIV>
</body>
</html>