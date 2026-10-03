<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Task</title>
</head>
<body>
<h1>Task <c:out value="${task.id}"/></h1>
<p>Title: <c:out value="${task.title}"/></p>
<p>Priority: <c:out value="${task.priority}"/></p>
<p>Completed: <c:out value="${task.completed}"/></p>
<a href="${pageContext.request.contextPath}/tasks">Back to the list</a>
</body>
</html>
