<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<h3>Current balance for account <c:out value="${ACCOUNT.nickname}" />: $<fmt:formatNumber value="${ACCOUNT.balance}" pattern="#,##0.00" /></h3>

<form method="post" action="<c:url value="/transfer" />">
<fieldset>
<legend>Transfer Funds</legend>
<input type="hidden" name="source" value="<c:out value="${ACCOUNT.accountNumber}" />" />
<p>
<label>Source Account</label><br />
<c:out value="${ACCOUNT.nickname}" /> (<c:out value="${fn:substring(ACCOUNT.accountNumber, fn:length(ACCOUNT.accountNumber) - 4, fn:length(ACCOUNT.accountNumber))}" />)<br />
<label for="target">Target Account</label><br />
<input type="text" name="target" size="12" maxlength="12" /><br />
<label for="amount">Amount to Transfer</label><br />
$<input type="text" name="amount" size="8" maxlength="8" /><br />
<input type="submit" value="Transfer" />
</p>
</fieldset>
</form>