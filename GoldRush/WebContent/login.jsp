<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<html>
<head>
  <title>Index</title>
  <link rel="stylesheet" href="css/screen.css" type="text/css" media="screen, projection">
  <link rel="stylesheet" href="css/print.css" type="text/css" media="print">  
  <!--[if lt IE 8]><link rel="stylesheet" href="css/ie.css" type="text/css" media="screen, projection"><![endif]-->
</head>
<body>
<div class="container">
  <div class="span-24"id="header">
    <h1>Gold Rush Bank</h1>
    <c:if test="${not empty MESSAGE}">
      <span class="error"><c:out value="${MESSAGE}" /></span>
    </c:if>
  </div>
  <hr />
  <div class="span-24 last" id="content" style="background-color: #9c9">
    <form method="post" action="<c:url value="/login" />">
      <fieldset>
      <legend>Log in Here</legend>
      <p><label for="username">Username:</label><br />
      <input type="text" name="username" id="username" /><br />
      <label for="password">Password:</label><br />
      <input type="password" name="password" id="password"/><br />
      <input type="submit" value="Login" />
      </p>
      </fieldset>
    </form>
  </div>
  <hr />
  <div class="span-24" id="footer">
    <h5>Copyright &copy; 2009 49er Nation Banking</h5>
  </div>
</div>
</body>
</html>