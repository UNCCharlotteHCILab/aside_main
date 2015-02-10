<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Index</title>
  <link rel="stylesheet" href="<c:url value="/css/screen.css" />" type="text/css" media="screen, projection">
  <link rel="stylesheet" href="<c:url value="/css/print.css" />" type="text/css" media="print">  
  <!--[if lt IE 8]><link rel="stylesheet" href="<c:url value="/css/ie.css" />" type="text/css" media="screen, projection"><![endif]-->
  <link rel="stylesheet" href="<c:url value="/css/goldrush.css" />" type="text/css">
</head>
<body>
<div class="container">
  <div class="span-24"id="header">
    <div class="padded">
    <h1>Accounts</h1>
    <c:if test="${not empty MESSAGE}">
      <span class="success"><c:out value="${MESSAGE}" /></span>
    </c:if>
    </div>
  </div>
  <hr />
  <div class="span-5" id="nav">
    <div class="padded">
    <p><a href="<c:url value="/accounts" />">Accounts</a><br />
    <a href="<c:url value="/logout" />">Logout <c:out value="${USER.username}" /></a></p>
    </div>
  </div>
  <div class="span-19 last" id="content">
    <div class="padded">
    <jsp:include page="${PAGE}" />
    </div>
  </div>
  <hr />
  <div class="span-24" id="footer">
    <div class="padded">
    <h5>Copyright &copy; 2009 49er Nation Banking</h5>
    </div>
  </div>
</div>
</body>
</html>