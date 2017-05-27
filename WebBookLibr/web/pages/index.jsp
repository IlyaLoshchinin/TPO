<%@ page import="org.apache.derby.client.am.Decimal" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<html>
<head>
    <%--<meta charset="utf-8">--%>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Library online</title>
    <link href="css/bootstrap.css" rel="stylesheet" type="text/css">
    <link href="css/style.css" rel="stylesheet" type="text/css">
</head>
<body>
<div class="container">
    <% response.setContentType("text/html;charset=UTF8");
        request.setCharacterEncoding("UTF8");%>

    <sql:setDataSource var="snapshot" driver="org.apache.derby.jdbc.ClientDriver"
                       url="jdbc:derby://localhost/BookDB"
    />
    <%
        String button = request.getParameter("button");
        String strKeyword = request.getParameter("keyword");


        if (strKeyword != null) {
            strKeyword = strKeyword.trim();
        }
        if ("search".equals(button) && strKeyword != null && !strKeyword.isEmpty()) { %>
    <sql:query var="result" dataSource="${snapshot}">
        SELECT a.NAME, p.TYTUL, p.ROK, p.CENA, w.NAME as wydawName
        FROM APP.autor a, APP.pozycje p, APP.wydawca w
        WHERE a.AUTID = p.AUTID AND p.WYDID = w.WYDID
        AND ( a.NAME LIKE '%<%= strKeyword%>%'
        OR p.TYTUL LIKE '%<%= strKeyword%>%'
        OR cast(p.ROK as CHAR(100)) LIKE '<%= strKeyword%>%'
        <% try {
            Float floatParam = Float.parseFloat(strKeyword);
        %>
        OR cast(p.Cena as FLOAT) = <%= floatParam%>
        OR cast(cast(p.CENA as INT) as CHAR(100)) LIKE '<%= strKeyword%>%'
        <% } catch (Exception ex) {%>
        OR cast(cast(p.CENA as INT) as CHAR(100)) LIKE '<%= strKeyword%>%'
        <% } %>
        OR w.NAME LIKE '%<%= strKeyword%>%')
    </sql:query>
    <% } else if ("view_all".equals(button)) { %>
    <sql:query var="result" dataSource="${snapshot}">
        SELECT a.NAME, p.TYTUL, p.ROK, p.CENA, w.NAME as wydawName
        FROM APP.autor a, APP.pozycje p, APP.wydawca w
        WHERE p.AUTID = a.AUTID AND p.WYDID = w.WYDID
    </sql:query>
    <%}%>

    <div class="row">
        <div class="col-xs-12">
            <h1 class="text-center text-uppercase text-area">Library online</h1>
            <div class="col-sm-10 col-sm-offset-1">
                <form class="form form-search" name="libraryBook" action="index.jsp" method="post">
                    <div class="form-group">
                        <div class="col-xs-8">
                            <input type="search" name="keyword" class="form-control" BookDB
                                   placeholder="Find a book by keyword">
                        </div>
                        <div class="col-xs-4">
                            <button class="btn btn-primary" name="button" value="search" type="submit">Search</button>
                            <button class="btn btn-default" name="button" value="view_all" type="submit">View All
                            </button>
                        </div>

                    </div>
                </form>

            </div>

        </div>
    </div>
    <div class="row">
        <div class="col-xs-12">
            <% String keyword = String.valueOf(request.getParameter("keyword")).trim(); %>
            <% if (!keyword.equals("null") && !keyword.equals("")) { %>
            <p>Result for keyword: <%= request.getParameter("keyword") %>
            </p>
            <% } %>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-12">

            <table class="table table-bordered table-hover">


                <tr>
                    <th>Full Name</th>
                    <th>Title</th>
                    <th>Year</th>
                    <th>Price</th>
                    <th>Published in print</th>
                </tr>

                <c:forEach var="row" items="${result.rowsByIndex}">
                    <tr>
                        <td><c:out value="${row[0]}"/></td>
                        <td><c:out value="${row[1]}"/></td>
                        <td><c:out value="${row[2]}"/></td>
                        <td><c:out value="${row[3]}"/></td>
                        <td><c:out value="${row[4]}"/></td>
                    </tr>
                </c:forEach>

            </table>


        </div>
    </div>

</div>

<script src="js/mark.min.js" type="text/javascript" charset="UTF-8"></script>
<script>
    var instance = new Mark(document.querySelector("table.table-bordered"));
    instance.mark("<%= strKeyword%>");
</script>
</body>
</html>
