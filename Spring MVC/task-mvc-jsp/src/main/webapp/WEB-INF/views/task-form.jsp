<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>New task</title>
</head>
<body>
<h1>New task</h1>
<form:form method="post" action="${pageContext.request.contextPath}/tasks" modelAttribute="task">
    <p>
        Title: <form:input path="title"/>
        <form:errors path="title" cssStyle="color:red"/>
    </p>
    <p>
        Priority:
        <form:select path="priority">
            <form:option value="" label="-- choose --"/>
            <form:options items="${priorities}"/>
        </form:select>
        <form:errors path="priority" cssStyle="color:red"/>
    </p>
    <p>
        Completed: <form:checkbox path="completed"/>
    </p>
    <button type="submit">Save</button>
</form:form>
<a href="${pageContext.request.contextPath}/tasks">Back to the list</a>
</body>
</html>
