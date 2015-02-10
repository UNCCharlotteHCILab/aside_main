<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<h2>Transactions for account <c:out value="${ACCOUNT.nickname}" /></h2>
<table border="0" cellpadding="2" cellspacing="0">
<tr>
  <th>Date</th>
  <th>Payee</th>
  <th>Amount</th>
</tr>
<c:forEach items="${TRANSACTIONS}" var="transaction" varStatus="i">
<c:choose>
  <c:when test="${i.count % 2 == 0}">
    <c:set var="style" scope="page" value="light" />
  </c:when>
  <c:otherwise>
    <c:set var="style" scope="page" value="dark" />
  </c:otherwise>
</c:choose>
<tr class="${style}">
<td><fmt:formatDate value="${transaction.date}" pattern="yyyy-MM-dd" /></td>
<td><c:out value="${transaction.payee}" /></td>
<td>$<fmt:formatNumber value="${transaction.amount}" pattern="#,##0.00" /></td>
</tr>
</c:forEach>
</table>