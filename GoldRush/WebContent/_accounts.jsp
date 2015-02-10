<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<table border="1" cellpadding="2" cellspacing="0">
<tr>
  <th>Account</th>
  <th>Actions</th>
</tr>
<c:forEach items="${ACCOUNTS}" var="account">
<c:url value="/transactions" var="transactionUrl">
<c:param name="account" value="${account.accountNumber}" />
</c:url>
<c:url value="/transfer" var="transferUrl">
<c:param name="account" value="${account.accountNumber}" />
</c:url>
<tr>
<td><c:out value="${account.nickname}" /></td>
<td><a href="${transactionUrl}">Transactions</a>
<c:if test="${USER.role == 'CUSTOMER'}">
|<a href="${transferUrl}">Transfer</a>
</c:if>
</td>
</tr>
</c:forEach>
</table>