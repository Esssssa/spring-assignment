<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Error</title>
</head>
<body>
<h1>Something went wrong</h1>
<p><c:out value="${message}"/></p>
<a href="${pageContext.request.contextPath}/tasks">Back to the list</a>
</body>
</html>
