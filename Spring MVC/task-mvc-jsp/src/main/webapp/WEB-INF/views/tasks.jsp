<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Tasks</title>
</head>
<body>
<h1><c:out value="${heading}"/></h1>
<p>
    <a href="${pageContext.request.contextPath}/tasks">All</a> |
    <a href="${pageContext.request.contextPath}/tasks/search?priority=HIGH">High</a> |
    <a href="${pageContext.request.contextPath}/tasks/new">New task</a>
</p>
<table border="1" cellpadding="6">
    <tr>
        <th>Id</th>
        <th>Title</th>
        <th>Priority</th>
        <th>Done</th>
    </tr>
    <c:forEach var="task" items="${tasks}">
        <tr>
            <td><c:out value="${task.id}"/></td>
            <td><a href="${pageContext.request.contextPath}/tasks/${task.id}"><c:out value="${task.title}"/></a></td>
            <td><c:out value="${task.priority}"/></td>
            <td><c:out value="${task.completed}"/></td>
        </tr>
    </c:forEach>
</table>
<c:if test="${empty tasks}">
    <p>No tasks found.</p>
</c:if>
</body>
</html>
