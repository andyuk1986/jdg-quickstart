<%@ page import="com.jboss.datagrid.monispan.web.HomePage" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="homePage" class="com.jboss.datagrid.monispan.web.HomePage" />
<%
homePage.init(request, response);
%>
<html>
<head>
    <title>Monispan Monitoring</title>
</head>
<body>
<h1>User Activity Statistics</h1>

<%
    homePage.generateStatisticsChart();
%>
<%--<img src="<%=request.getContextPath()%>/chart.jpg" height="300px" width="500px">--%>
<img src="<%=request.getContextPath()%>/chart.jpg" height="300px" width="500px">

</body>
</html>